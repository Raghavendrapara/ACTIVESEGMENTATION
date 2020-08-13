package cellTracking;

import java.util.ArrayList;

public class State extends Node {

     	// Iterator types returned by get-methods.
        // typedef vector<CellNode*>::iterator CellIterator;

		// Creates a state object in image aT, with index aIndex. The states
		// in image t are supposed to be numbered from 0 to Mt-1, where Mt is the
		// total number of states in image t. This has to be ensured by the caller,
		// but could also be ensured by using a static member or a factory design
		// pattern.
	    
        	// The cells associated with the detection.
			ArrayList<CellNode> mCells;

			// Index of the image in which the detection occurs.
			int mT;

	   State(int aT, int aIndex){
       super(aIndex);
       mT=aT;
       
	   }
	    

		// Adds a cell to the detection. A detection can have many cells passing through it.
		void addCell(CellNode aCell) {
		
			// A cell can not be added if it does not contain the State.
		    if(aCell.getState() == this)
		    mCells.add(aCell);
		
		}
		

		// Returns an iterator to the first CellNode associated with the detection.
		CellNode getBeginCell()
		{
			return mCells.get(0);
		}

		// Returns an iterator to the position after the last CellNode associated with the detection.
		CellNode GetEndCell()
		{
		
			return mCells.get(mCells.size()-1);
					
		}

		// Returns the number of cells currently assoicated with the detection.
		int getNumCells()
		{
			
			return mCells.size();
			
		}

		// The score of going through the State one time less. (NOT USED)
		double getMinusScore()
		{
			return 0.0; 
			
		}

		// The score of going through the state one more time.
		double getPlusScore() 
		{
			return 0.0;
			
		}

		// Returns the index of the image that the detection occurs in.
		int getT() 
		{
			return mT;
			
		}

		// Removes aCell from the list of CellNodes assoicated with the detection. NOT CURRENTLY USED.
		void removeCell(CellNode aCell) {
			
		
			for (int i=0; i<mCells.size(); i++){
				if (mCells.get(i) == aCell) {
					mCells.remove(i);
				}
		
				}
		}
	    

		
		// Updates counters to reflect that the algorithm goes throught the state one time less. (NOT USED)
	    void Minus() { ; }

		// Updates counters to reflect that the algorithm goes throught the state one mote time.
		 void Plus() { ; }
	}

