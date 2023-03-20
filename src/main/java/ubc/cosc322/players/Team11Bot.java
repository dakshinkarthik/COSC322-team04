
package ubc.cosc322.players;

import java.util.ArrayList;
import java.util.Collections;
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
public class Team11Bot extends GamePlayer{

	private final Logger logger;

    private GameClient gameClient = null; 
    private BaseGameGUI gameGui = null;
	private final GameStateManager gameStateManager;

    private String userName = null;
    private String passwd = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	Team11Bot bot = new Team11Bot(args[0], args[1]);
    	
    	if(bot.getGameGUI() == null) {
    		bot.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(bot::Go);
    	}
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public Team11Bot(String userName, String passwd) {

		logger = Logger.getLogger(GameStateManager.class.toString());

		this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gameGui = new BaseGameGUI(this);

		//Initialize GameStateManager
		this.gameStateManager = new GameStateManager();
    }
 


    @Override
    public void onLogin() {
		logger.info("Login Successful.");

		userName = getGameClient().getUserName();
		if(gameGui != null){
			gameGui.setRoomInformation(gameClient.getRoomList());
		}
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		switch (messageType) {
			case GameMessage.GAME_STATE_BOARD -> gameGui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
			case GameMessage.GAME_ACTION_MOVE ->  {
				//Update our GUI to reflect opponents move
				gameGui.updateGameState(msgDetails);

				//Update GameStateManager board state
				gameStateManager.opponentMove(msgDetails);

				//Make our move
				move();

			}
			case GameMessage.GAME_ACTION_START -> {

				//Make a move if the bot starts as white
				String black = (String) msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
				if(black.equalsIgnoreCase(userName)){

					logger.info("Playing as black.");
					gameStateManager.setPlayer(GameStateManager.Tile.BLACK);

					//Make our move
					move();


				} else {
					logger.info("Playing as white.");
					gameStateManager.setPlayer(GameStateManager.Tile.WHITE);
				}
			}
			default -> {
				String msg = "Unhandled Message Type occurred: " + messageType;
				logger.warning(msg);
			}
		}

    	return true;
    }
    
    private void move(){
		Map<String, Object> moveDetails = gameStateManager.makeMove();

		if(!moveDetails.isEmpty()){
			gameGui.updateGameState(moveDetails);
			gameClient.sendMoveMessage(moveDetails);

			logger.info("Made our move. Waiting on opponent.");

		}else {
			logger.severe("No move found. We've lost.");
		}
	}


    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return gameGui;
	}

	@Override
	public void connect() {
    	gameClient = new GameClient(userName, passwd, this);			
	}

 
}//end of class
