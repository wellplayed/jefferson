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
	public Firebase fb;
	public Firebase activeSeries;
	public Firebase activeMatch;
	public String status = "stop";
	public LinkedList<HashMap<String, Object>> firebaseQueue;
	public Semaphore gameStatus;

	public Obscene(String broadcastSlug){
		this.broadcastSlug = broadcastSlug;
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
		            		if(((Map)dataList.get(i)).get("active").toString().equals("true")){
		            			if(activeMatch == null || !activeMatch.toString().equals(activeSeries.toString() + "/" + i)){
		            				activeMatch = activeSeries.child("/" + i);
		            				System.out.println(activeMatch.toString());
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
		
		fb.child("/settings/ocr_status").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	status = snapshot.getValue().toString();
		    }

		    @Override
		    public void onCancelled() {
		        System.err.println("Listener was cancelled");
		    }
		});
	}
	
	public String getGameStatus(){
		return status;
	}
	
	public void logData(HashMap<String, Object> entry){
		Firebase newLogEntry = activeMatch.child("/game_log").push();
		newLogEntry.setValue(entry);
		Firebase newPlayerStats = activeMatch.child("/player_stats");
		newPlayerStats.setValue(entry.get("player_stats"));
		System.out.println("pushed");
	}
	
	public void queueData(HashMap<String, Object> entry){
		System.out.println("added");
		firebaseQueue.add(entry);
	}

	@Override
	public void run() {
		while(true){
			if(!firebaseQueue.isEmpty()){
				System.out.println("pushing");
				logData(firebaseQueue.pop());
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
