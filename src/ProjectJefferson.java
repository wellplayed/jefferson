
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	public static void main(String[] args) throws InterruptedException, IOException {
		Scanner s = new Scanner(System.in);
		Obscene obscene = null;
		String[] requiredConfigs = {"firebaseBaseUrl","playersPerTeam","game", "gameWidgetId"};
		System.out.println("Welcome to ProjectJefferson");
		
		//FILE READING
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		System.out.println("Select config file from /configs:");
		File folder = new File("configs");
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	        System.out.println((i+1) + ". " + listOfFiles[i].getName());
	    }
	    
		int configNum = s.nextInt();
		HashMap<String, String> config = new HashMap<String, String>();		
		System.out.println("You have selected #" + configNum + ": " + listOfFiles[configNum-1].getName());
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(listOfFiles[configNum-1]));
			String line;
			while ((line = br.readLine()) != null) {
				String[] lineList = line.split("=>");
				System.out.println("Config variable '" + lineList[0] + "' set to '" + lineList[1] + "'");
				config.put(lineList[0],lineList[1]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < requiredConfigs.length; i++) {
			if(!config.containsKey(requiredConfigs[i])) {
				System.out.println("ERROR: Missing config variable '" + requiredConfigs[i] + "'");
			}
		}
		
		//Default config
		if(!config.containsKey("streamId")) {
			System.out.println("Using default streamId of 1");
			config.put("streamId", "1");
		}
		
		if(!config.containsKey("gameWidgetConfigId")) {
			System.out.println("Using default gameWidgetConfigId of default");
			config.put("gameWidgetConfigId", "default");
		}
		
		if(!config.containsKey("usesMaps")) {
			System.out.println("Using default gameWidgetConfigId of 0");
			config.put("usesMaps", "0");
		}

		if(!config.containsKey("logData")) {
			System.out.println("Using default logData of 0");
			config.put("logData", "0");
		}
		
		System.out.println("------------");
		//DONE FILE READING

		System.out.println("Game: " + config.get("game"));
		obscene = new Obscene(config);
		Thread obsceneThread = new Thread(obscene);
		obsceneThread.start();
		Thread.sleep(5000);
		System.out.println("Done sleeping.");
		Integer playersPerTeam = Integer.parseInt(config.get("playersPerTeam"));
		if(config.get("game").equals("lol")){
			System.out.println("League of Legends!");
			/* League of Legends */ 
			String previousStatus = "stopped";
			int x = 0;
			Player[] leftPlayers = new Player[playersPerTeam];
			Player[] rightPlayers = new Player[playersPerTeam];
			String timeOffset = "0m0s";
			while(true) {
				String status = obscene.getGameClockState();
				System.out.println("Status: " + status);
				if(status.equals("started")){
					if(previousStatus.equals("stopped")){
					  MemoryAccess.setProcessName("League of Legends (TM) Client");
					  ExperienceAndOffsetsPlayerLocator locator = new ExperienceAndOffsetsPlayerLocator(playersPerTeam);
			          ArrayList<Integer> allPlayers = locator.getPlayerAddresses();
			          int i = 0;
			          System.out.println("Left team");
			          for(Integer playerOffset: allPlayers.subList(0, playersPerTeam)) {
			            int thisPlayer = i++;
			            leftPlayers[thisPlayer] = new Player("Left", "" + thisPlayer, playerOffset);
			            System.out.println(" " + i + ": " + String.format("0x%08X", playerOffset));
			          }
			          i = 0;
			          System.out.println("Right team");
			          for(Integer playerOffset: allPlayers.subList(playersPerTeam, playersPerTeam*2)) {
			            int thisPlayer = i++;
			            rightPlayers[thisPlayer] = new Player("Right", "" + thisPlayer, playerOffset);
			            System.out.println(" " + i + ": " + String.format("0x%08X", playerOffset));
			          }
					}
					long startTime = Calendar.getInstance().getTimeInMillis();
					long gameSeconds = obscene.getGameSeconds();
					HashMap<String, Object> gameData = new HashMap<String, Object>();
					HashMap<String, Object> leftStats = new HashMap<String, Object>();
					HashMap<String, Object> rightStats = new HashMap<String, Object>();
					HashMap<String, Object> teamData = new HashMap<String, Object>();
					HashMap<String, Object> fullLeftStats = new HashMap<String, Object>();
					HashMap<String, Object> fullRightStats = new HashMap<String, Object>();
					HashMap<String, Object> leftPlayersMap = new HashMap<String, Object>();
					HashMap<String, Object> rightPlayersMap = new HashMap<String, Object>();
					HashMap<String, Object> gameLog = new HashMap<String, Object>();
					for(int i=0; i<leftPlayers.length; i++){
						fullLeftStats.put("" + i, statListToMap(leftPlayers[i].getStats()));
						fullRightStats.put("" + i, statListToMap(rightPlayers[i].getStats()));
						leftPlayersMap.put("" + i, statListToMap(leftPlayers[i].getLoggedStats()));
						rightPlayersMap.put("" + i, statListToMap(rightPlayers[i].getLoggedStats()));
					}
					teamData.put("left",  fullLeftStats);
					teamData.put("right", fullRightStats);
					StatEntry totalLeftGold = Player.getTotalStatFromPlayers(leftPlayers, new IntStat("gold", PlayerStats.TOTAL_GOLD));
					StatEntry totalRightGold = Player.getTotalStatFromPlayers(rightPlayers, new IntStat("gold", PlayerStats.TOTAL_GOLD));
					leftStats.put(totalLeftGold.getName(), totalLeftGold.getValue());
					rightStats.put(totalRightGold.getName(), totalRightGold.getValue());
					leftStats.put("players",leftPlayersMap);
					rightStats.put("players",rightPlayersMap);
					gameLog.put("left", leftStats);
					gameLog.put("right", rightStats);
					gameData.put("game_time", convertTimeToString(gameSeconds*1000, timeOffset));
					gameLog.put("time", convertTimeToString(gameSeconds*1000, timeOffset));
					gameData.put("game_log", gameLog);
					gameData.put("player_stats", teamData);
					gameData.put("upload_type", 1);
					System.out.println(gameData);
					obscene.queueData(gameData);
					x++;
					long endTime = Calendar.getInstance().getTimeInMillis();
					if(endTime - startTime >= 0){
						Thread.sleep(5000 - (endTime - startTime));
					}
				}
				else if(status.equals("paused")){
					Thread.sleep(1000);
				}else if(status.equals("stopped")){
					x=0;
					Thread.sleep(1000);
				}
				previousStatus = status;
			}
		}else if(config.get("game")=="sc2"){
			/* Starcraft 2 */ 
			String previousStatus = "stopped";
			int x = 0;
			SC2Player playerOne = null;
			SC2Player playerTwo = null;
			String timeOffset = "0m0s";
			String username = System.getProperty("user.name");
			while(true) {
				String status = obscene.getGameClockState();
				if(status.equals("started")){
					String map = obscene.getMapName();
					long startTime = Calendar.getInstance().getTimeInMillis();
					HashMap<String, Object> gameData = new HashMap<String, Object>();
					HashMap<String, Object> gameLog = new HashMap<String, Object>();
					HashMap<String, Object> playerData = new HashMap<String, Object>();
					HashMap<String, Object> playerOneData = SC2JSONParser.parseSC2Player("C:/Users/" + username + "/Documents/StarCraft II/UserLogs/" + obscene.getMapName() + "/DataLog_Player1.txt");
					HashMap<String, Object> playerTwoData = SC2JSONParser.parseSC2Player("C:/Users/" + username + "/Documents/StarCraft II/UserLogs/" + obscene.getMapName() + "/DataLog_Player1.txt");

					playerData.put("0", playerOneData);
					playerData.put("1", playerTwoData);
					
					gameLog.put("0", playerOneData);
					gameLog.put("1", playerTwoData);
					gameLog.put("time", convertTimeToString(x*5000, timeOffset));
					
					gameData.put("game_time", convertTimeToString(x*5000, timeOffset));
					gameData.put("player_stats", playerData);
					gameData.put("game_log", gameLog);
					gameData.put("upload_type", 1);
					
					System.out.println(gameData);
					obscene.queueData(gameData);
					x++;
					long endTime = Calendar.getInstance().getTimeInMillis();
					if(endTime - startTime >= 0){
						Thread.sleep(725*5 - (endTime - startTime));
					}
				}
				else if(status.equals("paused")){
					Thread.sleep(1000);
				}else if(status.equals("stopped")){
					x=0;
					Thread.sleep(1000);
				}
				previousStatus = status;
			}
		}
	}
}
