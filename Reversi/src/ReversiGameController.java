import java.applet.Applet;
import java.awt.*;


public class ReversiGameController extends Applet{
  
// instantiations -----------------------------------------------------------------------
	// Shows turn of the players
	Label statusLabel = new Label("Welcome,Let's Play!");
  
	// initialized the game board
	ReversiGameBoard gameBoard = new ReversiGameBoard();
  
	// View the game board
	ReversiGameBoardView view=new ReversiGameBoardView(gameBoard,this);
  
	// Initializing  Buttons
	Button newBtn=new Button("New Game");
	Button ruleBtn=new Button("Rules");
	Button undoBtn=new Button("Undo Move");
	Button levelBtn=new Button("Set AI Level");
	Button exitBtn = new Button("Exit Game !");
 
//instantiations -----------------------------------------------------------------------
  
// initialization of the board with buttons
 public void init(){
	 
	 setBackground(Color.GRAY);
  
	 Panel actionPanel=new Panel();
	 actionPanel.setLayout(new GridLayout(1,5));
	 actionPanel.add(newBtn);
	 actionPanel.add(ruleBtn);
	 actionPanel.add(undoBtn);
	 actionPanel.add(levelBtn);
	 actionPanel.add(exitBtn);
  
	 Panel actionButtonPanel=new Panel();
	 actionButtonPanel.setLayout(new FlowLayout());
	 actionButtonPanel.add(actionPanel);
  
	 Panel statusPanel=new Panel();
	 statusPanel.setLayout(new FlowLayout());
	 statusPanel.add(statusLabel);
  
	 setLayout(new BorderLayout());
	 add("North",actionButtonPanel);
	 add("Center",statusPanel);
	 add("South",view);
 
	 setVisible(true);
 }
 
 
 //start a thread
 public void start() {
	 view.start();
 }
 
 
 //stop
 public void stop() {
	 view.stop();
 }
 
 
// Delegates the new game window , assign methods to all the buttons 
 public boolean action(Event ev, Object O){
	 
	if(view.wait)
		return true;
  
  	if(ev.target==newBtn)
  	  this.newGame();
  	if(ev.target==ruleBtn)
	  rule();
  	if(ev.target==undoBtn)
  	  undo();
  	if(ev.target==levelBtn)
    	  setLevel();
  	if(ev.target==exitBtn)
	  System.exit(0);
  
  return true;
  
 }
 
 
 // Set the level for the computer player coming from user
 private void setLevel() {
	 if(gameBoard.movesDone==0)
		 view.setAILevel();
	 if(gameBoard.movesDone!=0)
		 levelBtn.disable();	
}


// when new game clicked
 public void newGame(){
	 
	 // clears the board
	 gameBoard.clear();
	 //repaint the board
	 view.repaint();
	 levelBtn.enable();
  
 }

 //delegate to show rules to accomplish rules button click action
 public void rule(){
		 view.showRules();
 }
 
 //delegates the undo button action
 public void undo(){
	 if(gameBoard.movesDone!=0)
		 view.undoMove();
 }
 

 //displays the text message
 public void message(String text){
	 statusLabel.setText(text);
 }
 
} // End of ReversiGame Class 