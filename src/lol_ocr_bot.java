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
import java.util.HashMap;
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
	
	public static Firebase fb = new Firebase("https://ivylol-obscene.firebaseio.com/broadcasts/ivylol2/widget/-J7Zm9Gg4obv1PTxL3Nm/config/-J7_jpzNnnI_iu_HqU-Z/game_log");
    public static Replacer[] replacements = {
        new Replacer("|(", "k"),
        new Replacer("l", "1"),
        new Replacer("-k", "k"),
        new Replacer("I", "1"),
        new Replacer("1(", "k"),
        new Replacer("Z", "2")
    };

	public static void log_event(String blue_gold, String red_gold, String event, String team, String champion){
        System.out.println(
                "Blue: " + blue_gold +
                " Red: " + red_gold +
                " Event: " + event +
                " Team: " + team +
                " Champ: " + champion
        );
        /*
		Firebase child = fb.push();
		Map<String, String> data = new HashMap<String, String>();
		data.put("time", "00h00m00s");
		data.put("blue_gold", blue_gold);
		data.put("red_gold", red_gold);
		data.put("event", event);
		data.put("team", team);
		data.put("champion", champion);
		child.setValue(data);
		*/
	}
	
	public static String text_filter(String text){
        for(int i = 0; i < replacements.length; i++) {
            text = replacements[i].apply(text);
        }
        return text;
	}
	
	public static String read_text_from_image(File image) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process tesseract = rt.exec("tesseract \"" + image.getAbsolutePath() + "\" \"" + image.getAbsolutePath().substring(0, image.getAbsolutePath().lastIndexOf(".")) + "\"");
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

	public static void main(String[] args) {
		try{
			Robot rob = new Robot();
			rob.delay(3000);
            int i = 0;
			while(true) {
				BufferedImage blue_gold_image = image_filter(rob.createScreenCapture(new Rectangle(760,25,80,30)));
				BufferedImage red_gold_image = image_filter(rob.createScreenCapture(new Rectangle(1110,25,80,30)));
				File blue_gold_image_file = new File("blue_gold_" + i + ".png");
				ImageIO.write(blue_gold_image, "png", blue_gold_image_file);
				File red_gold_image_file = new File("red_gold_" + i + ".png");
				ImageIO.write(red_gold_image, "png", red_gold_image_file);
				String blue_gold = read_text_from_image(blue_gold_image_file);
				String red_gold = read_text_from_image(red_gold_image_file);
                System.out.print("Iteration: " + i + " => ");
				log_event(blue_gold, red_gold, "gold", "n/a", "n/a");
                i++;
				rob.delay(5000);
			}
		} catch(Exception e) {
			System.out.println("IT BROKED");
			e.printStackTrace();
		} finally {
            System.exit(0);
        }
	}
}
