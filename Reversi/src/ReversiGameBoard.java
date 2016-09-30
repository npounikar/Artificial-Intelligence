import javax.swing.JOptionPane;

class ReversiGameBoard{

 public int[] stonesArray;
 static int[] allVisisbleSquares;
 static int row=8,col=8;
 static int noOfVisibleSquares = row * col;
 static int BASE = row+2; 
 
 //the values A can have.
 public static int OUT = 88, P1 = 0, P2 = 1, EMPTY = 3;
 final static int GOING = 1,ENDED = 2;
 
 //---------------------------All Arrays ---------------------------------------------------------
 	//the moves already done.
 	int[] moves = new int[128];
 
 	//the number of stones flipped in that move.
 	int[] flips=new int[128];
 
 	//the positions of the stones flipped in that move.
 	int[][] posFlips=new int[128][24];
 
 	//all directions you can flips stones in. Given as the difference in 
 	//1dimensional field numbers.
 	int[] directions={1,-1,BASE,-BASE,BASE+1,BASE-1,-BASE+1,-BASE-1};
 
 	int[] filpCounts;
 	int[] pointsOfPlayers={0,0};
 	int[] possibleMoves=new int[noOfVisibleSquares];
 
 	
 	
 //------------------------ Used by the ComputerPlayer - algorithm ----------------------------------------------------
 	 static int evalvectorlength;
 	 static int noOfDifferentStones;
 	 static int[] typeOfStones;

 	
//---------------------------All varibles local to this class ---------------------------------------------------------
 
 int status; 
 int movesDone; 
 int possibleMovesCount;
 int currentPlayer;
 int freeStoneCount; 
 
 

 
 
//--------------------------------------static block : that runs when all the classes gets loaded --------------------------
 static { 
	 int current=0;
	 int q = 0;
	 allVisisbleSquares = new int[noOfVisibleSquares];
	 
	 for(int j=0; j<col; j++)
		 for(int i=0; i<row; i++)
			 allVisisbleSquares[current++]=coordinates(i,j);
  
	 typeOfStones=new int[(row+2)*(col+2)];
	 
	
	 for(int i=0;i<row/2;i++)  
		 for(int j=0;j<=i;j++){
			 typeOfStones[coordinates(i,j)] = q;
			 typeOfStones[coordinates(row-1-i,j)] = q;
			 typeOfStones[coordinates(i,col-1-j)] = q;
			 typeOfStones[coordinates(row-1-i,col-1-j)] = q;
			 typeOfStones[coordinates(j,i)] = q;
			 typeOfStones[coordinates(j,row-1-i)] = q;
			 typeOfStones[coordinates(row-1-j,i)] = q;
			 typeOfStones[coordinates(row-1-j,col-1-i)] = q;
			 q++;
	}
	noOfDifferentStones = q;
	evalvectorlength = noOfDifferentStones + 3;
	

 }

//create a new Board - constructor
public ReversiGameBoard(){
	 stonesArray = new int[(row+2)*(col+2)];
	 filpCounts=new int[(row+2)*(col+2)];
	 for(int i=0; i<stonesArray.length; i++) {
		 	stonesArray[i] = OUT;
	 }
	 clear();
}
 
 
 //--------------------------------convert from 2dimensional field coordinates to 1dimensional field number.--------------
//get the x coordinate of a square
 static int player1(int r) {
	 int x = r%BASE-1;
	 return x;
 }
 
//get the y coordinate.
 static int player2(int r) {
	 int y = r/BASE-1;
	 return y;
 }
 
//------------------------------------------------- returns P1 if given P2 and vice versa.-------------------------------------
 static int opponent(int currentPlayer)	{
	 int opponent = currentPlayer;
	 if (currentPlayer ==1)
		 opponent = 0;
	 if (currentPlayer ==0)
		 opponent = 1;
	 return opponent;
 }
 

 //------------------------------------------------ functions that returns 1D array index against x,y value--------------------
 static int coordinates(int i,int j)	{
	 return (i+1)+((j+1)*BASE);
 }

 //-------------------------------------------------copy of the Board-----------------------------------------------------------
 public ReversiGameBoard copy(){
	 ReversiGameBoard cloneofBoard=new ReversiGameBoard();
	 
	 cloneofBoard.movesDone=movesDone;
	 cloneofBoard.possibleMovesCount=possibleMovesCount;
	 cloneofBoard.currentPlayer=currentPlayer;
	 cloneofBoard.stonesArray=copyArray(stonesArray);
	 cloneofBoard.pointsOfPlayers=copyArray(pointsOfPlayers);
	 cloneofBoard.moves=copyArray(moves);
	 cloneofBoard.possibleMoves=copyArray(possibleMoves);
	 cloneofBoard.filpCounts=copyArray(filpCounts);
	 
	 return cloneofBoard;  
	 
 }
 

 //copy array
 int[] copyArray(int[] a) {
	 return (int[])a.clone();
 }

 
 
 //------------------------------Getters and setters ---------------------------
 
 
 // set the square with the value
 public void setSquareWithValue(int i,int value)	{
	 stonesArray[i]=value;

 }
 
//get the value of the Field - 2D
public int  getValueOfSquare(int i,int j)	{
	 int index = stonesArray[coordinates(i,j)];
	 return index;
}

 
 //get the value of the Field
 public int  getValueOfSquare(int i)	{
	 int value =  stonesArray[i];
	 return value;
 }
 
 
//get current player moving
public int getPlayer()	{
	 int currPlayer =  movesDone%2;
	 return currPlayer;
}


 

 
 // sets the can array
 void setAbleArray(){

	 pointsOfPlayers[P1] = pointsOfPlayers[P2] = possibleMovesCount=0;
	 freeStoneCount=0;
     currentPlayer=getPlayer();
     for(int i=0; i<noOfVisibleSquares; i++)
    	 if(stonesArray[allVisisbleSquares[i]] == P1) {
    		 pointsOfPlayers[P1]++;
    		 filpCounts[allVisisbleSquares[i]] = 0;
    	} else if(stonesArray[allVisisbleSquares[i]] == P2) {
    		pointsOfPlayers[P2]++;
    		filpCounts[allVisisbleSquares[i]] = 0;
    		}
    	else {
    		filpCounts[allVisisbleSquares[i]] = countNoOfFlipsPossible(allVisisbleSquares[i]);
    		freeStoneCount++;
    		
    		if(filpCounts[allVisisbleSquares[i]] > 0) {
    			possibleMoves[possibleMovesCount++] = allVisisbleSquares[i];
    		}
    }
     if(isGameOver())
    	 status=ENDED;
     else
    	 status=GOING; 
 }

 
//Checks for valid move or a move that player is eligible to play
public boolean isValidMove(int move) {

	boolean valid = false;
	 if (filpCounts[move] > 0) 
		 valid = true; 
	 
	 return valid;
}


 //make move on the board
 public void makeMove(int move){
	 
	 flips[movesDone] = 0;
	 if(status == ENDED)
		 return;
	 if(move != -1) {
		 int otherPlayer = opponent(currentPlayer);
		 int direction;
		 stonesArray[move]=currentPlayer;
		 for(int i=0; i<8; i++){
			 direction = directions[i];
			 if(flipPossibleCount(otherPlayer,move,direction) > 0)
				 for(int d=direction+move; stonesArray[d]==otherPlayer; d+=direction){
					 stonesArray[d] = currentPlayer;
					 posFlips[movesDone][flips[movesDone]++] = d;
				 }//end of for
		 }// end of for
	 }// end of if
	 
	 moves[movesDone++]=move;  
	 setAbleArray();
 }
 
 
 //undo last move
 public void undoMove(){
	 movesDone--;
	 int opponentPlayer=opponent(getPlayer());
	 
	 if(moves[movesDone] != -1)
		 stonesArray[moves[movesDone]] = EMPTY;
	 for(flips[movesDone]--; flips[movesDone] >= 0; flips[movesDone]--)
		 stonesArray[posFlips[movesDone][flips[movesDone]]]=opponentPlayer;
  
	 setAbleArray();
 }
 
 
 

 
 // calculate the no of flips
 public int countNoOfFlipsPossible(int move){
	 if(stonesArray[move] != EMPTY) 
		 return 0;
	 
	 
	 int  sum = 0;
	 int otherPlayer=opponent(currentPlayer);
	 for(int i=0;i<8;i++)
		 sum = sum + flipPossibleCount(otherPlayer,move,directions[i]);
	 
  return sum;
 }
 
 
 
 // no of filps possible in a particular direction for a move made
 public int flipPossibleCount(int otherPlayer,int move,int direction){

	 int sum=0;	 
	 if(stonesArray[direction+move]!=otherPlayer)
		 return 0;
	 
	 for(move = direction+move; stonesArray[move] == otherPlayer; move+=direction)
		 sum++;
	 
	 if(stonesArray[move]==currentPlayer)
		 return sum;
  
	 return 0;
 }
 
 
 

 // return true if humanPlayerWins
 public boolean humanWins() {
	 boolean humanwin = false;
	 if(pointsOfPlayers[0]>pointsOfPlayers[1])
		 humanwin = true;
	
	 return humanwin;
	
}
 
 //return true if computerWins
 public boolean computerWins() {
	 boolean computerwin = false;
	 if (pointsOfPlayers[0]<pointsOfPlayers[1])
		 computerwin = true;
	 
	 return computerwin;
 }
 
 
 // if game is over
 public boolean isGameOver() {
	 boolean gameOver = false;
	 if( freeStoneCount == 0 || (possibleMovesCount==0 && moves[movesDone-1] == -1) )
		 gameOver = true;
	
	 return gameOver;
 }
 
 
 // no of visible fields that are not free
 public int getOccupiedSquares()  {
	 
	 int occupied = pointsOfPlayers[0] + pointsOfPlayers[1];
	 return occupied;
 }
 
 
 // no of stones captured with move  
 public int getflips(int move){
	 int count = filpCounts[move];
	 return count;
 }
 

 //No of stones that can be flipped
 public int getTotalFlipSquares(){
	 int flip = 0;
	 for(int i=0;i<noOfVisibleSquares;i++)
		 flip = flip + filpCounts[allVisisbleSquares[i]];
  
	return flip;
 }
 

 // evaluation of the position
 public int getScore(){
	 int score = pointsOfPlayers[currentPlayer]-pointsOfPlayers[1-currentPlayer];
	 return score;
 }
 

 //no of moves that is possible
 public int getPossibleMoveCount() {
	  
	 return possibleMovesCount;
 }
    
//get the array on the basis if which the situations or the states will be evaluated.
 public int[] getEvalArray(int[] array){
	 
	 if(array==null)
		 array = new int[evalvectorlength];
	 
	 for(int i=0;i<noOfDifferentStones;i++)  
		 array[i]=0;
	 
	 for(int i=0;i<noOfVisibleSquares;i++){
		 int player = stonesArray[allVisisbleSquares[i]];
		 if(player==currentPlayer)
			 array[typeOfStones[allVisisbleSquares[i]]]++;
		 else if(player==1-currentPlayer)
			 array[typeOfStones[allVisisbleSquares[i]]]--;
	 }
	 array[noOfDifferentStones] = getScore();
	 array[noOfDifferentStones+1] = getPossibleMoveCount();
	 array[noOfDifferentStones+2] = getTotalFlipSquares();
	 
  return array; 
 }
 


 

 // returns a line with status
 public String statusString(){
	 
	 if(isGameOver()){
		 if(humanWins()) {
			 JOptionPane.showMessageDialog(null, "Congratulations !! You Won, Compouter Lost !!");
			 return "BLACK : "+pointsOfPlayers[P1]+" versus WHITE : "+pointsOfPlayers[P2]+"  ---- BLACK Wins!";
			 
		 }
		 if(computerWins()) {
			 JOptionPane.showMessageDialog(null, "Sorry, Hard Luck !! Computer Wins, You Lost !!");
			 return "WHITE : "+pointsOfPlayers[P2]+" versus BLACK : "+pointsOfPlayers[P1]+"  ---- WHITE Wins!";
		 }
		 JOptionPane.showMessageDialog(null, "The Game is Drawn !!");
		 return "The game ended in a DRAW";
	 }
	 
	 if(currentPlayer==P1)
		 return "BLACK : "+pointsOfPlayers[P1]+"   v/s    WHITE : "+pointsOfPlayers[P2];
	 
	 return "WHITE : "+pointsOfPlayers[P2]+"   v/s    BLACK : "+pointsOfPlayers[P1];
 	}

 
 
 // clears up the board
 public void clear(){
	 movesDone = 0;

	 for(int i=0; i<noOfVisibleSquares; i++) {
		 stonesArray[allVisisbleSquares[i]]=EMPTY;
	 }
		 setSquareWithValue(coordinates(3,3),P2);
		 setSquareWithValue(coordinates(3,4),P1);
		 setSquareWithValue(coordinates(4,3),P1);
		 setSquareWithValue(coordinates(4,4),P2);  
		 

  setAbleArray();
  status=GOING;
 }
 
 
 
}// End of Class