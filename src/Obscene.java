import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;


public class Obscene implements Runnable {
	
	public static final String FIREBASE_BASE_URL = "https://vin-obscene.firebaseio.com/broadcasts/";
	public String broadcastSlug;
	public String jeffersonId;
	public String widgetId;
	public Firebase fb;
	public Firebase activeSeries;
	public Firebase activeGame;
	public Firebase activeGamePlayerStats;
	public Firebase activeGameLog;
	public Firebase activeGameInfo;
	public Firebase activeMap;
	public Firebase jeffersonRedundancy;
	public String activeMatchSlug = "";
	public String activeMapName;
	public String status = "reset";
	public String gameTimeMark = "0m0s";
	public String timeOffset = "0m0s"; 
	public LinkedList<HashMap<String, Object>> firebaseQueue;
	public boolean timeOffsetChanged;
	public boolean useRemoteTime;
	public boolean doLogData;
	public boolean jeffersonUpload;
	public Semaphore firebaseQueueSync;

	public Obscene(String jeffersonId, String broadcastSlug, String widgetId, boolean useRemoteTime, boolean doLogData){
		this.broadcastSlug = broadcastSlug;
		this.jeffersonId = jeffersonId;
		this.widgetId = widgetId;
		this.firebaseQueue = new LinkedList<HashMap<String, Object>>();
		this.firebaseQueueSync = new Semaphore(1);
		this.useRemoteTime = useRemoteTime;
		this.doLogData = doLogData;
		configureFirebase();
	}
	
	public void configureFirebase(){
		fb = new Firebase(FIREBASE_BASE_URL + broadcastSlug);
		fb.child("/settings/stream_settings/stream_1/active_series_id").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	activeMatchSlug = snapshot.getValue().toString();
		        activeSeries = fb.child("/series/" + snapshot.getValue().toString() + "/matches");
		        activeSeries.addValueEventListener(new ValueEventListener() {
		            @Override
		            public void onDataChange(DataSnapshot snapshot) {
		            	ArrayList dataList = (ArrayList)snapshot.getValue();
		            	for(int i=0; i<dataList.size(); i++){
		            		if(((Map)dataList.get(i)).get("active") != null){
			            		if(((Map)dataList.get(i)).get("active").toString().equals("true")){
			            			if(activeGame == null || !activeGame.toString().equals(activeSeries.toString() + "/" + i)){
			            				activeGame = activeSeries.child("/" + i);
			            				activeGamePlayerStats = fb.child("/match_lol_player_stats/" + activeMatchSlug + "/" + i);
			            				activeGameLog = fb.child("/match_lol_game_log/" + activeMatchSlug + "/" + i);
			            				activeGameInfo = fb.child("/match_lol_game_info/" + activeMatchSlug + "/" + i);
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
			            				status = "stop";
			            				System.out.println("Active Match Changed (Series: " + activeMatchSlug + ", Match: " + i + ")");
			            				
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
		
		fb.child("/stream/1/events").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	ArrayList eventList = (ArrayList)snapshot.getValue();
		    	for(int i=eventList.size()-1; i>=0; i--){
			    	if(((Map)eventList.get(i)).get("widget_id").equals(widgetId)){
			    		String event =  ((Map)eventList.get(eventList.size()-1)).get("event_id").toString();
			    		if(event.equals("update") && !gameTimeMark.equals(timeOffset)){
			    			timeOffset = gameTimeMark;
			    			timeOffsetChanged=true;
			    			System.out.println("Game Time Updated: " + timeOffset);
			    		}else{
			    			status = ((Map)eventList.get(eventList.size()-1)).get("event_id").toString();
			    			System.out.println("Game " + status + "ed");
			    		}
			    		break;	
			    	}
		    	}
		    }

		    @Override
		    public void onCancelled() {
		        System.err.println("Listener was cancelled");
		    }
		});
		
		/*fb.child("/jefferson/status").addValueEventListener(new ValueEventListener(){
			 @Override
			    public void onDataChange(DataSnapshot snapshot) {
			    	status = snapshot.getValue().toString();
			    }

			    @Override
			    public void onCancelled() {
			        System.err.println("Listener was cancelled");
			    }
		});*/
		if(this.useRemoteTime){
			fb.child("/widget/" + widgetId + "/config/-J-rsf0DZNOFdrg1Jsdz/game_time_mark").addValueEventListener(new ValueEventListener() {
			    @Override
			    public void onDataChange(DataSnapshot snapshot) {
			    	gameTimeMark = snapshot.getValue().toString();
			    }
	
			    @Override
			    public void onCancelled() {
			        System.err.println("Listener was cancelled");
			    }
			});
		}
		
		/*fb.child("/Jefferson").addValueEventListener(new ValueEventListener(){
			@Override
		    public void onDataChange(DataSnapshot snapshot) {
				Calendar c = Calendar.getInstance();
				boolean needUpload = true;
				ArrayList jeffersonList = (ArrayList)snapshot.getValue();
		    	for(int i=0; i<jeffersonList.size(); i++){
		    		if(!((Map)jeffersonList.get(i)).get("id").equals(jeffersonId)){
		    			 if(c.getTimeInMillis() - Long.parseLong(((Map)jeffersonList.get(i)).get("time").toString()) < 500){
		    				 needUpload = false;
		    			 }
		    		}
		    	}
		    	this.
		    }

		    @Override
		    public void onCancelled() {
		        
		    }
		});*/
		
	}
	
	public boolean isTimeOffsetChanged(){
		if(timeOffsetChanged){
			timeOffsetChanged = false;
			return true;
		}
		return false;
	}
	
	public String getTimeOffset(){
		return timeOffset;
	}
	
	public String getGameStatus(){
		return status;
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
