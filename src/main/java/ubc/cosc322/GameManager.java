package main.java.ubc.cosc322;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ubc.cosc322.movement.Moves;
import ubc.cosc322.movement.SearchTree;
import ubc.cosc322.movement.Graph;
import java.util.*;
import java.util.logging.Logger;


public class GameStateManager{

	//IMPORTANT
	//Moves are presented as 2 element Integer ArrayLists
	//The first int representing the Y value from bottom to top
	//The second int representing the X value from right to left
	//[10, 7] corresponds to [10, G] on the game board GUI.
	//[5, 3] corresponds to [5, C] etc...

	private static final int THRESHOLD = 250000;	//ME: try different values
	private static final int DIM = 10;
	private static final int[][] BOARD_STATE_BEGINNING = {

			{0, 0, 0, 2, 0, 0, 2, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{2, 0, 0, 0, 0, 0, 0, 0, 0, 2},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
	};

	private Square ourPlayer;
	private Square opponentPlayer;
	private final Logger logger;
	private Graph currentBoardState;
	private Map<Moves.Move, Graph> legalMovesMap;

	public static int[][] getInitialBoardState(){
		return BOARD_STATE_BEGINNING;
	}

	
	public GameStateManager(){
		logger = Logger.getLogger(GameStateManager.class.toString());
		currentBoardState = new Graph(BOARD_STATE_BEGINNING);
		legalMovesMap = new HashMap<>();
	}


	public void setPlayers(Square player){
		if(player.isPlayer()){
			ourPlayer = player;
			if(player.isWhite())
				opponentPlayer = Square.BLACK;
			else
				opponentPlayer = Square.WHITE;
		}
	}

	public static List<Integer> indexToCoordinates(int index){
		ArrayList<Integer> list = new ArrayList<>(2);
		//Y Position
		list.add((int) Math.ceil(DIM - ((float) index/DIM)));
		//X Position
		list.add((index % DIM) + 1);
		return list;
	}

	// Takes the message Details that are stored in the array and retrieves the position of:
	// Current X and Y of Queen

	//ME: Research how to get the index of the queen from the message details
	public static int getQueenInitialIndexFromCoord (Map<String, Object> msgDetails) {
		ArrayList<Integer> initialPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
		int row = DIM-initialPosition.get(0);
		int col = initialPosition.get(1)-1;
		int index = row*DIM + col;
		return index;
	}

	public static int getQueenNewIndexFromCoord (Map<String, Object> msgDetails) {
		ArrayList<Integer> newPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
		int row = DIM-newPosition.get(0);
		int col = newPosition.get(1)-1;
		int index = row*DIM + col;
		return index;
	}

	public static int getArrowIndexFromCoord (Map<String, Object> msgDetails) {
		ArrayList<Integer> arrowPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
		int row = DIM-arrowPosition.get(0);
		int col = arrowPosition.get(1)-1;
		int index = row*DIM + col;
		return index;
	}



	/**
	 * Takes information from the opponents move and updates the board state graph accordingly
	 * @param opponentMove Opponent move information
	 */
	public void opponentPlayerMove(Map<String, Object> opponentMove){

		//Translate the x y coordinates to the graph node index
		int initialIndex = getQueenInitialIndexFromCoord(opponentMove);
		int newIndex = getQueenNewIndexFromCoord(opponentMove);
		int arrowIndex = getArrowIndexFromCoord(opponentMove);

		Moves.Move turn = new Moves.Move(initialIndex, newIndex, arrowIndex);

		//Check validity
		legalMovesMap = Moves.allMoves(currentBoardState, opponentPlayer);

		if(!legalMovesMap.containsKey(turn)){
			String opponentColor = ""; 
			if(opponentPlayer.isBlack()){
				opponentColor = "black";
			}else{
				opponentColor = "white";
			}
			String msg = "Illegal move made by "+opponentColor ;
			logger.severe(msg);
		}

		logger.info("Opponent has made their move. Our turn to find the best move...");

		//Update the graph
		if(ourPlayer.isWhite())
			currentBoardState.updateGraph(turn, Square.BLACK);
		else
			currentBoardState.updateGraph(turn, Square.WHITE);

	}

	/**
	 * Calculates the best possible move from the current state of the board.
	 * @return An Map containing movement information
	 */
	public Map<String, Object> findOurBestMove() {

		legalMovesMap = Moves.allMoves(currentBoardState, ourPlayer);

		int depth = 1;
		for(int i = 5; i > 0; i--){
			if(Math.pow(legalMovesMap.size(), i) < THRESHOLD){
				depth = i;
				break;
			}
		}

		Moves.Move bestMove = SearchTree.performAlphaBeta(Graph.cloneGraph(currentBoardState), ourPlayer, depth);

		//Double check there aren't any possible moves whatsoever
		if(bestMove == null) {
			bestMove = SearchTree.performAlphaBeta(Graph.cloneGraph(currentBoardState), ourPlayer, 1);
		}

		if(bestMove == null){
			return Collections.emptyMap();
		}

		//Put the move information into a message details object to send back to the game server
		return updateBoardState(bestMove);
	}

	public Map<String, Object> updateBoardState(Moves.Move move){
		Map<String, Object> playerMove = new HashMap<>();
		playerMove.put(AmazonsGameMessage.QUEEN_POS_CURR, indexToCoordinates(move.currentIndex()));
		playerMove.put(AmazonsGameMessage.QUEEN_POS_NEXT, indexToCoordinates(move.nextIndex()));
		playerMove.put(AmazonsGameMessage.ARROW_POS, indexToCoordinates(move.arrowIndex()));

		//Don't forget to update the current state of the game!
		currentBoardState = legalMovesMap.get(move);
		
		legalMovesMap = Moves.allMoves(currentBoardState, opponentPlayer);
		if(legalMovesMap.isEmpty()){
			logger.info("We won! Opponent has no more legal moves.");
		}

		return playerMove;
	}

	public static class Square {

		public static final Square EMPTY = new Square(0);
		public static final Square WHITE = new Square(1);
		public static final Square BLACK = new Square(2);
		public static final Square ARROW = new Square(3);
	
		private final int id;
	
		private Square(int id) {
			this.id = id;
		}
	
		public int getId() {
			return id;
		}

		public static Square valueOf(int id) {
			switch (id) {
				case 0:
					return EMPTY;
				case 1:
					return WHITE;
				case 2:
					return BLACK;
				case 3:
					return ARROW;
				default:
					throw new IllegalArgumentException("Invalid id: " + id);
			}
		}
	
		public boolean isEmpty() {
			if(this == EMPTY){
				return true;
			} else {
				return false;
			}
		}
	
		public boolean isPlayer() {
			if((isBlack() || isWhite())){
				return true;
			} else {
				return false;
			}
		}
	
		public boolean isWhite() {
			if(this == WHITE){
				return true;
			} else {
				return false;
			}
		}
	
		public boolean isBlack() {
			if(this == BLACK){
				return true;
			} else {
				return false;
			}
		}
	
		public boolean isFire() {
			if(this == ARROW){
				return true;
			} else {
				return false;
			}
		}
	}

}
