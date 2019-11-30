import java.io.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/**
 * This class takes some text file and turns it into an ascii.
 */
public class AsciiToImage {

/*
    //Charspace : '@#8&o:*. '
        AsciiToImage asti = new AsciiToImage();
		File inFile = new File("./output/orange.txt");

		try {
			ImageIO.write(asti.doIt(inFile), "png", new File("./output/orange.png"));
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();

		}
*/  

    public AsciiToImage(){}

    public static BufferedImage doIt(File inFile) throws Exception{
//        System.out.println("Beginning the read!");
        BufferedReader fileIn = new BufferedReader(new FileReader(inFile));
        fileIn.mark(40000000);
        String testLength = fileIn.readLine();

        if(testLength.length() == 0)
            throw new Exception("Input file must have some text in it.");

        int lines = 1;
        while(fileIn.readLine() != null)
            lines++;
        fileIn.reset();

//        System.out.println(lines + " input lines to read!");
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int charWidth = fm.stringWidth("#");
        int charHeight = fm.getAscent()-2;
        g2d.dispose();
//        System.out.println("Creating output image of size " + charWidth * testLength.length() + " x " + charHeight*lines);
//        System.out.println("From input .txt " + lines + " x " + testLength.length());
//        System.out.println("Total pixels: " + (charWidth * testLength.length())*(charHeight*lines));

        img = new BufferedImage(charWidth * testLength.length(), charHeight * lines, BufferedImage.TYPE_INT_RGB);

        g2d = img.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, (charWidth * testLength.length()), (charHeight*lines));
        g2d.setColor(Color.BLACK);

        String inputLine = fileIn.readLine();
        float totalLines = lines;
        lines = 1;

        while(inputLine != null) {
//            System.out.print("\b\b\b" + (int)(lines*100.0/totalLines) + "%");
            g2d.drawString(inputLine, 0, charHeight*lines);
            lines++;
            inputLine = fileIn.readLine();
        }
//        System.out.println("\nFinishing writing to file...");
        g2d.dispose();
        fileIn.close();
        return img;
    }
}