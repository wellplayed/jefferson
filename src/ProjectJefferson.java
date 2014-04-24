
import java.io.File;
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
		Obscene obscene = null;
		SC2Calibration sc2cal = null;
		System.out.println("Welcome to ProjectJefferson");
		System.out.print("1 - League of Legends\n2 - Starcraft 2\nPlease Select a Game: ");
		String jeffersonId = "jeffTest";
		int game = s.nextInt();
		int numPlayers = 5;
		boolean doLogData;
		switch(game){
		case 1:
			System.out.print("Number of players per team: ");
			numPlayers = s.nextInt();
			break;
		case 2:
			//MemoryAccess.setProcessName("Starcraft II");
			//sc2cal = new SC2Calibration();
			break;
		default:
			System.err.println("Invalid Game");
			System.exit(0);
		}
		System.out.print("1 - Data Log On\n2 - Data Log Off\nEnable Data Log: ");
		doLogData = (s.nextInt() == 1)?true:false;
		System.out.print("\n1 - SPQ-NA-2\n2 - SPQ-EU-2\n3 - SPQ-NA-Group\n4 - Enders Cup\n5 - March Matchness\n6 - LPL Demo\nPlease select a broadcast:");
		int broadcast = s.nextInt();
		switch(broadcast){
		case 1:
			obscene = new Obscene(jeffersonId, "spq-na-2", "-J7MJqvr34GTgjYChT65", true, doLogData);
			break;
		case 2:
			obscene = new Obscene(jeffersonId, "spq-eu-2", "-J7-wh2DSbt-4ppLMRpP", true, doLogData);
			break;
		case 3:
			obscene = new Obscene(jeffersonId, "spq-na-group", "-J7MJqvr34GTgjYChT65", true, doLogData);
			break;
		case 4:
			obscene = new Obscene(jeffersonId, "enderscup", "-JFQ23F7P1S01pAmACCf", false, doLogData);
			break;
		case 5:
			obscene = new Obscene(jeffersonId, "marchmadness", "-JGzfl8xkbygLZpM8Pcv", false, doLogData);
			break;
		case 6:
			obscene = new Obscene(jeffersonId, "lpl-demo", "-JL1erLh5ey6IyPC_vTx", false, doLogData);
			break;
		default:
			System.err.println("Invalid Broadcast");
			System.exit(0);
		}
		Thread obsceneThread = new Thread(obscene);
		obsceneThread.start();
		Thread.sleep(5000);
		if(game==1){
			/* League of Legends */ 
			String previousStatus = "stop";
			int x = 0;
			Player[] leftPlayers = new Player[numPlayers];
			Player[] rightPlayers = new Player[numPlayers];
			String timeOffset = "0m0s";
			while(true) {
				String status = obscene.getGameStatus();
				//String status = "start";
				if(status.equals("start")){
					
					if(obscene.isTimeOffsetChanged()){
						x=0;
						timeOffset = obscene.getTimeOffset();
					}
					if(previousStatus.equals("stop")){
					  MemoryAccess.setProcessName("League of Legends (TM) Client");
					  ExperienceAndOffsetsPlayerLocator locator = new ExperienceAndOffsetsPlayerLocator(numPlayers);
					  //PlayerListPlayerLocator locator = new PlayerListPlayerLocator();
			          ArrayList<Integer> allPlayers = locator.getPlayerAddresses();
			          int i = 0;
			          System.out.println("Left team");
			          for(Integer playerOffset: allPlayers.subList(0, numPlayers)) {
			            int thisPlayer = i++;
			            leftPlayers[thisPlayer] = new Player("Left", "" + thisPlayer, playerOffset);
			            System.out.println(" " + i + ": " + String.format("0x%08X", playerOffset));
			          }
			          i = 0;
			          System.out.println("Right team");
			          for(Integer playerOffset: allPlayers.subList(numPlayers, numPlayers*2)) {
			            int thisPlayer = i++;
			            rightPlayers[thisPlayer] = new Player("Right", "" + thisPlayer, playerOffset);
			            System.out.println(" " + i + ": " + String.format("0x%08X", playerOffset));
			          }
					}
					long startTime = Calendar.getInstance().getTimeInMillis();
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
					gameData.put("game_time", convertTimeToString(x*5000, timeOffset));
					gameLog.put("time", convertTimeToString(x*5000, timeOffset));
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
				else if(status.equals("pause")){
					Thread.sleep(1000);
				}else if(status.equals("stop")){
					x=0;
					Thread.sleep(1000);
				}
				previousStatus = status;
			}
		}else if(game==2){
			/* Starcraft 2 */ 
			String previousStatus = "stop";
			int x = 0;
			SC2Player playerOne = null;
			SC2Player playerTwo = null;
			String timeOffset = "0m0s";
			String username = System.getProperty("user.name");
			while(true) {
				String status = obscene.getGameStatus();
				if(status.equals("start")){
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
				else if(status.equals("pause")){
					Thread.sleep(1000);
				}else if(status.equals("stop")){
					x=0;
					Thread.sleep(1000);
				}
				previousStatus = status;
			}
		}
	}
}
