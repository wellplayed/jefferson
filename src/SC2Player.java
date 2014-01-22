import java.util.ArrayList;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public class SC2Player {

  public static final int PLAYER_SIZE = 0x198;

	private String playerName;
	private int baseAddress;
	
	public SC2Player(String playerName, int baseAddress){
		this.playerName = playerName;
		this.baseAddress = baseAddress;
	}
	
	public String getName(){
		return this.playerName;
	}
	
	public int getStatInt(int offset){
		return MemoryAccess.getInteger(this.baseAddress + offset);
	}
	
	public float getStatFloat(int offset){
    return MemoryAccess.getFloat(this.baseAddress + offset);
	}
	
	public ArrayList<StatEntry> getStats(){
		ArrayList<StatEntry> stats = new ArrayList<StatEntry>();
	    for(Stat stat: PlayerStats.SC2_STATS) {
	      stats.add(stat.getEntry(this.baseAddress));
	    }

		return stats;
	}
	
	public ArrayList<StatEntry> getLoggedStats() {
		ArrayList<StatEntry> stats = new ArrayList<StatEntry>();
	    for(Stat stat: PlayerStats.SC2_LOGGED_STATS) {
	      stats.add(stat.getEntry(this.baseAddress));
	    }

		return stats;
	}
	
	public static StatEntry getTotalStatFromPlayers(SC2Player[] players, Stat stat){
    StatEntry res = stat.zeroEntry();
    for(SC2Player player: players) {
      res = res.add(stat.getEntry(player.baseAddress));
    }
    return res;
	}
}
