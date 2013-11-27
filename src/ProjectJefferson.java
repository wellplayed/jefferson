
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
public class ProjectJefferson {

	static User32 user32 = (User32) Native.loadLibrary("user32", User32.class);
	static Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
	static int readRight = 0x0010;
	
	public static int getProcessId(String window) {  
        IntByReference pid = new IntByReference(0);  
        user32.GetWindowThreadProcessId(user32.FindWindowA(null, window), pid);  
        return pid.getValue();  
    }  
	
	public static Pointer openProcess(int permissions, int pid) {  
        Pointer process = kernel32.OpenProcess(permissions, true, pid);  
        return process;  
    }
	
	public static ArrayList<Integer> searchMemory(Pointer process, int value, int value2, int start, int end){
		ArrayList<Integer> addresses = new ArrayList<Integer>();
		int divider = 64;
		for(int x=start; x<end; x += (end-start)/divider){
			IntByReference read = new IntByReference(0);
			Memory output = new Memory((end-start)/divider);
			kernel32.ReadProcessMemory(process, x, output, (end-start)/divider, read);
			for(int i=0; i<(end-start)/divider; i+=0x4){
				if(output.getInt(i)==value){
					if(output.getInt(i+0x188)==value2){
						addresses.add(x+i);
					}
				}
			}
		}
		return addresses;
	}
	
	public static int readMemory(Pointer process, int address){
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(4);
		kernel32.ReadProcessMemory(process, address, output, 4, read);
		return output.getInt(0);	
	}
	
	public static HashMap<String, Object> statListToMap(ArrayList<Stat> statsList){
		HashMap<String, Object> statsMap = new HashMap<String, Object>();
		for(Stat s : statsList){
			statsMap.put(s.getName().replace(' ', '_').toLowerCase(), s.getValue());
		}
		return statsMap;
	}
	
	public static String convertTimeToString(long time, String offset){
		int offsetHours = 0, offsetMinutes = 0, offsetSeconds = 0;
		
		if(offset.contains("h")){
			offset = offset.replace("h", ":").replace("m",":").replace("s", ":");
			offsetHours = Integer.parseInt(offset.split(":")[0]);
			offsetMinutes = Integer.parseInt(offset.split(":")[1]);
			offsetSeconds = Integer.parseInt(offset.split(":")[2]);
		}else if(offset.contains("m")){
			offset = offset.replace("m",":").replace("s", ":");
			offsetMinutes = Integer.parseInt(offset.split(":")[0]);
			offsetSeconds = Integer.parseInt(offset.split(":")[1]);
		}else if(offset.contains("s")){
			offset = offset.replace("s", ":");
			offsetSeconds = Integer.parseInt(offset.split(":")[0]);
		}
		time+= (offsetSeconds*1000) + ((offsetMinutes*1000)*60) + ((offsetHours*1000)*3600);
    	String seconds = "" + (((time/1000))%60);
    	String minutes = "" + (((time/1000))/60)%60;
    	String hours = "" + (((time/1000))/60)/60;
    	if(seconds.length() == 1) seconds = "0" + seconds;
    	if(minutes.length() == 1) minutes = "0" + minutes;
    	if(hours.length() == 1) hours = "0" + hours;
    	return hours + ":" + minutes + ":" + seconds;
    }
	
	public static void main(String[] args) throws InterruptedException {
		Scanner s = new Scanner(System.in);
		Obscene obscene;
		System.out.print("Welcome to ProjectJefferson\n1 - SPQ-NA-2\n2 - SPQ-EU-2\nPlease select a broadcast:");
		int broadcast = s.nextInt();
		if(broadcast == 1){
			obscene = new Obscene("spq-na-2", "-J7MJqvr34GTgjYChT65");
		}else{
			obscene = new Obscene("spq-eu-2", "-J7-wh2DSbt-4ppLMRpP");
		}
		Thread obsceneThread = new Thread(obscene);
		String previousStatus = "reset";
		Pointer lolprocess = null;
		int x = 0;
		Player[] leftPlayers = new Player[5];
		Player[] rightPlayers = new Player[5];
		String timeOffset = "0m0s";
		obsceneThread.start();
		while(true) {
			String status = obscene.getGameStatus();
			if(status.equals("start")){
				if(previousStatus.equals("stop")){
					int pid = getProcessId("League of Legends (TM) Client"); // get our process ID  
					lolprocess = openProcess(readRight, pid);
          PlayerLocator locator = new ExperienceAndOffsetsPlayerLocator(lolprocess);
          ArrayList<Integer> allPlayers = locator.getPlayerAddresses();
          int i = 0;
          System.out.println("Left team");
          for(Integer playerOffset: allPlayers.subList(0, 5)) {
            int thisPlayer = i++;
            leftPlayers[thisPlayer] = new Player("Left", "" + thisPlayer, playerOffset);
            System.out.println(" " + i + ": " + String.format("0x%08X", playerOffset));
          }
          i = 0;
          System.out.println("Right team");
          for(Integer playerOffset: allPlayers.subList(5, 10)) {
            int thisPlayer = i++;
            rightPlayers[thisPlayer] = new Player("Right", "" + thisPlayer, playerOffset);
            System.out.println(" " + i + ": " + String.format("0x%08X", playerOffset));
          }
				}
				if(obscene.isTimeOffsetChanged()){
					x=0;
					timeOffset = obscene.getTimeOffset();
				}
				HashMap<String, Object> gameData = new HashMap<String, Object>();
				HashMap<String, Object> leftStats = new HashMap<String, Object>();
				HashMap<String, Object> rightStats = new HashMap<String, Object>();
				HashMap<String, Object> teamData = new HashMap<String, Object>();
				HashMap<String, Object> fullLeftStats = new HashMap<String, Object>();
				HashMap<String, Object> fullRightStats = new HashMap<String, Object>();
				HashMap<String, Object> gameLog = new HashMap<String, Object>();
				for(int i=0; i<leftPlayers.length; i++){
					fullLeftStats.put("" + i, statListToMap(leftPlayers[i].getStats(kernel32, lolprocess)));
					fullRightStats.put("" + i, statListToMap(rightPlayers[i].getStats(kernel32, lolprocess)));
					leftStats.put("" + i, statListToMap(leftPlayers[i].getLoggedStats(kernel32, lolprocess)));
					rightStats.put("" + i, statListToMap(rightPlayers[i].getLoggedStats(kernel32, lolprocess)));
				}
				teamData.put("left",  fullLeftStats);
				teamData.put("right", fullRightStats);
				Stat totalLeftGold = Player.getTotalStatFromPlayers(leftPlayers, "left_gold", PlayerStats.TOTAL_GOLD, PlayerStats.DATA_TYPE.INTEGER, kernel32, lolprocess);
				Stat totalRightGold = Player.getTotalStatFromPlayers(rightPlayers, "right_gold", PlayerStats.TOTAL_GOLD, PlayerStats.DATA_TYPE.INTEGER, kernel32, lolprocess);
				gameLog.put("left", leftStats);
				gameLog.put("right", rightStats);
				gameLog.put("time", convertTimeToString(x*5000, timeOffset));
				gameLog.put(totalLeftGold.getName(), totalLeftGold.getValue());
				gameLog.put(totalRightGold.getName(), totalRightGold.getValue());
				gameData.put("game_log", gameLog);
				gameData.put("player_stats", teamData);
				System.out.println(gameData);
				obscene.queueData(gameData);
                x++;
				Thread.sleep(5000);
			}
			else if(status.equals("pause")){
				Thread.sleep(1000);
			}else if(status.equals("stop")){
				x=0;
				Thread.sleep(3000);
			}
			previousStatus = status;
		}
	}
	
}
