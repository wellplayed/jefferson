import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;


public class SC2Calibration extends PlayerLocator{
	private static final int start = 0x01000000;
	private static final int end = 0x05000000;
	private int redPlayer;
	private int bluePlayer;

	public SC2Calibration(){
		Scanner s = new Scanner(System.in);
		System.out.println("Do you want to calibrate Jefferson for SC2 or load previous calibration?");
		System.out.println("1 - Re-Calibrate");
		System.out.println("2 - Load Previous Calibration");
		int calibrate = s.nextInt();
		if(calibrate == 1){
			System.out.println("Please launch Starcraft 2 and create a custom 1v1 melee game.");
			System.out.println("Make yourself a spectator and add two bots set to the 'very hard' difficulty.");
			System.out.println("\nFinding Red Player\n");
			redPlayer = findPlayer("Red");
			System.out.println("\nFinding Blue Player\n");
			bluePlayer = findPlayer("Blue");
			saveSC2Config();
			System.out.println("Calibration Complete");
		}else if(calibrate == 2){
			loadSC2Config();
			System.out.println("Calibration Loaded");
			//System.out.println(getPlayerAddresses());
		}else{
			System.err.println("Invalid Calibration Option");
			System.exit(0);
		}
		
	}
	
	private void loadSC2Config(){
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream("sc2.properties");
			prop.load(input);
			redPlayer = Integer.parseInt(prop.getProperty("redplayer"));
			bluePlayer = Integer.parseInt(prop.getProperty("blueplayer"));
	 
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void saveSC2Config(){
		Properties prop = new Properties();
		OutputStream output = null;
		try {output = new FileOutputStream("sc2.properties");
			prop.setProperty("redplayer", "" + redPlayer);
			prop.setProperty("blueplayer", "" + bluePlayer);
			prop.store(output, null);
	 
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private int findPlayer(String color){
		Scanner s = new Scanner(System.in);
		System.out.println("Once in game, pull up APM tab by pressing 'M' and when the two players have a different Average APM, pause the game.");
		System.out.println("Once paused, please input the Average APM of the " + color + " player?");
		System.out.print(color + " players Avergae APM:");
		ArrayList<Integer> addresses = initialValueSearch(s.nextInt());
		while(addresses.size() > 4){
			System.out.println("Resume the game.  Once the " + color + " player's APM has changed and is still different from other players, pause the game.");
			System.out.println("Once paused, please input the Average APM of the " + color + " player?");
			System.out.print(color + " players Avergae APM:");
			addresses = checkAddressesStillMatch(addresses, s.nextInt());
		}
		System.out.println("Player found at: " + String.format("0x%08X", addresses.get(0)));
		return addresses.get(0);
	}
	
	private ArrayList<Integer> checkAddressesStillMatch(ArrayList<Integer> addresses, int value){
		for(int i=0; i<addresses.size(); i++){
			if(MemoryAccess.getInteger(addresses.get(i)) != value){
				addresses.remove(i);
				i--;
			}
		}
		return addresses;
	}
	
	private ArrayList<Integer> initialValueSearch(int value){
		ArrayList<Integer> addresses = new ArrayList<Integer>();
		int lastAddress = start;
		while(lastAddress < end){
			lastAddress = searchForValue(value, lastAddress, end, 0x40000);
			if(lastAddress == -1){
				break;
			}else{
				addresses.add(lastAddress);
				lastAddress+= 0x4;
			}
		}
		return addresses;
	}
	
	@Override
	public ArrayList<Integer> getPlayerAddresses() {
		ArrayList<Integer> players = new ArrayList<Integer>();
		players.add(redPlayer);
		players.add(bluePlayer);
		return players;
	}
	
	public static void main(String[] args) {
		new SC2Calibration();
	}
}
