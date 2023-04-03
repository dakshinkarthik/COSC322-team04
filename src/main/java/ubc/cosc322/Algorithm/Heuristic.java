package ubc.cosc322.Algorithm;
import ubc.cosc322.Graph.Graph;
import ubc.cosc322.GameStateManager;
import ubc.cosc322.GameStateManager.Square;

public class Heuristic {

    private Heuristic(){

    }
    // This is the heuristic function that we will use to evaluate the board state
    public static float calculateHeuristicValue(Graph board, Square player) {
        
        float numFilledTiles = 0; 
        // Count the number of tiles that are not empty
        for (Graph.Node node : board.getNodes()) {
            if (!node.getValue().isEmpty()) {
                numFilledTiles++;
            }
        }
        numFilledTiles = 100 - numFilledTiles;

        float numNonEmptyTiles = 0;
        // Count the number of tiles that are not empty
        for (Graph.Node node : board.getNodes()) {
            if (!node.getValue().isEmpty()) {
                numNonEmptyTiles++;
            }
        }
        numNonEmptyTiles = (numNonEmptyTiles - 30) / 10;
        numNonEmptyTiles = -(numNonEmptyTiles * numNonEmptyTiles) + 40;

        float numAttackPiecesOnBoard = 0;
        // Count the number of attack pieces on the board
        for (Graph.Node node : board.getNodes()) {
            if (!node.getValue().isEmpty()) {
                numAttackPiecesOnBoard++;
            }
        }
        numAttackPiecesOnBoard = (float) ((numAttackPiecesOnBoard - 40));
        numAttackPiecesOnBoard = -(numAttackPiecesOnBoard * numAttackPiecesOnBoard) + 20;

        float numEmptyTiles = 0;
        numEmptyTiles = 100 - numFilledTiles;

        float numArrowPiecesOnBoard = 0;
        // Count the number of arrow pieces on the board
        for (Graph.Node node : board.getNodes()) {
            if (node.getValue().isFire()) {
                numArrowPiecesOnBoard++;;
            }
        }
        numArrowPiecesOnBoard = (numArrowPiecesOnBoard - 20) / 10;
        numArrowPiecesOnBoard = (numArrowPiecesOnBoard * numArrowPiecesOnBoard) + 40;

        double weight = Math.sqrt(numFilledTiles * numFilledTiles + numNonEmptyTiles * numNonEmptyTiles +
                numAttackPiecesOnBoard * numAttackPiecesOnBoard + numEmptyTiles * numEmptyTiles + numArrowPiecesOnBoard * numArrowPiecesOnBoard);

        float tiValue1 = 0;
        float tiValue2 = 0;
// 
        for (Graph.Node node : board.getNodes()) {
            if (node.getValue().isEmpty()) {
                continue;
            }
            
            tiValue1 += calculate_TiValue(player, node.getQdist1(), node.getQdist2());
            tiValue2 += calculate_TiValue(player, node.getKdist1(), node.getKdist2());
            
        }

        double a1 = (numFilledTiles / weight) * tiValue1;
        double a2 = (numNonEmptyTiles / weight) * calcC2_value(board);
        double a3 = (numAttackPiecesOnBoard / weight) * calcC1_value(board);
        double a4 = (numEmptyTiles / weight) * tiValue2;
        double a5 = (numArrowPiecesOnBoard / weight);
        
        return (float) (a1 + a2 + a3 + a4 + a5);
    }
     
    private static float calculate_TiValue(GameStateManager.Square playerSquare, int distance1, int distance2){
    	float k_constant = 1/4f;
        
            switch (Integer.compare(distance1, distance2)) {
                case 0:
                    return playerSquare == GameStateManager.Square.WHITE ? k_constant : -k_constant;
                case -1:
                    return 1;
                default:
                    return -1;
            }
        
    }
    
    private static float calcC1_value(Graph board){
        float sum = 0;
        for (Graph.Node n : board.getNodes()) {
            sum += Math.min(1, Math.max(-1, ((n.getKdist2() - n.getKdist1()) / 6f)));
        }

        return sum;
    }
    private static float calcC2_value(Graph board){
    	float sum = 0;
        for (Graph.Node n : board.getNodes()) {
            float term1 = (float) Math.pow(2, -n.getQdist1());
            float term2 = (float) Math.pow(2, -n.getQdist2());
            sum += term1 - term2;
        }
        return 2 * sum;
    }

    
    
    }
    
    
    



