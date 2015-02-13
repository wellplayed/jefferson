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
		    	activeSeriesId = snapshot.getValue().toString();
		        activeSeriesMatches = fb.child("/series/" + snapshot.getValue().toString() + "/matches");
		        activeSeriesMatches.addValueEventListener(new ValueEventListener() {
		            @Override
		            public void onDataChange(DataSnapshot snapshot) {
		            	ArrayList dataList = (ArrayList)snapshot.getValue();
		            	for(int i=0; i<dataList.size(); i++){
		            		if(((Map)dataList.get(i)).get("active") != null){
			            		if(((Map)dataList.get(i)).get("active").toString().equals("true")){
			            			Boolean gameWasNull = activeGame == null;
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
			            				if(!gameWasNull) {
			            					clockState = "stopped";
			            				}
			            				gameWasNull = false;
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
		fb.child("/widget/" + this.config.get("gameWidgetId") + "/config/" + this.config.get("gameWidgetConfigId")).addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	clockState = snapshot.child("/game_clock_state").getValue().toString();
		    	gameStartTimestamp = snapshot.child("/game_start_timestamp").getValue().toString();
		    	gamePauseTimestamp = snapshot.child("/game_pause_timestamp").getValue().toString();
		    	System.out.println("clockState: " + clockState);
		    	System.out.println("gameStartTimestamp: " + gameStartTimestamp);
		    	System.out.println("gamePauseTimestamp: " + gamePauseTimestamp);
		    	System.out.println("Config snapshot: " + snapshot.getValue().toString());
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
	
	public Long getGameSeconds() {
		Long nowSeconds = Calendar.getInstance().getTimeInMillis()/1000;
		Long startTimeSeconds = new Long(gameStartTimestamp);
		Long startTimeMillis = startTimeSeconds*1000;
		Date startDate = new Date(startTimeMillis);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		
		System.out.println("nowSeconds:		 " + nowSeconds);
		System.out.println("startTimeSeconds:	 " + startTimeSeconds);
		
		if(clockState.equals("started")) {
			Long seconds = new Long((nowSeconds - startTimeSeconds));
			System.out.println("seconds:" + seconds);
			return seconds;
		}
		else if(clockState.equals("paused")) {
			long pauseTimestampLong = Long.parseLong(gamePauseTimestamp)*1000;
			Date pauseDate = new Date(pauseTimestampLong);
			Calendar pauseCalendar = Calendar.getInstance();
			pauseCalendar.setTime(pauseDate);
			return new Long((nowSeconds - startTimeSeconds - (pauseCalendar.getTimeInMillis()/1000) ));
		}
		
		return Long.parseLong("0");
	}
	
	public String getMapName(){
		return activeMapName;
	}
	
	public void logData(HashMap<String, Object> entry){
		activeGameInfo.child("/game_time").setValue(entry.get("game_time"));
		if((Integer)entry.get("upload_type") == 1){
			activeGamePlayerStats.setValue(entry.get("player_stats"));
			if(config.get("logData").equals("1")){
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
