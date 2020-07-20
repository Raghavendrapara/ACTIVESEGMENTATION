package activeSegmentation.gui;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;

public class MyCanvas extends ImageCanvas  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6676213727309305006L;
	/**
	 * 
	 */
	
   
    int length;int width;int xpos;int ypos;
   
	MyCanvas(ImagePlus img,int length,int width,int xpos,int ypos)
	{
		super(img);
		
		this.length=length;
		this.width=width;
		this.xpos=xpos;
		this.ypos=ypos;
	}
	
	
	public void paint(Graphics g)
	{
		
		
		ImagePlus next=IJ.openImage("/home/raghavendra/Downloads/PhC-C2DH-U373/01_ST/SEG/man_seg000.tif");
		//ImageCanvas icc=next.getCanvas();
        Image i=next.getImage();
		i=i.getScaledInstance(length, width, Image.SCALE_SMOOTH);
		g.drawImage(i,000,000,this);
	}

}


