
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.jna.Pointer;
public class PlayerListPlayerLocator extends PlayerLocator {
  private static HashMap<Integer, Integer> pattern;
  static {
    pattern = new HashMap<Integer, Integer>();
    pattern.put(0x34, 280); //xp to level 2
    pattern.put(0x1BC, 280); //next player xp to level 2
  }

  public ArrayList<Integer> getPlayerAddresses() {
    Integer player1 = searchForPattern(pattern);
    HashMap<Integer, Integer> playerListPattern = new HashMap<Integer, Integer>();
    playerListPattern.put(0x0, player1);
    playerListPattern.put(0x4, player1+0x188);
    Integer playerListRoot = searchForPattern(playerListPattern);
    ArrayList<Integer> result = new ArrayList<Integer>();
    for(int i = 0; i < 10; i++) {
      result.add(getValueAt(playerListRoot + (i * 0x4)));
    }
    return result;
  }
}
