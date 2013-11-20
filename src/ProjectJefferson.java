
import java.util.ArrayList;
import java.util.HashMap;

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
	
	public static String convertTimeToString(long time){
    	String seconds = "" + ((time/1000)%60);
    	String minutes = "" + ((time/1000)/60)%60;
    	String hours = "" + ((time/1000)/60)/60;
    	if(seconds.length() == 1) seconds = "0" + seconds;
    	if(minutes.length() == 1) minutes = "0" + minutes;
    	if(hours.length() == 1) hours = "0" + hours;
    	return hours + ":" + minutes + ":" + seconds;
    }
	
	public static void main(String[] args) throws InterruptedException {
		Obscene obscene = new Obscene("spq-na-2");
		Thread obsceneThread = new Thread(obscene);
		String previousStatus = "stop";
		Pointer lolprocess = null;
		int baseAddress, x = 0;
		Player[] leftPlayers = new Player[5];
		Player[] rightPlayers = new Player[5];
		obsceneThread.start();
		while(true) {
			String status = obscene.getGameStatus();
			if(status.equals("start")){
				if(previousStatus.equals("stop")){
					int pid = getProcessId("League of Legends (TM) Client"); // get our process ID  
					lolprocess = openProcess(readRight, pid);
					ArrayList<Integer> addresses = searchMemory(lolprocess, 280,280, 0x30000000, 0x40000000);
					baseAddress = addresses.get(0)-0x1C;
					for(int i=0; i<leftPlayers.length; i++){
						leftPlayers[i] = new Player("Left", "" + i, baseAddress + (i*0x188));
						rightPlayers[i] = new Player("Right", "" + i, baseAddress + 0x7A8 + (i*0x188));
					}
				}
				HashMap<String, Object> gameData = new HashMap<String, Object>();
				HashMap<String, Object> leftStats = new HashMap<String, Object>();
				HashMap<String, Object> rightStats = new HashMap<String, Object>();
				HashMap<String, Object> teamData = new HashMap<String, Object>();
				for(int i=0; i<leftPlayers.length; i++){
					leftStats.put("" + i, statListToMap(leftPlayers[i].getStats(kernel32, lolprocess)));
					rightStats.put("" + i, statListToMap(rightPlayers[i].getStats(kernel32, lolprocess)));
				}
				teamData.put("left",  leftStats);
				teamData.put("right", rightStats);
				Stat totalLeftGold = Player.getTotalStatFromPlayers(leftPlayers, "left_gold", PlayerStats.TOTAL_GOLD, kernel32, lolprocess);
				Stat totalRightGold = Player.getTotalStatFromPlayers(rightPlayers, "right_gold", PlayerStats.TOTAL_GOLD, kernel32, lolprocess);
				gameData.put(totalLeftGold.getName(), totalLeftGold.getValue());
				gameData.put(totalRightGold.getName(), totalRightGold.getValue());
				gameData.put("time", convertTimeToString(x*5000));
				gameData.put("player_stats", teamData);
				System.out.println(gameData);
				obscene.queueData(gameData);
                x++;
				Thread.sleep(5000);
			}
			else if(status.equals("stop")){
				Thread.sleep(1000);
				x = 0;
			}
			else if(status.equals("pause")){
				Thread.sleep(1000);
			}
			previousStatus = status;
		}
	}
	
}
