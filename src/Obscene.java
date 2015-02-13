import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.Date;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;


public class Obscene implements Runnable {
	
	public static final String FIREBASE_BASE_URL = "https://vin-obscene.firebaseio.com/broadcasts/";
	public HashMap<String, String> config;
	public Firebase fb;
	public Firebase activeSeriesMatches;
	public Firebase activeGame;
	public Firebase activeGamePlayerStats;
	public Firebase activeGameLog;
	public Firebase activeGameInfo;
	public Firebase activeMap;
	public Firebase jeffersonRedundancy;
	public String activeSeriesId = "";
	public String activeMapName;
	public String clockState = "stopped";
	public String gameTimeMark = "0m0s";
	public String gameStartTimestamp = "0";
	public String gamePauseTimestamp = "0";
	public String timeOffset = "0m0s"; 
	public LinkedList<HashMap<String, Object>> firebaseQueue;
	public boolean timeOffsetChanged;
	public boolean useRemoteTime;
	public boolean doLogData;
	public boolean jeffersonUpload;
	public Semaphore firebaseQueueSync;

	public Obscene(HashMap<String, String> config){
		this.config = config;
		this.firebaseQueue = new LinkedList<HashMap<String, Object>>();
		this.firebaseQueueSync = new Semaphore(1);
		configureFirebase();
	}
	
	public void configureFirebase(){
		fb = new Firebase(this.config.get("firebaseBaseUrl"));
		
		//Watching for changes in active series
		fb.child("/settings/stream_settings/stream_" + this.config.get("streamId") + "/active_series_id").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	String prevGame = "";
		    	activeSeriesId = snapshot.getValue().toString();
		        activeSeriesMatches = fb.child("/series/" + snapshot.getValue().toString() + "/matches");
		        activeSeriesMatches.addValueEventListener(new ValueEventListener() {
		            @Override
		            public void onDataChange(DataSnapshot snapshot) {
		            	ArrayList dataList = (ArrayList)snapshot.getValue();
		            	for(int i=0; i<dataList.size(); i++){
		            		if(((Map)dataList.get(i)).get("active") != null){
			            		if(((Map)dataList.get(i)).get("active").toString().equals("true")){
			            			if(activeGame == null || !activeGame.toString().equals(activeSeriesMatches.toString() + "/" + i)){
			            				activeGame = activeSeriesMatches.child("/" + i);
			            				activeGamePlayerStats = fb.child("/match_lol_player_stats/" + activeSeriesId + "/" + i);
			            				activeGameLog = fb.child("/match_lol_game_log/" + activeSeriesId + "/" + i);
			            				activeGameInfo = fb.child("/match_lol_game_info/" + activeSeriesId + "/" + i);
			            				activeGame.child("/map_id").addValueEventListener(new ValueEventListener(){
											@Override
											public void onDataChange(DataSnapshot snapshot) {
												try{
												fb.child("/map/" + snapshot.getValue().toString() + "/name").addValueEventListener(new ValueEventListener(){
													@Override
													public void onDataChange(DataSnapshot snapshot) {
														activeMapName = snapshot.getValue().toString();
														System.out.println("Map: "+ activeMapName);
													}

													@Override
													public void onCancelled() {
														// TODO Auto-generated method stub
														
													}
												});
												}catch(Exception e){
													System.out.println("Map is null");
												}
												
											}

											@Override
											public void onCancelled() {
												
											}
			            					
			            				});
			            				try {
											firebaseQueueSync.acquire();
											firebaseQueue = new LinkedList<HashMap<String, Object>>();
											firebaseQueueSync.release();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
			            				clockState = "stopped";
			            				System.out.println("Active Match Changed (Series: " + activeSeriesId + ", Match_index: " + i + ")");			            				
			            			}
			            		}
		            		}
		            	}
		            }
		            @Override
		            public void onCancelled() {
		                System.err.println("Listener was cancelled");
		            }
		        });
		    }
		    @Override
		    public void onCancelled() {
		        System.err.println("Listener was cancelled");
		    }
		});
		
		//Watching for the game status to change
		fb.child("/widget/" + this.config.get("gameWidgetId") + "/config/" + this.config.get("gameWidgetConfigId") + "/game_clock_state").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	clockState = (String)snapshot.getValue().toString();
		    	System.out.println("Game clock state set to " + clockState);
		    }

		    @Override
		    public void onCancelled() {
		        System.err.println("Listener was cancelled");
		    }
		});
		
		//Watch the game's UTC start time
		fb.child("/widget/" + this.config.get("gameWidgetId") + "/config/" + this.config.get("gameWidgetConfigId") + "/game_start_timestamp").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	String val = snapshot.getValue().toString();
		    	gameStartTimestamp = val;
		    	System.out.println("gameStartTimestamp set to " + gameStartTimestamp);
		    }

		    @Override
		    public void onCancelled() {
		        System.err.println("Listener was cancelled");
		    }
		});				
		//Watch the game's UTC pause time
		fb.child("/widget/" + this.config.get("gameWidgetId") + "/config/" + this.config.get("gameWidgetConfigId") + "/game_pause_timestamp").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	String val = snapshot.getValue().toString();
		    	gamePauseTimestamp = val;
		    	System.out.println("gamePauseTimestamp set to " + gamePauseTimestamp);
		    }

		    @Override
		    public void onCancelled() {
		        System.err.println("Listener was cancelled");
		    }
		});		
	}
	
	public boolean isTimeOffsetChanged(){
		if(timeOffsetChanged){
			timeOffsetChanged = false;
			return true;
		}
		return false;
	}
	
	public String getGameStartTimestamp() {
		return gameStartTimestamp;
	}
	
	public String getTimeOffset(){
		return timeOffset;
	}
	
	public String getGameClockState(){
		return clockState;
	}
	
	public long getGameSeconds() {
		if(clockState == "stopped") {
			return Long.parseLong("0");
		}
		
		long now = Calendar.getInstance().getTimeInMillis();
		long startTimestampLong = Long.parseLong(gameStartTimestamp)*1000;
		Date startDate = new Date(startTimestampLong);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		if(clockState == "started") {
			return (startCalendar.getTimeInMillis() - now) / 1000;
		}
		else if(clockState == "paused") {
			long pauseTimestampLong = Long.parseLong(gamePauseTimestamp)*1000;
			Date pauseDate = new Date(pauseTimestampLong);
			Calendar pauseCalendar = Calendar.getInstance();
			pauseCalendar.setTime(pauseDate);
			return (startCalendar.getTimeInMillis() - pauseCalendar.getTimeInMillis() - now) / 1000;
		}

	}
	
	public String getMapName(){
		return activeMapName;
	}
	
	public void logData(HashMap<String, Object> entry){
		activeGameInfo.child("/game_time").setValue(entry.get("game_time"));
		if((Integer)entry.get("upload_type") == 1){
			activeGamePlayerStats.setValue(entry.get("player_stats"));
			if(doLogData){
				Firebase newLogEntry = activeGameLog.push();
				newLogEntry.setValue(entry.get("game_log"));
			}
		}
	}
	
	public void queueData(HashMap<String, Object> entry){
		try {
			firebaseQueueSync.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		firebaseQueue.add(entry);
		firebaseQueueSync.release();
	}

	@Override
	public void run() {
		while(true){
			try {
				long startTime = Calendar.getInstance().getTimeInMillis();
				firebaseQueueSync.acquire();
				boolean queueReady = !firebaseQueue.isEmpty();
				firebaseQueueSync.release();
				if(queueReady){
					firebaseQueueSync.acquire();
					HashMap<String, Object> dataToLog = firebaseQueue.pop();
					firebaseQueueSync.release();
					logData(dataToLog);
					long endTime = Calendar.getInstance().getTimeInMillis();
					System.out.println("Uploaded in " + (endTime - startTime) + " ms");
				}else{
					Thread.sleep(100);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
