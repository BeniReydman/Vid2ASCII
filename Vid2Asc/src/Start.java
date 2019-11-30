import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Start 
{
	ArrayList<File> files = new ArrayList<File>();
	
	public static void main(String[] args)
	{
		// int cores = Runtime.getRuntime().availableProcessors();
		// for (int x = 0; x < cores; x++)
		// {
		// 	MultiThread object = new MultiThread();
		// 	object.start();
		// }
		AsciiToImage asti = new AsciiToImage();
		String FILENAME = "C:\\Users\\MagikEh\\Documents\\Vid2ASCII\\Vid2Asc\\output\\orange.txt";
		File inFile = new File(FILENAME);

		try {
			ImageIO.write(asti.doIt(inFile), "png", new File(FILENAME.substring(0, (FILENAME.length()-4)) + ".png"));
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();

		}
	}
	
	public void videoToImages()
	{
		// while()
		// {
		// 	Java2DFrameConverter c = new Java2DFrameConverter;
		// 	c.convert(g.grab());
		// 	if(c == null)
		// 		break;
		// }
	}

}
