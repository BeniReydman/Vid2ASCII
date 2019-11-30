import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class imageToASCII
{
	private static int imageX;
	private static int imageY;
	private static int[][] imageConverted;
	//Eat an orange!
	
	public static void main(String[] args) throws IOException
	{
		imageToASC();
	}
	
	public static void imageToASC() throws IOException
	{
		convertToGrey();
		int wD = 280;
		int hT = 612;
		int width = imageX / wD;
		int height = imageY / hT;
		int avg = width * height;
		char[][] asciiPrint = new char[hT][wD];
		
		int a = 0;
		int b = 0;
		int loopCount = 0;
		int loopEnd = wD * hT;
		
		while (loopCount < loopEnd)
		{
			int rgbVal = 0;
			
			for (int c = a, x = (a + width); c < x; c++)
			{
				for (int d = b, y = (b + height); d < y; d++)
				{
					rgbVal += getRGBAvg(imageConverted[c][d]);
				}
			}
			
			int getAsciiAvg = 0;
			if(avg != 0)
				getAsciiAvg = rgbVal / avg;
			asciiPrint[loopCount / wD][loopCount % wD] = getAscii(getAsciiAvg);
			
			a += width;
			// check if next iteration needs to be done
			if((loopCount + 1) % wD == 0)
			{
				b += height;
				a = 0;
			}
			
			loopCount++;
		}
		
		String superAwesomeAscii = "";
		for(int y = 0; y < wD; y++)
		{
			for(int x = 0; x < hT; x++)
			{
				superAwesomeAscii += asciiPrint[x][y];
			}
			superAwesomeAscii += '\n';
		}
		//System.out.print(superAwesomeAscii);
		
		String FILENAME = "c:\\Users\\Beni\\workspace\\Vid2Asc\\output\\orange.txt";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {

			bw.write(superAwesomeAscii);

			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		}
		
	}
	
	public static char getAscii(int rgbVal)
	{
		char str;
		
		//TODO: Change from static to dynamic, so we can set a font and it'll output against that.
        if (rgbVal <= 20.0) {
            str = '@';
        } else if (rgbVal <= 55.0) {
            str = '#';
        } else if (rgbVal <= 75.0) {
            str = '8';
        } else if (rgbVal <= 90.0) {
            str = '&';
        } else if (rgbVal <= 120.0) {
            str = 'o';
        } else if (rgbVal <= 155.0) {
            str = ':';
        } else if (rgbVal <= 180.0) {
            str = '*';
        } else if (rgbVal <= 215.0) {
            str = '.';
        } else {
            str = ' ';
        }
        return str;
	}
	
	public static BufferedImage convertToGrey() throws IOException
	{
		BufferedImage image = null;
		imageConverted = null;
		
		// Attempt to read image and change to greyscale
		try
		{
			// Read in Image
			image = ImageIO.read(imageToASCII.class.getResource("/Objects/orange.jpg"));
			
			if(image == null)
			{
				System.out.println("you done goofed up getting image");
				return null;
			}
			imageConverted = convertTo2D(image);
			if(imageConverted == null)
			{
				System.out.println("you done goofed up converting");
				return null;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Convert image to 2d array of int rgb values
		int x = imageConverted.length;
		int y = imageConverted[0].length;
		imageX = x;
		imageY = y;
		System.out.println("int x: " + x + "\nint y: " + y);
		BufferedImage toSend = intToImg(imageConverted);
		// ByteArrayOutputStream os = new ByteArrayOutputStream();
		// ImageIO.write(toSend,"jpeg", os);
		// InputStream is = new ByteArrayInputStream(os.toByteArray());
		//channel.sendFile("", is, "result.jpeg");
		return toSend;
	}
	
	private static BufferedImage intToImg(int[][] pixels)
	{
		int width = pixels[0].length;
		int height = pixels.length;
		BufferedImage pixelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);    
	    pixelImage.setRGB(0, 0, width, height, twoDtoOneD(pixels), 0, width);
	    return pixelImage;
	}
	
	private static int[] twoDtoOneD(int[][] pixels)
	{
		int[] pixelArray = new int[(pixels.length)*(pixels[0].length)];
		int x = pixels.length;
		int y = pixels[0].length;
		int count = 0;
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				pixelArray[count] = pixels[i][j];
				count++;
			}
		}
		return pixelArray;
	}
	
	private static int getRGBAvg(int toConvert)
	{
		return ( (((byte)(toConvert >> 16)) & 0xFF) + (((byte)(toConvert >> 8)) & 0xFF) + (((byte)toConvert) & 0xFF)) / 3;
	}
	
	private static int[][] convertTo2D(BufferedImage image)
	{

		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		int argb = 0;
		int grayLevel;

		int[][] result = new int[height][width];

		final int pixelLength = 3;
		
		// loop through all pixels and convert to int value
		for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
		{
			argb = 0;
			grayLevel = (int)(pixels[pixel] * 0.114 + pixels[pixel + 1] * 0.587 + pixels[pixel + 2] * 0.299);
			argb += (grayLevel & 0xff); // blue
			argb += (((int) grayLevel & 0xff)  << 8); // green
			argb += (((int) grayLevel & 0xff) << 16); // red
			
			result[row][col] = argb;
			
			col++;
			
			// who needs 2 for loops :)
			if (col == width)
			{
				col = 0;
				row++;
			}
			
			if(pixel + 5 >= pixels.length)
				break;
		}

		return result;
	}
}
