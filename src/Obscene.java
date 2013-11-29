import java.util.ArrayList;
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
	public String widgetId;
	public Firebase fb;
	public Firebase activeSeries;
	public Firebase activeMatch;
	public Firebase activeMatchPlayerStats;
	public Firebase activeMatchGameLog;
	public Firebase activeMatchGameInfo;
	public String activeMatchSlug = "";
	public String status = "reset";
	public String gameTimeMark = "0m0s";
	public String timeOffset = "0m0s"; 
	public LinkedList<HashMap<String, Object>> firebaseQueue;
	public boolean timeOffsetChanged;
	public Semaphore firebaseQueueSync;

	public Obscene(String broadcastSlug, String widgetId){
		this.broadcastSlug = broadcastSlug;
		this.widgetId = widgetId;
		this.firebaseQueue = new LinkedList<HashMap<String, Object>>();
		this.firebaseQueueSync = new Semaphore(1);
		configureFirebase();
	}
	
	public void configureFirebase(){
		fb = new Firebase(FIREBASE_BASE_URL + broadcastSlug);
		fb.child("/settings/active_series_id").addValueEventListener(new ValueEventListener() {
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
			            			if(activeMatch == null || !activeMatch.toString().equals(activeSeries.toString() + "/" + i)){
			            				activeMatch = activeSeries.child("/" + i);
			            				activeMatchPlayerStats = fb.child("/match_player_stats/" + activeMatchSlug + "/" + i);
			            				activeMatchGameLog = fb.child("/match_game_log/" + activeMatchSlug + "/" + i);
			            				activeMatchGameInfo = fb.child("/match_game_info/" + activeMatchSlug + "/" + i);
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
			    		}else{
			    			status = ((Map)eventList.get(eventList.size()-1)).get("event_id").toString();
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
		
		fb.child("/widget/" + widgetId + "/config/default/game_time_mark").addValueEventListener(new ValueEventListener() {
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
	
	public void logData(HashMap<String, Object> entry){
		activeMatchPlayerStats.setValue(entry.get("player_stats"));
		activeMatchGameInfo.child("/game_time").setValue(entry.get("game_time"));
		Firebase newLogEntry = activeMatchGameLog.push();
		newLogEntry.setValue(entry.get("game_log"));
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
				firebaseQueueSync.acquire();
				boolean queueReady = !firebaseQueue.isEmpty();
				firebaseQueueSync.release();
				if(queueReady){
					firebaseQueueSync.acquire();
					HashMap<String, Object> dataToLog = firebaseQueue.pop();
					firebaseQueueSync.release();
					logData(dataToLog);
				}else{
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
