import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class MemoryAccess {
  static User32 user32 = (User32) Native.loadLibrary("user32", User32.class);
  static Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
  static int readRight = 0x0010;
  static Pointer lolMemory = openProcess(readRight, getProcessId("Starcraft II"));

  private static int getProcessId(String window) {
    IntByReference pid = new IntByReference(0);
    user32.GetWindowThreadProcessId(user32.FindWindowA(null, window), pid);
    return pid.getValue();
  }

  private static Pointer openProcess(int permissions, int pid) {
    Pointer process = kernel32.OpenProcess(permissions, true, pid);
    return process;
  }

  private static Memory readSingleValue(int address) {
    IntByReference read = new IntByReference(0);
    Memory output = new Memory(4);
    kernel32.ReadProcessMemory(lolMemory, address, output, 4, read);
    return output;
  }

  public static int getInteger(int address) {
    return readSingleValue(address).getInt(0);
  }

  public static float getFloat(int address) {
    return readSingleValue(address).getFloat(0);
  }

  public static void readBlock(Memory output, int startAddress, int amount, IntByReference read) {
    kernel32.ReadProcessMemory(lolMemory, startAddress, output, amount, read);
  }
}
