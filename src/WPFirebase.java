import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;


public class WPFirebase implements Runnable{

	private final String FIREBASE_BASE_URL = "https://vin-obscene.firebaseio.com/broadcasts/";
	private String broadcastSlug;
	private String activeSeriesSlug;
	private String activeMatchSlug;
	private String jeffersonStatus;
	private Firebase firebase;
	
	public WPFirebase(String broadcastSlug){
		this.broadcastSlug = broadcastSlug;
		configureFirebase();
	}
	
	private void configureFirebase(){
		firebase = new Firebase(FIREBASE_BASE_URL + broadcastSlug);
		
		//Configure Active Series and Game Firebase URLS
		firebase.child("/settings/active_series_id").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				activeSeriesSlug = snapshot.getValue().toString();
				firebase.child("/series/" + snapshot.getValue().toString() + "/matches").addValueEventListener(new ValueEventListener() {
		            @Override
		            public void onDataChange(DataSnapshot snapshot) {
		            	ArrayList dataList = (ArrayList)snapshot.getValue();
		            	for(int i=0; i<dataList.size(); i++){
		            		if(((Map)dataList.get(i)).get("active") != null && ((Map)dataList.get(i)).get("active").toString().equals("true")){
			            		activeMatchSlug = "" + i;
			            		System.out.println("Active Series Changed (Series: " + activeSeriesSlug + ", Match: " + activeMatchSlug + ")");
		            		}
		            	}
		            }
		            @Override
		            public void onCancelled() {}
		        });
			}
			@Override
			public void onCancelled() {}			
		});
		
		//Load Game from Firebase
		firebase.child("/jefferson/game").addValueEventListener(new ValueEventListener(){
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				MemoryAccess.setProcessName(snapshot.getValue().toString());
			}
			
			@Override
			public void onCancelled() {}
		});
		
		//Load Number of Players from Firebase
		firebase.child("/jefferson/num_players").addValueEventListener(new ValueEventListener(){
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				System.out.println("Number of Players Set: " + snapshot.getValue().toString());
			}
			
			@Override
			public void onCancelled() {}
		});
		
		//Load Interval from Firebase
		firebase.child("/jefferson/interval").addValueEventListener(new ValueEventListener(){
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				System.out.println("Interval Set: " + snapshot.getValue().toString());
			}
			
			@Override
			public void onCancelled() {}
		});
		
		//Load Stats from Firebase
		firebase.child("/jefferson/stats").addValueEventListener(new ValueEventListener(){
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				ArrayList<Map> statList = (ArrayList<Map>)snapshot.getValue();
				Stat[] stats = new Stat[statList.size()]; 
				for(int i=0; i<stats.length; i++){
					Map statConfig = statList.get(i);
					Stat stat = null;
					int offset = Integer.parseInt(statConfig.get("offset").toString().replace("0x", ""),16);
					switch(Integer.parseInt(statConfig.get("type").toString())){
					case 0:
						stat = new IntStat(statConfig.get("name").toString(), offset);
						break;
					case 1:
						stat = new FloatStat(statConfig.get("name").toString(), offset);
						break;
					case 2:
						stat = new ItemValueStat(statConfig.get("name").toString(), offset);
						break;
					}
					System.out.println("Stat Loaded: " + statConfig.get("name"));
					stats[i] = stat;	
				}
				PlayerStats.STATS = stats;
			}

			@Override
			public void onCancelled() {}
		});
		
		//Configure Jefferson Control
		firebase.child("/jefferson/status").addValueEventListener(new ValueEventListener(){
			@Override
		    public void onDataChange(DataSnapshot snapshot) {
			 	jeffersonStatus = snapshot.getValue().toString();
			 	System.out.println(jeffersonStatus);
		    }

		    @Override
		    public void onCancelled() {}
		});
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		new WPFirebase("marchmadness");

	}

}
