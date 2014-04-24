import java.util.HashMap;
import java.util.Map;


public class SkillOrderStat extends Stat {
	
	public int qOffset;
	public int wOffset;
	public int eOffset;
	public int rOffset;
	public Map<Integer, Map<Integer, String>> skillOrder;
	
	public SkillOrderStat(int qOffset, int wOffset, int eOffset, int rOffset) {
		super("Skill Order");
		skillOrder = new HashMap<Integer, Map<Integer, String>>();
		this.qOffset = qOffset;
		this.wOffset = wOffset;
		this.eOffset = eOffset;
		this.rOffset = rOffset;
	}

	@Override
	public StatEntry<Map<Integer, String>> getEntry(int playerBaseAddress) {
		Map<Integer, String> order = skillOrder.get(playerBaseAddress);
		if(order == null){
			order = new HashMap<Integer, String>();
		}
		
		int q =  MemoryAccess.getInteger(playerBaseAddress + qOffset);
		int w =  MemoryAccess.getInteger(playerBaseAddress + wOffset);
		int e =  MemoryAccess.getInteger(playerBaseAddress + eOffset);
		int r =  MemoryAccess.getInteger(playerBaseAddress + rOffset);
		
		System.out.println((playerBaseAddress + qOffset) + " - Q: " + q  + " - W: " + w + " - E: " + e + " - R: " + r);
		
		SkillOrderStatEntry entry = new SkillOrderStatEntry(q, 
															w,
															e, 
															r, 
															order);
		skillOrder.remove(playerBaseAddress);
		skillOrder.put(playerBaseAddress, entry.getSkillOrderMap());
		return entry;
	}

	@Override
	public StatEntry<Map<Integer, String>> zeroEntry() {
		// TODO Auto-generated method stub
		return null;
	}

}
