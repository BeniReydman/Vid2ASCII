import java.io.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.*;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import java.util.ArrayList;
// import org.bytedeco.javacv.FrameGrabber.Exception;
/**
 * This class takes some text file and turns it into an ascii.
 */
public class Converter {

    public Converter(){}

    public BufferedImage imgToAscii(File inFile) throws Exception{
        BufferedReader fileIn = new BufferedReader(new FileReader(inFile));
        // fileIn.mark(4000000);
        String testLength = fileIn.readLine();
        if(testLength.length() == 0)
            throw new Exception("Input file must have some text in it.");

        int lines = 1;
        while(fileIn.readLine() != null)
            lines++;
        // fileIn.reset();
        fileIn.close();
        fileIn = new BufferedReader(new FileReader(inFile));

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        //12pt font gives 7w 11h characters in px.
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int charWidth = fm.stringWidth("#");
        int charHeight = fm.getAscent()-2;
        g2d.dispose();

        img = new BufferedImage(charWidth * testLength.length(), charHeight * lines, BufferedImage.TYPE_INT_RGB);

        g2d = img.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        // g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.fillRect(0, 0, (charWidth * testLength.length()), (charHeight*lines));
        g2d.setColor(Color.BLACK);

        String inputLine = fileIn.readLine();
        lines = 1;

        while(inputLine != null) {
            g2d.drawString(inputLine, 0, charHeight*lines);
            lines++;
            inputLine = fileIn.readLine();
        }
        g2d.dispose();
        fileIn.close();
        return img;
    }

    //http://codehustler.org/blog/java-to-create-grayscale-images-icons/
    public BufferedImage toGreyscale(BufferedImage srcImg) {
        BufferedImage image = new BufferedImage( srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY );  
        Graphics g = image.getGraphics();  
        g.drawImage( srcImg, 0, 0, null );  
        g.dispose();
        return image;
    }

    public BufferedImage imageToASCIIImage(BufferedImage srcImg, int outW, int outH, int fontSize) throws Exception{

        //Force the alpha state to pre-Multiply for greyscale conversion.
        srcImg.coerceData(true);

        BufferedImage asciiImg = new BufferedImage(1, 1, srcImg.getType());
        Graphics g = asciiImg.createGraphics();
        Font font = new Font("Monospaced", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int charWidth = fm.stringWidth("#");
        int charHeight = fm.getAscent()-2;
        g.dispose();

        BufferedImage scaledImg = new BufferedImage(outW/charWidth, outH/charHeight, BufferedImage.TYPE_BYTE_GRAY);
        g = scaledImg.createGraphics();
        g.drawImage(srcImg, 0, 0, scaledImg.getWidth(), scaledImg.getHeight(), null);
        g.dispose();

        // char[] charset = {' ', '.', '*', ':', 'o', '&', '8', '#', '@'};
        char[] charset = {' ', '.', '\'', '\\', '`', '^', '"', ',', ':', ';', 'I', 'l', '!', 'i', '>', '<', '~', '+', '_', '-', '?', ']', '[', '}', '{', '1', ')', '(', '|', '/', 't', 'f', 'j', 'r', 'x', 'n', 'u', 'v', 'c', 'z', 'X', 'Y', 'U', 'J', 'C', 'L', 'Q', '0', 'O', 'Z', 'm', 'w', 'q', 'p', 'd', 'b', 'k', 'h', 'a', 'o', '*', '#', 'M', 'W', '&', '8', '%', 'B', '@', '$'};

        byte[] pixels = ((DataBufferByte) scaledImg.getRaster().getDataBuffer()).getData();
        String output = "";

        asciiImg = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_RGB);
        g = asciiImg.createGraphics();
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, asciiImg.getWidth(), asciiImg.getHeight());
        g.setColor(Color.BLACK);
        
        int lines = 1;
        //Need to skip b/c RGB byte values.
        for(int i = 0; i < pixels.length; i++) {
            //TODO: May look into skipping greyscale and using Benny's conversion rate (looks to near-match Human colour perception):
            //grayLevel = (int)(pixels[pixel] * 0.114 + pixels[pixel + 1] * 0.587 + pixels[pixel + 2] * 0.299);
            output += charset[(int)Math.floor((charset.length-1) * ((pixels[i]+128)/255.0))];//(Math.pow((pixels[i]+128),2) / 65535.0))];

            if(i%scaledImg.getWidth()==0) {
                g.drawString(output, 0, charHeight*lines);
                output = "";
                lines++;
            }
        }
        g.dispose();
        return asciiImg;
    }

    public BufferedImage imageToColourASCIIImage(BufferedImage srcImg, int fontSize) throws Exception {
        BufferedImage asciiImg = imageToASCIIImage(srcImg, srcImg.getWidth(), srcImg.getHeight(), fontSize);
        //This will give black areas of the ASCII image the colour value of the source image.
        int[] sPix = srcImg.getRGB(0, 0, srcImg.getWidth(), srcImg.getHeight(), null, 0, srcImg.getWidth());
        int[] aPix = asciiImg.getRGB(0, 0, asciiImg.getWidth(), asciiImg.getHeight(), null, 0, asciiImg.getWidth());
        if(sPix.length != aPix.length)
            throw new Exception("Output img size doesn't match input img size!\n\t" + srcImg.getWidth() + ", " + srcImg.getHeight() + " -> " + asciiImg.getWidth() + " " + asciiImg.getHeight() );
        int white = -16777216;
        //aPix = [-16777216 | 0]
        for(int i = 0; i < sPix.length; i++) {
            // sPix[i] = sPix[i] | aPix[i];//WORKS WELL ENOUGH
            sPix[i] = aPix[i] == white ? sPix[i]: -1;
        }
        // System.out.println(aPix.length);
        asciiImg.setRGB(0, 0, asciiImg.getWidth(), asciiImg.getHeight(), sPix, 0, asciiImg.getWidth());

        return asciiImg;
    }

    public File imageToASCII(BufferedImage srcImg, float scale, String filePath) throws Exception{
        
        File outFile = new File(filePath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));


        //Force the alpha state to pre-Multiply for greyscale conversion.
        srcImg.coerceData(true);

        BufferedImage asciiImg = new BufferedImage(1, 1, srcImg.getType());
        Graphics g = asciiImg.createGraphics();
        Font font = new Font("Monospaced", Font.BOLD, 12);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int charWidth = fm.stringWidth("#");
        int charHeight = fm.getAscent();
        g.dispose();

        int scaleH = (int)(srcImg.getHeight()*scale)/charHeight;
        int scaleW = (int)(srcImg.getWidth()*scale)/charWidth;

        BufferedImage scaledImg = new BufferedImage(scaleW, scaleH, BufferedImage.TYPE_BYTE_GRAY);
        g = scaledImg.createGraphics();
        g.drawImage(srcImg, 0, 0, scaledImg.getWidth(), scaledImg.getHeight(), null);
        g.dispose();

        char[] charset = {' ', '.', '*', ':', 'o', '&', '8', '#', '@'};
        
        byte[] pixels = ((DataBufferByte) scaledImg.getRaster().getDataBuffer()).getData();
        String output = "";
        g.dispose();

        //Need to skip b/c RGB byte values.
        for(int i = 0; i < pixels.length; i++) {
            //TODO: May look into skipping greyscale and using Benny's conversion rate (looks to near-match Human colour perception):
            //grayLevel = (int)(pixels[pixel] * 0.114 + pixels[pixel + 1] * 0.587 + pixels[pixel + 2] * 0.299);
            output += charset[(int)Math.floor((charset.length-1) * ((pixels[i]+128)/255.0))];//(Math.pow((pixels[i]+128),2) / 65535.0))];

            if(i%scaledImg.getWidth()==0) {
                output += '\n';
                bw.write(output);
                output = "";
            }
        }
        // System.out.println("");
        bw.close();
        return outFile;
    }
    
	public ArrayList<BufferedImage> videoToImages(String inPath) throws Exception {
        //TODO: Figure out how to pull the audio from the originial file and stuff it into the output file. 
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		FFmpegFrameGrabber g = new FFmpegFrameGrabber(inPath);
		g.setFormat("");
		g.start();
		while (true) {
			Java2DFrameConverter c = new Java2DFrameConverter();
			Frame currFrame = g.grab();
			if (currFrame == null)
				break;
			BufferedImage curr = c.convert(currFrame);
			if (curr == null)
				continue;
			images.add(curr);
		}
		g.stop();
		g.close();
		return images;
	}

	public void imagesToVideo(String outPath, ArrayList<BufferedImage> images) throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
		Java2DFrameConverter c = new Java2DFrameConverter();
		FFmpegFrameRecorder recorder;
		recorder = new FFmpegFrameRecorder(outPath + ".mp4", 1280*2, 720*2);
		recorder.setFormat("mp4");
		recorder.setFrameRate(30);
		recorder.start();
		for (int i = 0; i < images.size(); i++) {
			if (images.get(i) == null)
				continue;
			Frame curr = c.convert(images.get(i));
			recorder.record(curr, avutil.AV_PIX_FMT_RGB32_1);
        }
		recorder.stop();
        recorder.close();
	}

}