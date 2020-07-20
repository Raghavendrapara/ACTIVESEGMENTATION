package activeSegmentation.gui;

import java.awt.Image;

import javax.swing.JFrame;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;


public class ExpWithCanvas {
public static void main(String args[])
{
		
	ImagePlus next=IJ.openImage("/home/raghavendra/Downloads/PhC-C2DH-U373/01_ST/SEG/man_seg000.tif");
	JFrame frame=new JFrame();
    MyCanvas mc=new MyCanvas(next,300,300,00,00);
	frame.add(mc);
	frame.setSize(1000,1000);
//	frame.add(new JLabel(jj));
	frame.setVisible(true);

	
}
}
