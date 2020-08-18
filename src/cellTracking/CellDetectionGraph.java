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
	ArrayList<ArrayList<Arc>> track;
	CellDetectionGraph(ImagePlus images[])
	{
	
		detectionRois=new ArrayList<>();
		imagePlusArray=images;
		trellis=new Trellis(images.length);
		
	}
	
	private void createGraph()
	{
		track=trellis.highestScoringPath();
		
	}
	
	private void setDetections()
	{
		System.out.println(imagePlusArray.length);
		int aFrame=0;
		RoiManager roiman=new RoiManager();
		for(ImagePlus image:imagePlusArray)
		{
			int aNodePos=0;
			
			//Uses GroundTruthExtractor Class for bit by bit extraction of ROIs for a ImagePlus Object 
			ArrayList<Roi> tempRoiList = GroundTruthExtractor.runextracter(image,roiman, 1, 255, 0);
			detectionRois.add(tempRoiList);
			System.out.println(tempRoiList.size());
			//Iterate over Rois for the current Frame
			for(Roi temp:tempRoiList)
			{
				trellis.addNode(aFrame,new Node(aNodePos++,temp));
			}
		
			
			if(aFrame>0)
			{
				for(Node backs:trellis.mNodes.get(aFrame-1))
				{
					for(Node curr:trellis.mNodes.get(aFrame))
					{
						curr.addBackwardArc(new Arc(backs,curr));
					}
				}
			}
			aFrame++;
			
		}
		numOfRois=roiman.getRoisAsArray().length;
		//trellis.highestScoringPath();
		System.out.println(numOfRois);
		//System.out.println(trellis.aScore);
	}
	
	public static void main(String args[])
	{
		//Currently a pre-decided path for main testing
        String inputPath="/home/raghavendra/Downloads/PhC-C2DH-U373/01_GT/SEG/"; 
		
		GroundTruthExtractor extracter= new GroundTruthExtractor();
		List<String> images=extracter.loadImages(inputPath);
		ImagePlus iptemp[]=new ImagePlus[images.size()];
		//Iterate over the Stack
		for(int frameNum=0;frameNum<images.size();frameNum++) {
		ImagePlus currentImage= IJ.openImage(inputPath+images.get(frameNum));	
	    iptemp[frameNum] = currentImage;
		}
		//Initialize with the stack as ImagePlus Array
		CellDetectionGraph cellDetGra=new CellDetectionGraph(iptemp);
		cellDetGra.setDetections();
		System.out.println(cellDetGra.detectionRois.size());
		cellDetGra.createGraph();
		System.out.println("Done");

	}
}
