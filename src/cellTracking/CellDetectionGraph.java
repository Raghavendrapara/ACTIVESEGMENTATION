package cellTracking;

import java.util.ArrayList;
import java.util.List;

import activeSegmentation.feature.FeatureManager;
import activeSegmentation.feature.GroundTruthExtractor;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

public class CellDetectionGraph {
	
	private ArrayList<ArrayList<Roi>> detectionRois=new ArrayList<>();;
	private ImagePlus imagePlusArray[];
	private int numOfRois;
	private Trellis trellis;
	private ArrayList<ArrayList<Arc>> track;
	private FeatureManager featureMan;
	private RoiManager roiman;
	
	public CellDetectionGraph(RoiManager roiman,FeatureManager featureman)
	{
		 ArrayList<ImagePlus> imageList=featureman.getImageList(); 
			
			//GroundTruthExtractor extracter= new GroundTruthExtractor();
			//List<String> images=extracter.loadImages(inputPath);
			ImagePlus iptemp[]=new ImagePlus[imageList.size()];
			//Iterate over the Stack
			for(int frameNum=0;frameNum<imageList.size();frameNum++) {
			//ImagePlus currentImage= IJ.openImage(inputPath+images.get(frameNum));	
		    iptemp[frameNum] = imageList.get(frameNum);
			}
			imagePlusArray=iptemp;
		trellis=new Trellis(imagePlusArray.length);
		this.roiman=roiman;
		setFeatureMan(featureman);
	}
	
	
	
	private void createGraph()
	{
		track=trellis.highestScoringPath();
       
		
	}
	
	private void setDetections()
	{
		//System.out.println(imagePlusArray.length);
		int aFrame=0;
	//	RoiManager roiman=new RoiManager();
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
		//System.out.println(numOfRois);
		//System.out.println(trellis.aScore);
	}
	
	public ArrayList<ArrayList<Roi>> runTrack()
	{
		//Currently a pre-decided path for main testing
        
		//Initialize with the stack as ImagePlus Array
		//CellDetectionGraph cellDetGra=new CellDetectionGraph(iptemp);
		this.setDetections();
		//System.out.println(this.detectionRois.size());
		this.createGraph();
		ArrayList<ArrayList<Roi>> trackRois=new ArrayList<>();
		for(ArrayList<Arc> tempArcs:this.track)
		{
			ArrayList<Roi> tempNodes=new ArrayList<>();
			for(Arc arc:tempArcs)
			{
				if(arc!=null)
				{
					tempNodes.add(arc.getStart().roi);
				}
			}
			trackRois.add(tempNodes);
		}
		
		IJ.log("Done");
		featureMan.setTrackSet(trackRois);
		return trackRois;

	}

	public FeatureManager getFeatureMan() {
		return featureMan;
	}

	public void setFeatureMan(FeatureManager featureMan) {
		this.featureMan = featureMan;
	}
}
