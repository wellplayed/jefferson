import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class SC2JSONParser {
	
	public static HashMap<String, Object> parseSC2Player(String fileName){
		HashMap<String, Object> playerData = new HashMap<String, Object>(); 
		
		JsonParserFactory factory=JsonParserFactory.getInstance();
		JSONParser parser=factory.newJsonParser();
		Map jsonData= parser.parseJson(readFile(fileName));
		
		Map player = (Map) jsonData.get("playerGeneral");
		String raceSlug = (String) player.get("race");
		String race = "";
		if(raceSlug.equals("Terr")) race = "terran";
		else if(raceSlug.equals("Prot")) race = "protoss";
		else if(raceSlug.equals("Zerg")) race = "zerg";
		
		Map minerals = (Map) jsonData.get("minerals");
		Map gas = (Map) jsonData.get("gas");
		Map supply = (Map) jsonData.get("supply");
		Map mineralValues = (Map) jsonData.get("mineralValues");
		Map gasValues = (Map) jsonData.get("gasValues");
		Map upgrades = (Map) jsonData.get(race + "Upgrades");
		Map structureCount = (Map) jsonData.get(race + "StructureCount");
		Map unitCount = (Map) jsonData.get(race + "UnitCount");
		ArrayList unitLocations = (ArrayList) jsonData.get("unitLocations");
		
		addData("minerals", minerals, playerData);
		addData("gas", gas, playerData);
		addData("supply", supply, playerData);
		addData("mineral_value", mineralValues, playerData);
		addData("gas_value", gasValues, playerData);
		playerData.put("structures", structureCount);
		playerData.put("unit_count", unitCount);
		playerData.put("units", unitLocations);
		
		return playerData;
	}
	
	private static String formatTitle(String s){
		String formattedString = "";
		for(int i=0; i<s.length(); i++){
			if(s.charAt(i) >= 65 && s.charAt(i) <= 90){
				formattedString +="_";
			}
			formattedString += s.charAt(i);
		}
		return formattedString.toLowerCase();
	}
	
	private static void addData(String header, Map newData, HashMap<String, Object> data){
		Iterator it = newData.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        data.put(header + "_" + formatTitle((String)pairs.getKey()), pairs.getValue());
	        it.remove();
	    }
	}
	
	
	private static String readFile(String fileName){
		FileReader f = null;
		try {
			f = new FileReader(fileName);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader br = new BufferedReader(f);
		String everything = null;
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        everything = sb.toString();
		    } catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		 int start = everything.indexOf("{");
		 return everything.substring(start).replace("\"accountID\": \"\",", "\"accountID\": \"1234\",");
	}

	
	
	public static void main(String[] args){
		System.out.println(parseSC2Player("C:/Users/Sherman/Documents/StarCraft II/UserLogs/Habitation Station LE/DataLog_Player1.txt"));
	}
}
