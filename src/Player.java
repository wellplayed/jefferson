import java.util.ArrayList;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public class Player {

  public static final int PLAYER_SIZE = 0x1A8;

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
	
	public int getStatInt(int offset){
		return MemoryAccess.getInteger(this.baseAddress + offset);
	}
	
	public float getStatFloat(int offset){
    return MemoryAccess.getFloat(this.baseAddress + offset);
	}
	
	public ArrayList<StatEntry> getStats(){
		ArrayList<StatEntry> stats = new ArrayList<StatEntry>();
    for(Stat stat: PlayerStats.STATS) {
      stats.add(stat.getEntry(this.baseAddress));
    }

		return stats;
	}
	
	public ArrayList<StatEntry> getLoggedStats() {
		ArrayList<StatEntry> stats = new ArrayList<StatEntry>();
    for(Stat stat: PlayerStats.LOGGED_STATS) {
      stats.add(stat.getEntry(this.baseAddress));
    }

		return stats;
	}
	
	public static StatEntry getTotalStatFromPlayers(Player[] players, Stat stat){
    StatEntry res = stat.zeroEntry();
    for(Player player: players) {
      res = res.add(stat.getEntry(player.baseAddress));
    }
    return res;
	}
}
