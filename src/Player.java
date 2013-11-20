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
	
	public int getStat(int offset, Kernel32 kernel32, Pointer process){
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(4);
		kernel32.ReadProcessMemory(process, this.baseAddress + offset, output, 4, read);
		return output.getInt(0);	
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
	
	public static Stat getTotalStatFromPlayers(Player[] players, String name, int offset, Kernel32 kernel32, Pointer process){
		int total = 0;
		for(int i=0; i<players.length; i++){
			total += players[i].getStat(offset, kernel32, process);
		}
		return new Stat(name, total);
	}
}
