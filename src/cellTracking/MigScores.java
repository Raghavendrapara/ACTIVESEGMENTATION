package cellTracking;

import java.util.ArrayList;

import ij.gui.Roi;

//Computes scores for migration events in cells/blobs of an image sequence.

public class MigScores {
	
	
	// The scores are log-probabilities of migration events.
	
	
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
	private double migMat[][];
	
	
	private double death; //Calculated from Training on Event Histogram
	private ArrayList<ArrayList<Roi>> detections; //From Cell Detection Graph
	
	
    //Use fixed probabilities from Event Histogram
	// If the death probability is set to 0, we do not add any death
	// events at all.   
	public void setDetectionRoi()
	{
		
	}
}