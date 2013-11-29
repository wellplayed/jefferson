
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

public class ProjectJefferson {

	public static HashMap<String, String> statListToMap(ArrayList<StatEntry> statsList){
		HashMap<String, String> statsMap = new HashMap<String, String>();
		for(StatEntry s : statsList) {
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
		int x = 0;
		Player[] leftPlayers = new Player[5];
		Player[] rightPlayers = new Player[5];
		String timeOffset = "0m0s";
		obsceneThread.start();
		while(true) {
			String status = obscene.getGameStatus();
			if(status.equals("start")){
				if(previousStatus.equals("stop")){
				  ExperienceAndOffsetsPlayerLocator locator = new ExperienceAndOffsetsPlayerLocator();
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
				long startTime = Calendar.getInstance().getTimeInMillis();
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
					fullLeftStats.put("" + i, statListToMap(leftPlayers[i].getStats()));
					fullRightStats.put("" + i, statListToMap(rightPlayers[i].getStats()));
					leftStats.put("" + i, statListToMap(leftPlayers[i].getLoggedStats()));
					rightStats.put("" + i, statListToMap(rightPlayers[i].getLoggedStats()));
				}
				teamData.put("left",  fullLeftStats);
				teamData.put("right", fullRightStats);
				StatEntry totalLeftGold = Player.getTotalStatFromPlayers(leftPlayers, new IntStat("left_gold", PlayerStats.TOTAL_GOLD));
				StatEntry totalRightGold = Player.getTotalStatFromPlayers(rightPlayers, new IntStat("right_gold", PlayerStats.TOTAL_GOLD));
				gameLog.put("left", leftStats);
				gameLog.put("right", rightStats);
				gameData.put("game_time", convertTimeToString(x*5000, timeOffset));
				gameLog.put(totalLeftGold.getName(), totalLeftGold.getValue());
				gameLog.put(totalRightGold.getName(), totalRightGold.getValue());
				gameData.put("game_log", gameLog);
				gameData.put("player_stats", teamData);
				System.out.println(gameData);
				obscene.queueData(gameData);
				x++;
				long endTime = Calendar.getInstance().getTimeInMillis();
				if(endTime - startTime > 0){
					Thread.sleep(5000 - (endTime - startTime));
				}
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
