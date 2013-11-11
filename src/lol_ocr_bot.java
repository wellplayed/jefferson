/*
 * League of Legends OCR Bot
 * 
 * This program uses the Tesseract OCR engine to read 
 * values gold values in spectator mode of League of Legends
 * and then uploads them to a Firebase URL.
 * 
 * You must install tesseract before running.  A copy is included in
 * the root folder.
 * 
 * Currently the bot isn't very configurable.  The image locations 
 * are static, requiring that you match my exact settings.  The Firebase
 * URL is also static.  Just a lot of static things.  The bot also requires
 * that you aren't using coloblind mode.  The Blue and Purple are similar colors
 * allowing me to use the same filter twice.
 * 
 * Plans
 * -Make OCR zones configurable from Obscene.
 * -Ask for a Firebase URL on startup and then store the data in the active series
 * -Experiment with reading smaller text and improving both image and text filters.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.firebase.client.*;

public class lol_ocr_bot {
	public static final String FIREBASE_BASE_URL = "https://vin-obscene.firebaseio.com/broadcasts/";
	public static Firebase fb;
	public static Firebase activeSeries;
	public static Firebase activeMatch;
	public static String status = "stop";
    public static Replacer[] replacements = {
        new Replacer("|(", "k"),
        new Replacer("l", "1"),
        new Replacer("-k", "k"),
        new Replacer("I", "1"),
        new Replacer("1(", "k"),
        new Replacer("Z", "2")
    };
    public static final long DELAY = 10000;

    public static final String INDIVIDUAL_GOLD_CONFIG = "individualgoldconf";
    public static final String TEAM_GOLD_CONFIG = "teamgoldconf";
    public static final String TIME_CONFIG = "timeconf";

	public static void log_event(int i, String blue_gold, String red_gold, String event, String team, String champion){
        System.out.println(
                "Blue: " + blue_gold +
                " Red: " + red_gold +
                " Event: " + event +
                " Team: " + team +
                " Champ: " + champion
        );

		Firebase newLogEntry = activeMatch.child("/game_log").push();
		Map<String, String> data = new HashMap<String, String>();
		data.put("time", convertTimeToString(i*DELAY));
		data.put("blue_gold", blue_gold);
		data.put("red_gold", red_gold);
		data.put("event", event);
		newLogEntry.setValue(data);
	}
	
	public static String text_filter(String text){
        for(int i = 0; i < replacements.length; i++) {
            text = replacements[i].apply(text);
        }
        return text;
	}
	
	public static String read_text_from_image(File image, String config) {
        if(config == null) {
            config = "";
        }
        try {
            Runtime rt = Runtime.getRuntime();
            Process tesseract = rt.exec("tesseract \"" + image.getAbsolutePath() + "\" \"" + image.getAbsolutePath().substring(0, image.getAbsolutePath().lastIndexOf(".")) + "\" " + config);
            tesseract.waitFor();
        } catch(Exception e) {
            System.out.println("Error with Tesseract! check path");
            e.printStackTrace();
            return "n/a";
        }

        try{
            File f = new File(image.getAbsolutePath().substring(0, image.getAbsolutePath().lastIndexOf(".")) + ".txt");
            Scanner s = new Scanner(f);
            return text_filter(s.nextLine());
        } catch(Exception e) {
            System.out.println("Error reading file " + image.getAbsolutePath());
            return "n/a";
        }
	}
	
	public static BufferedImage image_filter(BufferedImage image){
		for(int x=0;x<image.getWidth(); x++){
			for(int y=0; y<image.getHeight(); y++){
				if((image.getRGB(x, y) & 0x000000FF) > 100){
					image.setRGB(x, y, Color.black.getRGB());
				}else{
					image.setRGB(x, y, Color.white.getRGB());
				}
			}
		}
		return image;
	}

    public static BufferedImage grabScreen(Robot rob, int x, int y, int w, int h) {
        return rob.createScreenCapture(new Rectangle(x, y, w, h));
    }

    public static void captureTeamGold(Robot rob, int i) throws IOException {
        BufferedImage blue_gold_image = image_filter(grabScreen(rob, 760, 25, 80, 30));
        BufferedImage red_gold_image = image_filter(grabScreen(rob, 1110, 25, 80, 30));
        File blue_gold_image_file = new File("blue_gold_" + i + ".png");
        ImageIO.write(blue_gold_image, "png", blue_gold_image_file);
        File red_gold_image_file = new File("red_gold_" + i + ".png");
        ImageIO.write(red_gold_image, "png", red_gold_image_file);
        String blue_gold = read_text_from_image(blue_gold_image_file, TEAM_GOLD_CONFIG);
        String red_gold = read_text_from_image(red_gold_image_file, TEAM_GOLD_CONFIG);
        System.out.print("Iteration: " + i + " => ");
        log_event(i, blue_gold, red_gold, "gold", "n/a", "n/a");
    }

    public static void capturePlayerGold(Robot rob, int i) throws IOException {
        File[] blues = {
            new File("blue_player_1_gold_" + i + ".png"),
            new File("blue_player_2_gold_" + i + ".png"),
            new File("blue_player_3_gold_" + i + ".png"),
            new File("blue_player_4_gold_" + i + ".png"),
            new File("blue_player_5_gold_" + i + ".png")
        };
        File[] reds = {
            new File("red_player_1_gold_" + i + ".png"),
            new File("red_player_2_gold_" + i + ".png"),
            new File("red_player_3_gold_" + i + ".png"),
            new File("red_player_4_gold_" + i + ".png"),
            new File("red_player_5_gold_" + i + ".png")
        };
        ImageIO.write(image_filter(grabScreen(rob, 600, 923 , 155, 31 )), "png", blues[0]);
        ImageIO.write(image_filter(grabScreen(rob, 600, 954, 155, 31  )), "png", blues[1]);
        ImageIO.write(image_filter(grabScreen(rob, 600, 985, 155, 31  )), "png", blues[2]);
        ImageIO.write(image_filter(grabScreen(rob, 600, 1016, 155, 31 )), "png", blues[3]);
        ImageIO.write(image_filter(grabScreen(rob, 600, 1046, 155, 31 )), "png", blues[4]);
        ImageIO.write(image_filter(grabScreen(rob, 1180, 923, 155, 31 )), "png",  reds[0]);
        ImageIO.write(image_filter(grabScreen(rob, 1180, 954, 155, 31 )), "png",  reds[1]);
        ImageIO.write(image_filter(grabScreen(rob, 1180, 985, 155, 31 )), "png",  reds[2]);
        ImageIO.write(image_filter(grabScreen(rob, 1180, 1016, 155, 31)), "png",  reds[3]);
        ImageIO.write(image_filter(grabScreen(rob, 1180, 1046, 155, 31)), "png",  reds[4]);
        StringBuilder redGolds =  new StringBuilder("(Red team)  ");
        StringBuilder blueGolds = new StringBuilder("(Blue team) ");

        for(int j = 0; j < 5; j++) {
            redGolds.append("" + (j + 1) + ": " + read_text_from_image(reds[j], INDIVIDUAL_GOLD_CONFIG) + ", ");
            blueGolds.append("" + (j + 1) + ": " + read_text_from_image(blues[j], INDIVIDUAL_GOLD_CONFIG) + ", ");
        }

        System.out.println(blueGolds.toString());
        System.out.println(redGolds.toString());
    }
    
    public static String convertTimeToString(long time){
    	String seconds = "" + ((time/1000)%60);
    	String minutes = "" + ((time/1000)/60)%60;
    	String hours = "" + ((time/1000)/60)/60;
    	if(seconds.length() == 1) seconds = "0" + seconds;
    	if(minutes.length() == 1) minutes = "0" + minutes;
    	if(hours.length() == 1) hours = "0" + hours;
    	return hours + ":" + minutes + ":" + seconds;
    	
    }
	
	public static void configureFirebase(String broadcastSlug){
		fb = new Firebase(FIREBASE_BASE_URL + broadcastSlug);
		fb.child("/settings/active_series_id").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		        activeSeries = fb.child("/series/" + snapshot.getValue().toString() + "/matches");
		        activeSeries.addValueEventListener(new ValueEventListener() {
		            @Override
		            public void onDataChange(DataSnapshot snapshot) {
		            	ArrayList dataList = (ArrayList)snapshot.getValue();
		            	for(int i=0; i<dataList.size(); i++){
		            		if(((Map)dataList.get(i)).get("active").toString().equals("true")){
		            			if(activeMatch == null || !activeMatch.toString().equals(activeSeries.toString() + "/" + i)){
		            				activeMatch = activeSeries.child("/" + i);
		            				System.out.println(activeMatch.toString());
		            			}
		            		}
		            	}
		            }
		            @Override
		            public void onCancelled() {
		                System.err.println("Listener was cancelled");
		            }
		        });
		    }
		    @Override
		    public void onCancelled() {
		        System.err.println("Listener was cancelled");
		    }
		});
		
		fb.child("/settings/ocr_status").addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		    	status = snapshot.getValue().toString();
		    }

		    @Override
		    public void onCancelled() {
		        System.err.println("Listener was cancelled");
		    }
		});
	}
	
	public static void main(String[] args) {
		configureFirebase("spq-na-1");
		try{
			Robot rob = new Robot();
			rob.delay(3000);
            int i = 0;
			while(true) {
				if(status.equals("start")){
					long startTime = Calendar.getInstance().getTimeInMillis();
					captureTeamGold(rob, i);
	                i++;
					rob.delay((int)(DELAY - (startTime-Calendar.getInstance().getTimeInMillis())));
				}
				else if(status.equals("stop")){
					i = 0;
					rob.delay(1000);
				}
				else if(status.equals("pause")){
					rob.delay(1000);
				}
			}
		} catch(Exception e) {
			System.out.println("IT BROKED");
			e.printStackTrace();
		} finally {
            System.exit(0);
        }
        
	}
}
