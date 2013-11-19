import java.util.ArrayList;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public class Player {

	private String playerName;
	private String championName;
	private int baseAddress;
	
	public Player(String playerName, String championName, int baseAddress){
		this.playerName = playerName;
		this.championName = championName;
		this.baseAddress = baseAddress;
	}
	
	public String getName(){
		return this.playerName;
	}
	
	public String getChampion(){
		return this.championName;
	}
	
	public ArrayList<Stat> getStats(Kernel32 kernel32, Pointer process){
		ArrayList<Stat> stats = new ArrayList<Stat>();
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(0x180);  
		kernel32.ReadProcessMemory(process, this.baseAddress, output, 0x180, read);
		for(int i=0; i<PlayerStats.STATS.length; i++){
			stats.add(new Stat(PlayerStats.STAT_NAMES[i], output.getInt(PlayerStats.STATS[i])));
		}
		return stats;
	}
}
