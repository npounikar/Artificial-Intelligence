import javax.swing.JOptionPane;

class Player implements Runnable{
 
	ReversiGameBoard realBoard,board;
	int side;
	int maximumLevel;
 

	/*for use in a parallel thread (applet mode)*/
	ReversiGameBoardView bview;
	long time;
	
	//evaluation string used to evaluate situation
	public String evalString="8 0 -7 2 -8 2 0 -2 1 -6 2 3 1 -2 1 1 -2 0 -1 0 1 -2 -1 4 3 5 53 best";
 
	int[] out;
	int[] informedData=new InformedData(evalString).arr;
	int kickOff;

	
	
public Player(ReversiGameBoard gb,int side,ReversiGameBoardView gbv){  
	realBoard = gb;
	bview = gbv;  
	// set the level of fore-seeing the states
	
	setForwardSeeingFactor(bview.aiLevel);	  	
}
	 
 
public Player() {
	
}


// sets the factors that will set the level for alpha beta algo to see forward
 public void setForwardSeeingFactor(int factor){
	 
	 if(factor > 6) 
	  factor = 6;
  
	 if(factor < 1) 
	  factor = 1;
  
  maximumLevel = factor;
  JOptionPane.showMessageDialog(null, "Computer level is set to : "+maximumLevel);
 }
 

 // set informed data
 public void setInformedData(int[] weight) {
	 informedData = weight;
 }



 // to start the separate thread for the player
 public void startSeparateThread(String str){
	 new Thread(this).start();
 }
 public void run(){
	 
	setTime();
	int move=getBestStableMove();
	while(getTime()<4000) {
		try{
			if(maximumLevel < 3)
				Thread.sleep(50);
			if(maximumLevel == 3)
				Thread.sleep(100);
			if(maximumLevel == 4)
				Thread.sleep(200);
			if(maximumLevel == 5)
				Thread.sleep(300);
			if(maximumLevel == 6)
				Thread.sleep(400);
		}catch(Exception e){
		
			}
	}
	bview.answer("Your Turn, Make Move!!",move);
 }
	  

 
 // Returns the best Move
 public int getBestStableMove(){
	 
	 board = realBoard.copy();  
	 if(board.possibleMovesCount == 0)
		 	return -1;
	 
	 setKickOff();
	 int x = board.possibleMovesCount;
	 int value, best=-1, alpha=-9999;
	 
	 for(int i = 0; i < x; i++) {	
		 board.makeMove(board.possibleMoves[i]);
		 value =-doAlphaBeta(-9999,-alpha,1);

		 board.undoMove();
		 if(value > alpha) { 
			 alpha=value;
			 best = i;
		 }
	 }
	 return board.possibleMoves[best];
 }
 
 
 // determines the window of the eval informed data
 void setKickOff() {
	 if(board.getOccupiedSquares()<informedData[informedData.length-1])
		 kickOff=0;
	 else 
		 kickOff=out.length;
 }
 

 public int getScore(){
	 int score = 0;
	 out = board.getEvalArray(out);
	 for(int i=0; i < out.length; i++)
		 score+=informedData[kickOff+i]*out[i];
  
	 return score;
 }
 
 
 // tells how good the current situation is
 public int doAlphaBeta(int alpha, int beta, int level) {
	 int number;

	 if(level >= maximumLevel || board.possibleMovesCount==0) 
		 return getScore();
	 
	 for(int i = 0; i < board.possibleMovesCount; i++) {  
		 board.makeMove(board.possibleMoves[i]);
		 number =-doAlphaBeta(-beta,-alpha,level+1);
      
     board.undoMove();
     if(number > beta)
        return number;
     if(number > alpha)
        alpha = number;
    }
  return alpha;
 }
 
 

 //get milliseconds
 public int getTime() {
	 return (int)(System.currentTimeMillis()-time);
 }
 
 //set milliseconds
 public void setTime() {
	 time=System.currentTimeMillis();
 }

 
 
}// End of Player Class
