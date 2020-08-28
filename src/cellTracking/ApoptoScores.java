package cellTracking;

import java.util.ArrayList;

import ij.gui.Roi;

//Computes scores for death events in cells/blobs of an image sequence.

public class ApoptoScores {
	
	
	// The scores are log-probabilities of death events.
	
	
	    /* 
     	 N x 4 matrix where N is the total number of Detections in all frames
         except the last. Only possible death events are included in the
         list, so if the death probability is set to 0, an empty list will
         be returned.
      
	     The elements of the matrix are:
	     Frame index .
	     Index of the detection in frame index 
	     Log-probability that no death event takes place in the detection
	     Log-probability that at least one death event takes place in the detection.
	    
	    */
	private double deathMat[][];
	
	
	private double death; //Calculated from Training on Event Histogram
	private ArrayList<ArrayList<Roi>> detections; //From Cell Detection Graph
	
	
    //Use fixed probabilities from Event Histogram
	// If the death probability is set to 0, we do not add any death
	// events at all.   
	public void setDetectionRoi()
	{
		
	}
	public double[][] getDeathprob(){
		
	        if(death==0)
	        return deathMat;
	   
	       // There is no need to find death scores in the last frame.
	        int k=0;//Counter for a detection Roi
            for(int frameNum=0;frameNum<detections.size()-1;frameNum++)
	        for(int detection=0;detection<detections.get(frameNum).size();detection++)
	        {
	    	deathMat[k][0]   = frameNum;
		    deathMat[k][1]   = detection;
		    deathMat[k][2]	 = Math.max(Math.log(death),-100000);			
		    deathMat[k][3]   = Math.max(Math.log(1-death),-100000);
		    k+=1;
	        }
            
    return deathMat;
	
	}
	
	public void setDeathProb(double deathProb)
	{
		
		death=deathProb;
	}
}