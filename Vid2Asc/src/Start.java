import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bytedeco.javacpp.*;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class Start 
{
	static ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	
	public static void main(String[] args)
	{
		try {
			videoToImages();
			System.out.println("total size is: " + images.size());
			for(int x = 0; x < images.size(); x++)
			{
				if(images.get(x) == null)
					continue;
				System.out.println(x);
				File curr = imageToASCII.imageToASC(images.get(x));
				BufferedImage currImg = AsciiToImage.doIt(curr);
				images.set(x, currImg);
			}
			imagesToVideo();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (java.lang.Exception e)
		{
			// TODO Auto-generated catch block
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
		g.setFormat("mp4");
		g.start();
		while(true)
		{
			Java2DFrameConverter c = new Java2DFrameConverter();
			Frame currFrame = g.grab();
			if(currFrame == null)
				break;
			BufferedImage curr = c.convert(currFrame);
			if (curr == null)
				continue;
			images.add(curr);
		}
		g.stop();
		g.close();
	}
	
	public static void imagesToVideo() throws Exception, org.bytedeco.javacv.FrameRecorder.Exception
	{
		Java2DFrameConverter c = new Java2DFrameConverter();
		FFmpegFrameRecorder recorder;
		recorder = new FFmpegFrameRecorder("C:\\Users\\Beni\\workspace\\Vid2Asc\\src\\Objects\\newVideo.mp4", 3840, 2160);
        recorder.setFormat("mp4");
        recorder.setFrameRate(30);
        recorder.start();
        for (int i = 0; i < images.size(); i++) {
			if(images.get(i) == null)
				continue;
        	Frame curr = c.convert(images.get(i));
            recorder.record(curr, avutil.AV_PIX_FMT_RGB32_1);
        }
        recorder.stop();
        recorder.close();
	}

}
