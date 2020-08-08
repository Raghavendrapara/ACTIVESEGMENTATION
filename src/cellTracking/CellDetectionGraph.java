package cellTracking;

import java.util.ArrayList;
import java.util.List;

import activeSegmentation.feature.GroundTruthExtractor;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

public class CellDetectionGraph {
	
	private ArrayList<ArrayList<Roi>> detectionRois;
	private ImagePlus imagePlusArray[];
	private int numOfRois;
	private Trellis trellis;
	CellDetectionGraph(ImagePlus images[])
	{
	
		detectionRois=new ArrayList<>();
		imagePlusArray=images;
		trellis=new Trellis(images.length);
		
	}
	
	private void createGraph()
	{
		
		
	}
	
	private void setDetections()
	{
		System.out.println(imagePlusArray.length);
		int aFrame=0,aNodePos=0;
		RoiManager roiman=new RoiManager();
		for(ImagePlus image:imagePlusArray)
		{
			
			
			ArrayList<Roi> tempRoiList = GroundTruthExtractor.runextracter(image,roiman, 1, 255, 0);
			detectionRois.add(tempRoiList);
			for(Roi temp:tempRoiList)
				trellis.addNode(aFrame,new Node(aNodePos++,temp));
			aFrame++;
		}
		numOfRois=roiman.getRoisAsArray().length;
	}
	
	public static void main(String args[])
	{
        String inputPath="/home/raghavendra/Downloads/PhC-C2DH-U373/01_ST/SEG/"; 
		
		GroundTruthExtractor extracter= new GroundTruthExtractor();
		List<String> images=extracter.loadImages(inputPath);
		ImagePlus iptemp[]=new ImagePlus[images.size()];
		for(int frameNum=0;frameNum<images.size();frameNum++) {
		ImagePlus currentImage= IJ.openImage(inputPath+images.get(frameNum));	
	    iptemp[frameNum] = currentImage;
		}
		CellDetectionGraph cellDetGra=new CellDetectionGraph(iptemp);
		cellDetGra.setDetections();
		System.out.println(cellDetGra.detectionRois.size());
	}
}
