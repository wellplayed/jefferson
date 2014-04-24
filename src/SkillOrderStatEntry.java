import java.util.HashMap;
import java.util.Map;


public class SkillOrderStatEntry extends StatEntry<Map<Integer, String>> {

	public SkillOrderStatEntry(int qLevel, int wLevel, int eLevel, int rLevel, Map<Integer, String> skillOrder) {
		qLevel = (qLevel>5)?0:qLevel;
		wLevel = (wLevel>5)?0:wLevel;
		eLevel = (eLevel>5)?0:eLevel;
		rLevel = (rLevel>5)?0:rLevel;
		
		if(skillOrder.size() < qLevel + wLevel + eLevel + rLevel){
			for (String skill : skillOrder.values()) {
				if(skill.equals("q")){
					qLevel--;
				}else if(skill.equals("w")){
					wLevel--;
				}else if(skill.equals("e")){
					eLevel--;
				}else if(skill.equals("r")){
					rLevel--;
				}
			}
			if(qLevel == 1){
				skillOrder.put(skillOrder.size() + 1, "q");
			}else if(wLevel == 1){
				skillOrder.put(skillOrder.size() + 1, "w");
			}else if(eLevel == 1){
				skillOrder.put(skillOrder.size() + 1, "e");
			}else if(rLevel == 1){
				skillOrder.put(skillOrder.size() + 1, "r");
			}
		}
		this.name = "Skill Order";
		this.value = skillOrder;
	}
	
	public Map<Integer, String> getSkillOrderMap(){
		return this.value;
	}

	@Override
	public StatEntry<Map<Integer, String>> add(StatEntry<Map<Integer, String>> other) {
		// TODO Auto-generated method stub
		return null;
	}

}
