package cellTracking;

import cellTracking.Node;

public class Arc {
	
	Node mStart,mEnd;
	double score;
	Arc(Node aStart,Node aEnd)
	{
		mStart=aStart;
		mEnd=aEnd;
	}
	
	Node getStart()
	{
		return mStart;
	}
	
	Node getEnd()
	{
		return mEnd;
	}
	
	double score()
	{
	return 1.0;	
	}
	

}

