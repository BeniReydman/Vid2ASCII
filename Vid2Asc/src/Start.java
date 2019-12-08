import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

public class Start {
	public static void main(String[] args) {
		
			
		try {
			// imgToTxt("src/Objects/Mona.jpg");
			// imgToTxt("src/Objects/orange.jpg");
			convertVideo("src/Objects/video.mp4");
			// imgToASCIIImage("src/Objects/orange.jpg");
			

		} catch (java.lang.Exception e) {
			System.out.println("\n----------------------------");
			e.printStackTrace();
		}
	}

	private static void imgToASCIIImage(String inputPath) throws Exception {
		Converter c = new Converter();
		BufferedImage out = c.imgToAscii(new File(inputPath));
		ImageIO.write(out, "png", new File(inputPath.substring(0, inputPath.lastIndexOf(".")) + "_ASCII.png"));
	}

	private static void imgToTxt(String inputPath) throws Exception{
		Converter c = new Converter();
		BufferedImage srcImg = ImageIO.read(new File(inputPath));
		c.imageToASCII(srcImg, 0.75f, inputPath.substring(0, inputPath.lastIndexOf(".")) + ".txt");

	}

	private static void convertVideo(String inputPath) throws Exception {
		Converter c = new Converter();
		ArrayList<BufferedImage> images = c.videoToImages(inputPath);
		System.out.println("Converting:");

		//Hyperthreading is a bitch and won't let us get a real core count
		int cores = Runtime.getRuntime().availableProcessors();
		
		MTWorker[] workers = new MTWorker[cores];
		for (int x = 0; x < workers.length; x++) {
			workers[x] = new MTWorker(images, x, cores, c);
			workers[x].start();
		}

		//Collect all the baby threads together!
		for(MTWorker worker : workers) {
			worker.join();
		}
		
		c.imagesToVideo(inputPath.substring(0, inputPath.lastIndexOf(".")) + "_ASCII", images);
		System.out.println("Finished!");
	}
}
