import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
	public String status = "reset";
	public String gameTimeMark = "0m0s";
	public String timeOffset = "0m0s"; 
	public LinkedList<HashMap<String, Object>> firebaseQueue;
	public boolean timeOffsetChanged;

	public Obscene(String broadcastSlug, String widgetId){
		this.broadcastSlug = broadcastSlug;
		this.widgetId = widgetId;
		firebaseQueue = new LinkedList<HashMap<String, Object>>();
		configureFirebase();
	}
	
	public void configureFirebase(){
		fb = new Firebase(FIREBASE_BASE_URL + broadcastSlug);
		fb.child("/settings/active_series_id").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
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
			            				System.out.println(activeMatch.toString());
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
		Firebase newPlayerStats = activeMatch.child("/player_stats");
		newPlayerStats.setValue(entry.get("player_stats"));
		Firebase newLogEntry = activeMatch.child("/game_log").push();
		newLogEntry.setValue(entry.get("game_log"));
	}
	
	public void queueData(HashMap<String, Object> entry){
		firebaseQueue.add(entry);
	}

	@Override
	public void run() {
		while(true){
			if(!firebaseQueue.isEmpty()){
				logData(firebaseQueue.pop());
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
