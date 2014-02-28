
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.jna.Pointer;

class ExperienceAndOffsetsPlayerLocator extends PlayerLocator {
  private static HashMap<Integer, Integer> pattern;
  static {
    pattern = new HashMap<Integer, Integer>();
    pattern.put(0x34, 280); //xp to level 2
    pattern.put(Player.PLAYER_SIZE + 0x34, 280); //next player xp to level 2
    pattern.put((Player.PLAYER_SIZE * 2) + 0x34, 280);
    pattern.put((Player.PLAYER_SIZE * 3) + 0x34, 280);
    pattern.put((Player.PLAYER_SIZE * 4) + 0x34, 280);
  }

  public ArrayList<Integer> getPlayerAddresses() {
    Integer player1 = searchForPattern(pattern);
	//Integer player1 = 0x3B86CAA0;
    ArrayList<Integer> result = new ArrayList<Integer>();
    int teamOffset = 0x7F8;
    for(int i = 0; i < 5; i++) {
      result.add(player1 + (Player.PLAYER_SIZE * i));
    }
    for(int i = 0; i < 5; i++) {
      result.add(player1 + teamOffset + (Player.PLAYER_SIZE * i));
    }
    return result;
  }
}
