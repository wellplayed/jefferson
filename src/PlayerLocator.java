import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

abstract class PlayerLocator {
  protected static User32 user32 = (User32) Native.loadLibrary("user32", User32.class);
  protected static Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
  protected static int readRight = 0x0010;
  protected static int playerBlockStart = 0x30000000;
  protected static int playerBlockEnd = 0x40000000;
  protected Pointer memory;

  public PlayerLocator(Pointer appMemory) {
    memory = appMemory;
  }

  public abstract ArrayList<Integer> getPlayerAddresses();

  protected Integer searchForValue(Integer value) {
    int divider = 128;
    int blockSize = (playerBlockEnd - 0x20000000) / divider;
    //for each block
    for(int x = 0x20000000; x < playerBlockEnd; x += blockSize) {
      IntByReference read = new IntByReference(0);
      Memory output = new Memory(blockSize);
      kernel32.ReadProcessMemory(memory, x, output, blockSize, read);
      for(int i = 0; i < blockSize; i += 0x4){
        if(output.getInt(i) == value) {
          return x + i;
        }
      }
    }
    return -1;
  }

  protected Integer getValueAt(Integer address) {
    IntByReference read = new IntByReference(0);
    Memory output = new Memory(4);
    kernel32.ReadProcessMemory(memory, address, output, 4, read);
    return output.getInt(0);
  }

  protected Integer searchForPattern(HashMap<Integer, Integer> pattern) {
    ArrayList<Integer> addresses = new ArrayList<Integer>();
    int divider = 64;
    int blockSize = (playerBlockEnd - playerBlockStart)/divider;
    //for each block
    for(int x = playerBlockStart; x < playerBlockEnd; x += blockSize){
      IntByReference read = new IntByReference(0);
      Memory output = new Memory(blockSize);
      kernel32.ReadProcessMemory(memory, x, output, blockSize, read);
      //for each 4bytes in block
      for(int i = 0; i < blockSize; i += 0x4){
        boolean match = true;
        for(Map.Entry<Integer, Integer> entry: pattern.entrySet()) {
          if(i + entry.getKey() >= blockSize) {
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
          addresses.add(x + i);
        }
      }
    }
    return addresses.get(0);
  }
}
