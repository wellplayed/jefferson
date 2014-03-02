
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.jna.Pointer;

class ExperienceAndOffsetsPlayerLocator extends PlayerLocator {
  private HashMap<Integer, Integer> pattern;
  private int numPlayers;
  private int teamOffset;
  
  public ExperienceAndOffsetsPlayerLocator(int numPlayers){
	  this.numPlayers = numPlayers;
	  teamOffset = numPlayers * Player.PLAYER_SIZE;
	  pattern = new HashMap<Integer, Integer>();
	  for(int i=0; i<numPlayers; i++){
		  pattern.put((Player.PLAYER_SIZE * i) + 0x34, 280);
		  pattern.put((Player.PLAYER_SIZE * i) + 0x34 + teamOffset, 280);
	  }
  }

  public ArrayList<Integer> getPlayerAddresses() {
    Integer player1 = searchForPattern(pattern);
    ArrayList<Integer> result = new ArrayList<Integer>();
    
    for(int i = 0; i < this.numPlayers; i++) {
      result.add(player1 + (Player.PLAYER_SIZE * i));
    }
    for(int i = 0; i < this.numPlayers; i++) {
      result.add(player1 + teamOffset + (Player.PLAYER_SIZE * i));
    }
    return result;
  }
}
