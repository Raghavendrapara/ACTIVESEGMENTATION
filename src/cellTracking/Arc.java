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
	
	void setScore(double score)
	{
	
		this.score=score;	
	}
	double score()
	{
		
		double d= Math.log(Math.random());
		return d;
	}
	double getMigscore()
	{
		
		double centroidStart[] = mStart.roi.getContourCentroid();
		double centroidEnd[]   = mEnd.roi.getContourCentroid();
		double x1= centroidStart[0];
		double x2= centroidEnd[0];
		double y1= centroidStart[1];
		double y2= centroidEnd[1];
		double dis=Math.sqrt(Math.abs( ( (x2-x1)*(x2-x1)-(y2-y1)*(y2-y1) ) ));
	//	System.out.println(dis);
		return Math.log(1/dis);
	}

}

