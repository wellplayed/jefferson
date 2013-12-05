import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

abstract class PlayerLocator {
  protected static int playerBlockStart = 0x20000000;
  protected static int playerBlockEnd = 0x40000000;
  protected static int playerSize = 0x694;

  public abstract ArrayList<Integer> getPlayerAddresses();

  protected Integer searchForValue(Integer value) {
    int divider = 256;
    int blockSize = (playerBlockEnd - playerBlockStart) / divider;
    //for each block
    for(int x = 0x20000000; x < playerBlockEnd; x += blockSize) {
      IntByReference read = new IntByReference(0);
      Memory output = new Memory(blockSize+playerSize);
      MemoryAccess.readBlock(output, x, blockSize, read);
      for(int i = 0; i < blockSize; i += 0x4){
        if(value.equals(output.getInt(i))) {
          return x + i;
        }
      }
    }
    return -1;
  }

  protected Integer getValueAt(Integer address) {
    return MemoryAccess.getInteger(address);
  }

  protected Integer searchForPattern(HashMap<Integer, Integer> pattern) {
    ArrayList<Integer> addresses = new ArrayList<Integer>();
    int divider = 256;
    int blockSize = (playerBlockEnd - playerBlockStart)/divider;
    //for each block
    for(int x = playerBlockStart; x < playerBlockEnd; x += blockSize){
      IntByReference read = new IntByReference(0);
      Memory output = new Memory(blockSize + playerSize);
      MemoryAccess.readBlock(output, x, blockSize, read);
      //for each 4bytes in block
      for(int i = 0; i < blockSize; i += 0x4){
        boolean match = true;
        for(Map.Entry<Integer, Integer> entry: pattern.entrySet()) {
          if(i + entry.getKey() >= blockSize+playerSize) {
            match = false;
            break;
          }
          if(output.getInt(i + entry.getKey()) == entry.getValue()) {
            //pattern matches so far...
          } else {
            match = false;
            break;
          }
        }
        if(match) {
          System.out.println(String.format("0x%08X",x+i));
          addresses.add(x + i);
        }
      }
    }
    return addresses.get(0);
  }
}
