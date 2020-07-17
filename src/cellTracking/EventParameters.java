package cellTracking;

import java.util.ArrayList;
import java.util.Collections;

public class EventParameters {

	int mValue;				// Value of the Variable. Usually the number of times that an event occurs.
	int mNumScores;			// The number of predefined scores.
	ArrayList<Double> mScore;
	EventParameters(int aValue, int aNumScores, ArrayList<Double> aScores)
	{
		mScore=new ArrayList<>(aScores.size());
		mValue=aValue;
		mNumScores=aNumScores;
		Collections.copy(mScore,aScores);
		
		
	}
	double GetPlusScore()
	{
		if (mValue < mNumScores-1) {
			return mScore.get(mValue+1) - mScore.get(mValue);
		} else {
		
			return Math.min(mScore.get(mNumScores-1) - mScore.get(mNumScores-2), 0.0);
		}
	}

	double GetMinusScore()
	{
	
		if (mValue < mNumScores) {
		return mScore.get(mValue-1) - mScore.get(mValue);
	    } 
		else {
		
		return Math.max(mScore.get(mNumScores-2) - mScore.get(mNumScores-1), 0.0);
	    }
		
	}

	// Increases the value by 1.
    void plus()
    {
    	mValue++;
    }

	// Decreases the value by 1. The value must not be 0 before the function call.
	void minus()
	{
		if(mValue>0)
			mValue=Math.max(mValue-1,0);
	}

    int getValue()
    {
    	return mValue; 
    }
}