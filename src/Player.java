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
	
	public int getStatInt(int offset, Kernel32 kernel32, Pointer process){
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(4);
		kernel32.ReadProcessMemory(process, this.baseAddress + offset, output, 4, read);
		return output.getInt(0);	
	}
	
	public float getStatFloat(int offset, Kernel32 kernel32, Pointer process){
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(4);
		kernel32.ReadProcessMemory(process, this.baseAddress + offset, output, 4, read);
		return output.getFloat(0);	
	}
	
	public ArrayList<Stat> getStats(Kernel32 kernel32, Pointer process){
		ArrayList<Stat> stats = new ArrayList<Stat>();
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(0x180);  
		kernel32.ReadProcessMemory(process, this.baseAddress, output, 0x180, read);
		for(int i=0; i<PlayerStats.STATS.length; i++){
			if(PlayerStats.STAT_DATA_TYPES[i] == PlayerStats.DATA_TYPE.INTEGER){
				stats.add(new Stat(PlayerStats.STAT_NAMES[i], output.getInt(PlayerStats.STATS[i])));
			}else if(PlayerStats.STAT_DATA_TYPES[i] == PlayerStats.DATA_TYPE.FLOAT){
				stats.add(new Stat(PlayerStats.STAT_NAMES[i], output.getFloat(PlayerStats.STATS[i])));
			}
		}
		return stats;
	}
	
	public ArrayList<Stat> getLoggedStats(Kernel32 kernel32, Pointer process){
		ArrayList<Stat> stats = new ArrayList<Stat>();
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(0x180);  
		kernel32.ReadProcessMemory(process, this.baseAddress, output, 0x180, read);
		for(int i=0; i<PlayerStats.LOGGED_STATS.length; i++){
			if(PlayerStats.LOGGED_STAT_DATA_TYPES[i] == PlayerStats.DATA_TYPE.INTEGER){
				stats.add(new Stat(PlayerStats.LOGGED_STAT_NAMES[i], output.getInt(PlayerStats.LOGGED_STATS[i])));
			}else if(PlayerStats.LOGGED_STAT_DATA_TYPES[i] == PlayerStats.DATA_TYPE.FLOAT){
				stats.add(new Stat(PlayerStats.LOGGED_STAT_NAMES[i], output.getFloat(PlayerStats.LOGGED_STATS[i])));
			}
		}
		return stats;
	}
	
	public static Stat getTotalStatFromPlayers(Player[] players, String name, int offset, PlayerStats.DATA_TYPE type, Kernel32 kernel32, Pointer process){
		Stat s = null;
		if(type == PlayerStats.DATA_TYPE.INTEGER){
			int total = 0;
			for(int i=0; i<players.length; i++){
				total += players[i].getStatInt(offset, kernel32, process);
			}
			s=new Stat(name, total);
		}else if(type == PlayerStats.DATA_TYPE.FLOAT){
			float total = 0f;
			for(int i=0; i<players.length; i++){
				total += players[i].getStatFloat(offset, kernel32, process);
			}
			s=new Stat(name, total);
		}
		return s;
	}
}
