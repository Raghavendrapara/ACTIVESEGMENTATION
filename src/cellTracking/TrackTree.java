package cellTracking;

import java.util.ArrayList;

public class TrackTree {
	
	
		private int stackSize;                       // The number of images in the image sequence
		private int trackNum;					     // The current tracking iteration. Used to give cells the correct iteration number.
		private ArrayList<Node> trackStartingCells;  // Starting nodes in all cell tracks.
		private Node currentCell;			         // Cell node that we are currently adding more cell nodes to.
		
		TrackTree(int aNumT)
		{
			stackSize=aNumT;
			trackNum=1;
			currentCell = null;
		}
		
		

  /*  public Node createCellFirst(IdleState aState) {
	
    	Node cell = new Node(State aState, mIteration);  // Deleted in destructor.
	
    	mFirstCells.push_back(cell);
	
    	mActiveCell = cell;
	
    	return cell;
    }

    public Node createCellLink(Node aLinkCell, Event aEvent) {
    	
	Node newCell = new Node(aEvent.getEndState(), trackNum); 
	aLinkCell.addLink(aEvent, newCell);
	currentCell = newCell;
	return newCell;
	
}
*/
}



