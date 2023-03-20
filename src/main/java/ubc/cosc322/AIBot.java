
package ubc.cosc322.players;

import java.util.ArrayList;

import java.util.Map;
import java.util.logging.Logger;

import ubc.cosc322.GameStateManager;
import ygraph.ai.smartfox.games.*;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class AIBot extends GamePlayer{
	
	private final Logger logger;
    private GameClient gameClient = null; 
    private final GameStateManager RosieManager;
    private BaseGameGUI gamegui = null;
    private String username = null;
    private String password = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	AIBot rosie = new AIBot(args[0], args[1]);
    	
    	if(rosie.getGameGUI() == null) {
    		rosie.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(rosie::Go);
    	}
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public AIBot(String username, String password) {
    	this.username = username;
    	this.password = password;
    	logger = Logger.getLogger(GameStateManager.class.toString());
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    	this.RosieManager = new GameStateManager();
    }
 


    @Override
    public void onLogin() {
    	System.out.println("Congratualations!!! "
    			+ "I am called because the server indicated that the login is successfully");
    	System.out.println("The next step is to find a room and join it: "
    			+ "the gameClient instance created in my constructor knows how!"); 

    	username = gameClient.getUserName();
    	if(gamegui != null) {
    	gamegui.setRoomInformation(gameClient.getRoomList());
    	}
    }


    
    private void moveRosie(){
		Map<String, Object> move = RosieManager.makeMove();

		if(!move.isEmpty()){
			gamegui.updateGameState(move);
			gameClient.sendMoveMessage(move);

			logger.info("we made our move. Waiting for the opponent now!");

		}else {
			logger.severe("No more move found. We lost :(");
		}
	}
    
    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	switch (messageType) {
		case GameMessage.GAME_STATE_BOARD -> gamegui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
		case GameMessage.GAME_ACTION_MOVE ->  {
			gamegui.updateGameState(msgDetails);
			RosieManager.opponentMove(msgDetails);
			moveRosie();

		}
		case GameMessage.GAME_ACTION_START -> {

			//Make a move if the bot starts as white
			String black = (String) msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
			if(black.equalsIgnoreCase(username)){

				logger.info("Playing as black.");
				RosieManager.setPlayer(GameStateManager.Tile.BLACK);
				moveRosie();


			} else {
				logger.info("Playing as white.");
				RosieManager.setPlayer(GameStateManager.Tile.WHITE);
			}
		}
		default -> {
			String msg = "Unhandled Message Type occurred: " + messageType;
			logger.warning(msg);
		}
	}

	return true;
    }
    


    @Override
    public String userName() {
    	return username;
    }

	@Override
	public GameClient getGameClient() {
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return gamegui;
	}

	@Override
	public void connect() {
    	gameClient = new GameClient(username, password, this);			
	}

 
}//end of class
