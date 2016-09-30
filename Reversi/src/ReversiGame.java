import java.awt.Frame;

public class ReversiGame {

	public static void main(String[] args) {
		  
		  //instantiations
		  Frame container=new Frame("Reversi");
		  ReversiGameController game = new ReversiGameController();
		  
		  //set the attributes of the Reversigame container
		  container.setSize(391,510);
		  container.add("Center",game );
		  container.setResizable(false);
		  container.setVisible(true);
		  container.setLocation(500,100);
		  
		  
		  // starts the game
		  game .init();
		  game .start();

	}

}
