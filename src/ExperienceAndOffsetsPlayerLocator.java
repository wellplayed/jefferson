package org.wellplayed.jefferson;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.jna.Pointer;

class ExperienceAndOffsetsPlayerLocator implements PlayerLocator {
  private static HashMap<Integer, Integer> pattern;
  static {
    pattern = new HashMap<Integer, Integer>();
    pattern.put(0x0, 270);
    pattern.put(0x188, 270);
  }

  public ExperienceAndOffsetsPlayerLocator(Pointer lolMemory) {
    super(lolMemory);
  }

  public ArrayList<Integer> getPlayerAddresses() {
    Integer player1 = searchForPattern(pattern);
    ArrayList<Integer> result = new ArrayList<Integer>();
    int playerOffset = 0x188;
    int teamOffset = 0x7A8;
    for(int i = 0; i < 5; i++) {
      result.add(player1 + (playerOffset * i));
    }
    for(int i = 0; i < 5; i++) {
      result.add(player1 + teamOffset + (playerOffset * i));
    }
    return result;
  }
}
