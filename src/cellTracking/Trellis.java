package cellTracking;
import java.util.*;

public class Trellis
{
	ArrayList<Arc> aArcs;
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
	
	   ArrayList<Node> arr=mNodes.get(aT);
	   arr.add(aNode);
	   mNodes.set(aT,arr);
	   
   }


   Node getNode(int aT, int aN) 
   {
	return mNodes.get(aT).get(aN);//Layer Number aT and Node number aN
   }


   void highestScoringPath() //Implementation of the pseudo code for Viterbi Algorithm

   {

	ArrayList<ArrayList<Arc>> bestArcs=new ArrayList<>();		//Arc simply denotes the edges carrying the same naming as used in the paperper
	ArrayList<ArrayList<Double>> bestScores=new ArrayList<>();
	ArrayList<ArrayList<Integer>> prevIndex=new ArrayList<>();		

	for (int t=0; t<mNumT; t++) 
	{
		bestArcs.add(new ArrayList<Arc>(mNodes.size()));
		ArrayList<Double> tempScore=new ArrayList<Double>(mNodes.get(t).size());
		ArrayList<Integer> tempPreviousIndex=new ArrayList<Integer>(mNodes.get(t).size());
		
		for(int i=0;i<tempScore.size();i++)
		    {
			tempScore.set(i,-(Double.MAX_VALUE));
			tempPreviousIndex.set(i, -1);
	            }
	        
		bestScores.add(tempScore);
		prevIndex.add(tempPreviousIndex);  // -1 indicates that the node can not be reached.
	}

	// Set the initial scores to 0.
	ArrayList<Double> tempScoreAt0=bestScores.get(0);
	for (int n=0; n<mNodes.get(0).size(); n++) {
        tempScoreAt0.add(n,0.0);
    }
    bestScores.set(0,tempScoreAt0);
	// Go through the layers one by one to find the highest scoring path from the beginning of the Trellis to the end.
    for (int t=1; t<mNumT; t++) 
    {
        for (int n=0; n<mNodes.get(t).size(); n++)
	{
             Node node = mNodes.get(t).get(n);
		
		for (int i=0; i<node.getNumOfForArcs(); i++) 
		{
			              Arc bArc     = node.getBackwardArc(i);
                          int pIndex   = bArc.getStart().getIndex();
                          double score = bestScores.get(t-1).get(pIndex) + bArc.score();
                          
                          if (i==0 || score > bestScores.get(t).get(n))
                          {
                	
                          ArrayList<Arc> tempArc=bestArcs.get(t);
                          tempArc.set(n,bArc);
                          bestArcs.set(t,tempArc);
                    
                          ArrayList<Double> tempScore=bestScores.get(t);
                          tempScore.set(n,score);
                          bestScores.set(t,tempScore);

                          ArrayList<Integer> tempPreviousPos=prevIndex.get(t);
                          tempPreviousPos.set(n,pIndex);
                          prevIndex.set(t,tempPreviousPos);
         
		          }
                }
        }
    }
    //BackTracking
   	int endIndex = 0;
	for (int n=0; n<mNodes.get(mNumT-1).size();n++)
	{
		if (bestScores.get(mNumT-1).get(n) > bestScores.get(mNumT-1).get(endIndex)) 
		{
			endIndex = n;
		}
	}

	int maxIndex = endIndex;
        for (int t=mNumT-1; t>0; t--)
        {
	    aArcs.add(bestArcs.get(t).get(maxIndex));
        maxIndex = prevIndex.get(t).get(maxIndex);
        }

	// Set output score.
    aScore = bestScores.get(mNumT-1).get(endIndex);

	//To Delete temporary lists
	
}
}
