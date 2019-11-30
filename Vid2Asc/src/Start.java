import java.io.File;
import java.util.ArrayList;

public class Start 
{
	ArrayList<File> files = new ArrayList<File>();
	
	public static void main(String[] args)
	{
		int cores = Runtime.getRuntime().availableProcessors();
		for (int x = 0; x < cores; x++)
		{
			MultiThread object = new MultiThread();
			object.start();
		}
	}
	
	public void videoToImages()
	{
		while()
		{
			Java2DFrameConverter c = new Java2DFrameConverter;
			c.convert(g.grab());
			if(c == null)
				break;
		}
	}

}
