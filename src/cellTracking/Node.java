package cellTracking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Node {
	int Index;
	ArrayList<Arc> ForwardArc;
	ArrayList<Arc> BackwardArc;
	HashMap<Integer,String> id1=new HashMap<>();              //FeatureNames
	HashMap<Integer,Double> id2=new HashMap<>();              //FeatureValues
	
	Node(int aIndex)
	{
		Index=aIndex;
	}
	
	
	void addForwardArc(Arc aArc)
	{
		if(this== aArc.GetStart())
			ForwardArc.add(aArc);
		
		
	}
	void addBackwardArc(Arc aArc)
	{
		if(this == aArc.GetEnd())
			BackwardArc.add(aArc);
      		
	}
	int getNumOfBackArcs()
	{
		return BackwardArc.size();
	}
	int getNumOfForArcs()
	{
		return ForwardArc.size();
	}
	Arc getForwardArc(int indx)
	{
		return ForwardArc.get(indx);
	}
	int getIndex()
	{
		return Index;
	}
	
	Arc getBackwardArc(int indx)
	{
		return BackwardArc.get(indx);
	}
	
	
/*	void getFeatureMap(int i,int j) throws IOException
	{
		GroundTruthExtractor ob=new GroundTruthExtractor();
		ob.getValues(i);
		id1=ob.FeatureSet.get(j).featureNames;
		id2=ob.FeatureSet.get(j).featureValues;
	}
	*/
}
