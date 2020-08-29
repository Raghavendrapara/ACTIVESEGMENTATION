package cellTracking;
import java.util.*;

public class Trellis
{
	ArrayList<Arc> aArcs=new ArrayList<>();
	ArrayList<ArrayList<Arc>> tracks=new ArrayList<>();
	double aScore;
// Default constructor used to make it possible to inherit from the class.
   ArrayList<ArrayList<Node>> mNodes=new ArrayList<>();//Layer number wise storage of Nodes for each tiff image 

   int mNumT;//No. Of tiff images/layers

   Trellis(int aNumT)
  {
	mNumT=aNumT;
	for (int t=0; t<mNumT; t++) 
	{
		mNodes.add(new ArrayList<Node>());
	}
  }

//Implement a destructor equivalent for trellis

   void addNode(int aT, Node aNode) //aT denotes position of the image in tiff stack 
   {
	
	   mNodes.get(aT).add(aNode);
	   
   }


   Node getNode(int aT, int aN) 
   {
	return mNodes.get(aT).get(aN);//Layer Number aT and Node number aN
   }


   //Viterbi Algorithm Part
   ArrayList<ArrayList<Arc>> highestScoringPath() //Implementation of the pseudo code for Viterbi Algorithm

   {

	ArrayList<ArrayList<Arc>> bestArcs=new ArrayList<>();		//Arc simply denotes the edges carrying the same naming as used in the paperper
	ArrayList<ArrayList<Double>> bestScores=new ArrayList<>();
	ArrayList<ArrayList<Integer>> prevIndex=new ArrayList<>();		

	for (int t=0; t<mNumT; t++) 
	{ 
		ArrayList<Arc> bestArcsInit=new ArrayList<>();
		
		for(int initial=0;initial<mNodes.get(t).size();initial++)
		bestArcsInit.add(null);
		
		bestArcs.add(bestArcsInit);
		
		ArrayList<Double> tempScore=new ArrayList<Double>();
		ArrayList<Integer> tempPreviousIndex=new ArrayList<Integer>();
		
		for(int i=0;i<mNodes.get(t).size();i++)
		    {
			tempScore.add(-(Double.MAX_VALUE));
			tempPreviousIndex.add(-1);
	            }
	        
		bestScores.add(tempScore);
		prevIndex.add(tempPreviousIndex);  // -1 indicates that the node can not be reached.
	}

	// Set the initial scores to 0.
	ArrayList<Double> tempScoreAt0=bestScores.get(0);
	for (int n=0; n<mNodes.get(0).size(); n++) {
        tempScoreAt0.set(n,1.0*n);
    }
    bestScores.set(0,tempScoreAt0);
    
    
    
    
	// Go through the layers one by one to find the highest scoring path from the beginning of the Trellis to the end.
    for (int t=1; t<mNumT; t++) 
    {
        for (int n=0; n<mNodes.get(t).size(); n++)
	{
             Node node = mNodes.get(t).get(n);
		
		for (int i=0; i<node.getNumOfBackArcs(); i++) 
		{
			              Arc bArc     = node.getBackwardArc(i);
                          int pIndex   = bArc.getStart().getIndex();
                          double score = bestScores.get(t-1).get(pIndex) + bArc.getMigscore();
                          
                          if (i==0 || score > bestScores.get(t).get(n))
                          {
                	
                          
                          bestArcs.get(t).set(n,bArc);
                    
                          bestScores.get(t).set(n,score);

                         
                          prevIndex.get(t).set(n,pIndex);
         
		          }
                }
        }
    }
    HashSet<Integer> visited=new HashSet<>();
    //BackTracking
    while(bestScores.get(mNumT-1).size()!=visited.size()){
   	int endIndex = 0;
   	while(bestScores.get(mNumT-1).get(endIndex)==null) {
   		endIndex++;}
	for (int n=0; n<mNodes.get(mNumT-1).size();n++)
	{
		if(!visited.contains(n) && !visited.contains(endIndex))
		if (bestScores.get(mNumT-1).get(n) > bestScores.get(mNumT-1).get(endIndex)) 
		{
			endIndex = n;
		}
	}

	int maxIndex = endIndex;
        for (int t=mNumT-1; t>=0; t--)
        {
	    aArcs.add(bestArcs.get(t).get(maxIndex));
        maxIndex = prevIndex.get(t).get(maxIndex);
     //   System.out.println(maxIndex);
        }
        bestScores.get(mNumT-1).set(endIndex,null);
        visited.add(endIndex);
tracks.add(aArcs);
aArcs=new ArrayList<>();
   }

	// Set output score.
   
	return tracks;

	//To Delete temporary lists
	
}
}
