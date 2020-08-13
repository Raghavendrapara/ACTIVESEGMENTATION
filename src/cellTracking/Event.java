package cellTracking;

import java.util.ArrayList;

public class Event {
		// Creates an Event that has no score associated with it.
		// 
		// Inputs:
		// aStartState - The state that the event links from.
		// aEndState - The state that the event links to.
		Event(State aStartState, State aEndState){
			
		}

		// Creates an Event that has different scores associated different occurrance counts.
		// 
		// Inputs:
		// aStartState - The state that the event links from.
		// aEndState - The state that the event links to.
		// aValue - The number of times that the event occurs when it is created.
		// aScores - The scores of the event occuring a given number of times. The first element
		// specifies the score associated with the Event occurring once.
		// aNumScores - The length of aScore.
		Event(State aStartState, State aEndState, int aValue, int aNumScores, double aScores){
		
		}
		

		// Should add an additional occurrance of the event to the Tree aTree. aPrint specifies if
		// the parameters of the Event should be printed. This is to allow Swaps to supress the
		// printouts of other Events that they execute.
		 void execute(TrackTree aTree, ArrayList<CellNode> aEndCellNodes) {
		}

		 // Adds another occurrance of the event to a cell lineage tree, by linking it to
		 // an existing CellNode associated with aEndState.
		 //
		 // Inputs:
		 // aTree - Lineage tree that will get an additional event.
		 // aCell - CellNode associated with aEndState.
		 void execute(TrackTree aTree, ArrayList<CellNode> aEndCellNodes, CellNode aCell) {
		}

		State getEndState() { return mEndState; }

		State getStartState() { return mStartState; }
	    
		// Checks that it is allowed to link from aStateFrom to aStateTo using the Event.
		// This is used when chains of CellNodes are created, to make sure that only valid
		// links are created.

		// Returns true if it is ok to perform a swap where the current Event is the first
		// event and aEvent is the second Event.
		// TODO: MAKE THIS FUNCTION TAKE A CELLNODE AS INPUT.

		State mEndState;		// The state linked to by the Event.
		State mStartState;		// The state linked from by the Event.
	};