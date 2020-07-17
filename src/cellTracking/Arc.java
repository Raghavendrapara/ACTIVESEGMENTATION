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
	
	Node GetStart()
	{
		return mStart;
	}
	
	Node GetEnd()
	{
		return mEnd;
	}
	
	double score()
	{
	return 1.0;	
	}
	

}

