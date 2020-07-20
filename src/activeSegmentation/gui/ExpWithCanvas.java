package activeSegmentation.gui;

import java.awt.Color;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;


public class ExpWithCanvas {
public static void main(String args[])
{
		
	ImagePlus next=IJ.openImage("/home/raghavendra/Downloads/PhC-C2DH-U373/01_ST/SEG/man_seg000.tif");
JFrame fframe=new JFrame();
	JPanel frame=new JPanel();

	JPanel panel = new JPanel();
	panel.setLayout(null);
	panel.setBounds(100,100,10, 10);
	panel.setBackground(Color.GRAY);
	
    MyCanvas mc=new MyCanvas(next,250,250,00,00);
	
	
//	frame.add(new JLabel(jj));
	
fframe.add(mc);
fframe.setSize(1000,1000);
fframe.setVisible(true);
	
}
}
