import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;

import javax.swing.JOptionPane;

class ReversiGameBoardView extends Canvas implements Runnable
{
    ReversiGameBoard gBoard;
    ReversiGameController rGame;
    
    String statusText="";
   
	 //Color of the squares and the board
    Color[] blinkColors={Color.white,new Color(128,128,128),Color.black};
    Color backGreenColor[]={new Color(0,175,0),new Color(0,150,0)};
    Color black = new Color(0,0,0);
    Color white = new Color(255,255,255);
    
    int aiLevel = 4;
    int frameNumber;
    int blinkCount=blinkColors.length;
    Graphics graphics;
    Frame mainFrame;
    
    int sRow,sCol;
    boolean wait=false;
    int gridSize=48;
    
    int[] oldBoard; 
    boolean computerPlayer = true;
    Player[] players={null,null};
    boolean[] computer={(!computerPlayer),computerPlayer};
        
    
public ReversiGameBoardView(ReversiGameBoard gb,ReversiGameController rg){
		
		rGame = rg;
		gBoard = gb;
		reSizing(ReversiGameBoard.row,ReversiGameBoard.col);
		players[0]=new Player(gBoard,0,this);
		players[1]=new Player(gBoard,1,this);
		statusText=gBoard.statusString();
}
    
    
     
// Mouse Clicked
public boolean mouseDown(Event event,int x,int y){
    	 
     if(x >= sRow||y >= sCol || wait)
     	return true;
      
     mouseClicked(ReversiGameBoard.coordinates(x/gridSize,y/gridSize));
     return true;
      
}
    
     
// when mouse clicked
void mouseClicked(int move) { 
    	 
     	if(!computer[gBoard.getPlayer()]) {
     		
     		if(gBoard.possibleMovesCount==0)
     			makeMove(-1);
     		else if(gBoard.isValidMove(move))
     			makeMove(move);
     		
     	}
     	if(computer[gBoard.getPlayer()])
     		computerMove();
}
     
     
     
//Make move
public void makeMove(int move){
     	blinkingOff();
     	gBoard.makeMove(move);
     	statusText = gBoard.statusString();
     	blinkingOn();
      
}     
     
     
// undo the last move
public void undoMove(){
    	blinkingOff();
    	gBoard.undoMove();
    	statusText = gBoard.statusString();
    	blinkingOn();
}
    

//set computer player
public void setPlayers(boolean noComputerPlayer,boolean computerPlayer){
    	computer[0] = noComputerPlayer;
    	computer[1] = computerPlayer;   
}
     
    
    
// Computer move
void computerMove() {
    	wait=true;
    	message("Computer Thinking...");  
    	players[gBoard.getPlayer()].startSeparateThread("Make your move !!");         
}

   
//resize this object (the buttons)
void reSizing(int x,int y){
		sRow = x * gridSize;
		sCol = y * gridSize;
		resize(sRow,sCol+25);
}
	

//paint one square
void paintSquare(int i,int j){
    	paintGreen(i,j);   
    	paintStones(i,j);
}
	

// redraws the board
//called by internal thread
boolean painting=false;
public void paint(Graphics newGraphics){

		synchronized(this){
			if(painting) 
				return;
			painting=true;
		}
	
		graphics = newGraphics;
		for(int j = 0; j < ReversiGameBoard.col; j++)
			for(int i=0; i < ReversiGameBoard.row; i++)
					paintSquare(i,j);
		changeStatus();
		painting=false;
}
	


// blinking
public int highlighting(int i,int j) {
    	if(oldBoard==null)
    		return 0;
    	
    	graphics=getGraphics();
    	int x = ReversiGameBoard.coordinates(i,j);
    	
    	if(oldBoard[x]==ReversiGameBoard.P1 && gBoard.getValueOfSquare(x)==ReversiGameBoard.P2)
    		return 1;
    	if(oldBoard[x]==ReversiGameBoard.P2 && gBoard.getValueOfSquare(x)==ReversiGameBoard.P1)
    		return 2;
     return 0;   
}

   
    
	
// paint the status
void changeStatus(){
    	graphics.setColor(Color.CYAN);
    	graphics.drawString(statusText,10,sCol+20);
}
    
    
// to set the message telling whose turn
void message(String text) {
   	 	rGame.message(text);
}
    
    
public void answer(String msg,int move){
   	 oldBoard=(int[])gBoard.stonesArray.clone();
   	 makeMove(move);
   	 wait=false;
   	 
   	 if(!gBoard.isGameOver())
   		 message(msg);
   	 else if(gBoard.isGameOver())
   		 message("Game Over");
}

    
    

// highlight the color of the square that has been recently changed
public void fade(){

     if(frameNumber == -1)
    	 return;
     
     if(frameNumber >= blinkCount){
    	 repaint();
    	 frameNumber=-1;
    	 return;
     }
     for(int j=0;j<ReversiGameBoard.col;j++)
    	 for(int i=0;i<ReversiGameBoard.row;i++)
    		 switch(highlighting(i,j)){
    	  		case 0:break;  
    	  		case 1:
    	  			paintGreen(i,j);
    	  			paintSquareWithcolor(i,j,blinkColors[frameNumber]);
    	  			break;
    	  		case 2:         
    	  			paintGreen(i,j);
    	  			paintSquareWithcolor(i,j,blinkColors[blinkCount-1-frameNumber]);
    	  			break;
    	  }       
     frameNumber++;

}

//show rules
public void showRules() {
	JOptionPane.showMessageDialog(null,"Each reversi stone has a black side and a white side. \n"
			+ "On your turn, you place one stone on the square of the board with your \n"
			+ "color. You must place the stone so that an opponent's stone, pr a row of \n"
			+ "opponent's stones, is flanked by your stones. All of the opponent's stones \n"
			+ "between your stones are then turned over to become your color. A move consists \n"
			+ " of placing one stone on an empty square. If you cannot make a legal move(i.e. \n"
			+ " you cannot capture any of your opponent's stones), you have to pass, and your \n"
			+ "opponent is on. If none of the players can place a stone, it means \n"
			+ "the game is over, and the player owning more stones wins. \n"
			+"\n"
			+"Capture : \n"
			+"You can capture vertical, horizontal, and diagonal rows of stones. \n"
			+ "Also, you can capture more than one row at once."+ "");	
	
}



public void setAILevel() {
	 aiLevel = Integer.parseInt(JOptionPane.showInputDialog(null, "Set Diffuculty level , Ranging from 1 to 6 !!", aiLevel));
	 Player p = new Player();
	 p.setForwardSeeingFactor(aiLevel);
	 
}



// Paint the background carpet with green 
void paintGreen(int i,int j){
		graphics.setColor(backGreenColor[(i+j)%2]);
    	graphics.fillRect((i * gridSize),(j * gridSize), gridSize, gridSize);        
}
   
// paint the square with color of stone and show the no of flips present
void paintStones(int i,int j){
    	
    	int value;   
    	boolean canmove;
    	int flips;
    	
    	synchronized(this){
    		value=gBoard.getValueOfSquare(i,j);
    		canmove=gBoard.isValidMove(ReversiGameBoard.coordinates(i,j));
    		flips=gBoard.getflips(ReversiGameBoard.coordinates(i,j));
    	}
    	if(value==ReversiGameBoard.P1)
    		paintSquareWithcolor(i,j,black);
    	else if(value==ReversiGameBoard.P2)
    		paintSquareWithcolor(i,j,white );
    	else if(canmove){
    		int ii = i * gridSize;
        	int jj = j * gridSize;
        	graphics.setColor(Color.black);
        	graphics.drawString(""+flips,ii+gridSize/2,jj+gridSize/2);      
    	}
}


// Paint Square with color
void paintSquareWithcolor(int i,int j,Color c){
    	graphics.setColor(c);
    	graphics.fillRect((i * gridSize)+2, (j * gridSize)+2, gridSize-4, gridSize-4);        
}

   
// starts blinking
void blinkingOn(){
    	repaint();
   	 	frameNumber=0;
}
     
    
//stops the blinking
void blinkingOff(){
	 oldBoard=(int[])gBoard.stonesArray.clone();
	 repaint();       
}

 

// start the thread for the animations
boolean stayTuned;
public void start() {
	 stayTuned=true;
     new Thread(this).start();
}
 
 
 
//this calls fade every 500 milliseconds
public void run(){
	 while(stayTuned){
		 try {
			 Thread.sleep(500);
		 	} catch(Exception e){
		 		
		 	}
		 fade();
	 }
}

// stop the thread
public void stop() {
	 stayTuned=false;
}




} // End of Class