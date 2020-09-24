package cellTracking;

import java.util.ArrayList;

import ij.gui.Roi;

public class CellDispVariance {

	private Roi consecutiveFrames[][];
	CellDispVariance(Roi consecutiveFrames[][])
	{
		
	}
	
	double getVariance() {
		
		double mean;
		double sum=0.0;
		int cnt=0;
		ArrayList<Double> displacements=new ArrayList<>();
		for(int fixInd=0;fixInd<consecutiveFrames[0].length;fixInd++) {
			
			Roi temp1=consecutiveFrames[0][fixInd];
			if(temp1!=null)
			for(int variInd=0;variInd<consecutiveFrames[1].length;variInd++) {
				
				Roi temp2=consecutiveFrames[1][variInd];
				if(temp2!=null) {
					
					
					sum = sum + roiDistance(temp1,temp2);
					displacements.add(roiDistance(temp1, temp2));
					cnt++;
					
				}
				
			}
		}
		
		mean=sum/cnt;
		sum=0.0;
		for(double curr:displacements)
		{
			sum=sum+Math.pow(curr-mean, 2);
		}
		
		double var = sum/--cnt;
		
		return var;
		
		
	}
	
	double roiDistance(Roi pos1, Roi pos2) {
		
		double cood1[] = pos1.getContourCentroid();
		double cood2[] = pos2.getContourCentroid();
		
		double dis = Math.sqrt(Math.pow((cood1[0]-cood2[0]),2) + Math.pow((cood1[1] - cood2[1]),2));
		
		return dis;
	}
}
