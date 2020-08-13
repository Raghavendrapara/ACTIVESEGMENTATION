package cellTracking;

import java.util.ArrayList;

public class TrackTree {
	
	
		private int stackSize;                       // The number of images in the image sequence
		private int trackNum;					     // The current tracking iteration. Used to give cells the correct iteration number.
		private ArrayList<CellNode> trackStartingCells;  // Starting nodes in all cell tracks.
		private CellNode currentCell;			         // Cell node that we are currently adding more cell nodes to.
		
		TrackTree(int aNumT)
		{
			stackSize=aNumT;
			trackNum=1;
			currentCell = null;
		}
		
		

       public CellNode createCellFirst(IdleState aState) {
	
    	State a=(State) aState;
    	CellNode cell = new CellNode(a, trackNum);  
	
    	trackStartingCells.add(cell);
	
    	currentCell = cell;
	
    	return cell;
    }

        public CellNode createCellLink(CellNode aLinkCell, Event aEvent) {
    	
	    CellNode newCell = new CellNode(aEvent.getEndState(), trackNum); 
	    aLinkCell.addLink(aEvent, newCell);
	    currentCell = newCell;
     	return newCell;
	
        }
   /*     
        void getIterations(double aIterationA) {

        	// Fill the matrix with -1.
        	for (int t=0; t<mNumT; t++) {
        		for (int c=0; c<getNumCells(); c++) {
        			aIterationA[c*mNumT+t] = -1.0;
        		}
        	}

        	int cIndex = 0;
        	for (CellIterator cIt = GetBeginFirstCell(); cIt!=GetEndFirstCell(); ++cIt) {
        		CellNode cell = cIt;

        		cell = cell->GetNextCell();  // The first CellNode is an IdleState.

        		// Write track to aIterationA.
        		while (true) {
        			State aState = cell.getState();
        			aIterationA[cIndex*mNumT + state->GetT()-1] = (double) cell->GetIteration();
        			
        			// Move to the next CellNode in the Cell unless we have reached the end.
        			// Don't include the IdleState that end all cells except the dividing ones.
        			CellNode nextCell = cell->GetNextCell();
        			if (cell.hasChildren() || (!nextCell.hasNextCell() && !nextCell.hasChildren())) {
        				break;
        			}
        			cell = nextCell;
        		}

        		cIndex++;
        	}
        }
  */      
        void removeFirstCell(CellNode aCell){
        	// Slow way of erasing an element.
        	for (int i=0; i<(int)trackStartingCells.size(); i++){
        		if (trackStartingCells.get(i) == aCell) {
        			trackStartingCells.remove(i);
        			
        		}
        	}

}
}



