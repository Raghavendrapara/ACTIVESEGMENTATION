package activeSegmentation.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Wand;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import activeSegmentation.feature.FeatureManager;


/**
 * Based on  Custom canvas by
 * @author Ignacio Arganda-Carreras and Johannes Schindelin
 */

public class SimpleCanvas extends OverlayedImageCanvas {
	/**
	 * default serial version UID
	 */
	private static final long serialVersionUID = 1L;
    FeatureManager featureManager;
	ImageProcessor ip;
	
	public SimpleCanvas(ImagePlus imp,FeatureManager featureMan)	{
		super(imp);
		ip=imp.getProcessor();
		Dimension dim = new Dimension(Math.min(512, imp.getWidth()), Math.min(512, imp.getHeight()));
		setMinimumSize(dim);
		setSize(dim.width, dim.height);
		setDstDimensions(dim.width, dim.height);
		/*
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				repaint();
			}
		});*/
		this.featureManager=featureMan;
	 	featureManager.setTrackProcessor(ip);
	}
	
	//@Override
	//public void setDrawingSize(int w, int h) {}

	public void setDstDimensions(int width, int height) {
		super.dstWidth = width;
		super.dstHeight = height;
		// adjust srcRect: can it grow/shrink?
		int w = Math.min((int)(width  / magnification), imp.getWidth());
		int h = Math.min((int)(height / magnification), imp.getHeight());
		int x = srcRect.x;
		if (x + w > imp.getWidth()) x = w - imp.getWidth();
		int y = srcRect.y;
		if (y + h > imp.getHeight()) y = h - imp.getHeight();
		srcRect.setRect(x, y, w, h);
		repaint();
	
	}
	
	
	private  MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {

			if (mouseEvent.getClickCount() == 1) {
				Point p=MouseInfo.getPointerInfo().getLocation();

				double X=p.getX();
				double Y=p.getY();
				Wand w=new Wand(ip);
				IJ.doWand((int)X, (int)Y);
				w.autoOutline((int)X, (int)Y);
				int xpoint[]=w.xpoints;
				System.out.println(xpoint[2]);
			
			}

	
	}};
	//@Override
	public void paint(Graphics g) {
		Rectangle srcRect = getSrcRect();
		double mag = getMagnification();
		int dw = (int)(srcRect.width * mag);
		int dh = (int)(srcRect.height * mag);
		g.setClip(0, 0, dw, dh);

		super.paint(g);

		int w = getWidth();
		int h = getHeight();
		g.setClip(0, 0, w, h);

		// Paint away the outside
		g.setColor(getBackground());
		g.fillRect(dw, 0, w - dw, h);
		g.fillRect(0, dh, w, h - dh);
	}


}