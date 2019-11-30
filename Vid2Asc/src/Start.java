import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class Start 
{
	static ArrayList<File> files = new ArrayList<File>();
	static ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	
	public static void main(String[] args) 
	{
		try {
			videoToImages();
			imageToASCII.imageToASC(images.get(0));
		} catch (IOException e) {
			e.printStackTrace();

		}
		
		int cores = Runtime.getRuntime().availableProcessors();
		for (int x = 0; x < cores; x++)
		{
			MultiThread object = new MultiThread();
			object.start();
		}
	}
	
	public static void videoToImages() throws Exception
	{
		FFmpegFrameGrabber g = new FFmpegFrameGrabber("C:\\Users\\Beni\\workspace\\Vid2Asc\\src\\Objects\\video.mp4");
		g.setFormat("");
		g.start();
		while(true)
		{
			Java2DFrameConverter c = new Java2DFrameConverter();
			Frame currFrame = g.grab();
			if(currFrame == null)
				break;
			BufferedImage curr = c.convert(currFrame);
			images.add(curr);
		}
		g.stop();
		g.close();
	}

}
