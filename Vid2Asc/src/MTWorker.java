import java.util.*;
import java.awt.image.BufferedImage;

public class MTWorker extends Thread {
    int total;
    int pos;
    ArrayList<BufferedImage> imageList;
    Converter converter;

    public MTWorker(ArrayList<BufferedImage> imageList, int pos, int total, Converter converter) {
        this.pos = pos;
        this.total = total;
        this.imageList = imageList;
        this.converter = converter;
    } 
	public void run() { 
        try { 
        // pos = (int)Thread.currentThread().getId()%4;
        for(int i = pos; i < this.imageList.size(); i += total)
            if(this.imageList.get(i) != null) {
                this.imageList.set(i, this.converter.imageToColourASCIIImage((this.imageList.get(i)), 3));
            }
        } 
        catch (Exception e) { 
            System.out.println (e); 
            e.printStackTrace();
        } 
    } 
}
