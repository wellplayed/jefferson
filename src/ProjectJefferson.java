
import java.util.ArrayList;

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
		ArrayList<Integer> addresses = searchMemory(lolprocess, 280,280, 0x30000000, 0x40000000);
		if(addresses.size()>=1){
			int baseAddress = addresses.get(0)-0x1C;
			System.out.println(baseAddress);
			Player[] players = new Player[5];
			for(int i=0; i<players.length; i++){
				players[i] = new Player("Blue", "" + i, baseAddress + (i*0x188));
			}
	        for(int i = 0; i<20; i++){
	        	for(Player player : players){
	        		System.out.println(player.getName() + " - " + player.getChampion() + " - " + player.getStats(kernel32, lolprocess));
	        	}
	        	Thread.sleep(10000);
	        }
		}
	}
	
	public static Pointer openProcess(int permissions, int pid) {  
        Pointer process = kernel32.OpenProcess(permissions, true, pid);  
        return process;  
    }
	
	public static ArrayList<Integer> searchMemory(Pointer process, int value, int value2, int start, int end){
		ArrayList<Integer> addresses = new ArrayList<Integer>();
		int divider = 64;
		for(int x=start; x<end; x += (end-start)/divider){
			IntByReference read = new IntByReference(0);
			Memory output = new Memory((end-start)/divider);
			kernel32.ReadProcessMemory(process, x, output, (end-start)/divider, read);
			for(int i=0; i<(end-start)/divider; i+=0x4){
				if(output.getInt(i)==value){
					if(output.getInt(i+0x188)==value2){
						addresses.add(x+i);
					}
				}
			}
		}
		return addresses;
	}
	
	public static int readMemory(Pointer process, int address){
		IntByReference read = new IntByReference(0);
		Memory output = new Memory(4);
		kernel32.ReadProcessMemory(process, address, output, 4, read);
		return output.getInt(0);	
	}
	
}
