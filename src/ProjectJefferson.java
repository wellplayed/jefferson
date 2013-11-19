
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
public class ProjectJefferson {

	static User32 user32 = (User32) Native.loadLibrary("user32", User32.class);
	static Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
	static int readRight = 0x0010; 
	
	public static int getProcessId(String window) {  
        IntByReference pid = new IntByReference(0);  
        user32.GetWindowThreadProcessId(user32.FindWindowA(null, window), pid);  
        return pid.getValue();  
    }  
	public static void main(String[] args) throws InterruptedException {
		int pid = getProcessId("League of Legends (TM) Client"); // get our process ID  
		Pointer lolprocess = openProcess(readRight, pid);
		int size = 4; // we want to read 4 bytes
		int address = 0x3672BBD0;
		Player blue1 = new Player("Link", "Orianna", address);
        for(int i = 0; i<10; i++){
           System.out.println(blue1.getName() + " - " + blue1.getChampion() + " - " + blue1.getStats(kernel32, lolprocess));
           Thread.sleep(5000);
        }
	}
	
	public static Pointer openProcess(int permissions, int pid) {  
        Pointer process = kernel32.OpenProcess(permissions, true, pid);  
        return process;  
    }
}
