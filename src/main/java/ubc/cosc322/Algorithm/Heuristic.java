package ubc.cosc322.Algorithm;
import ubc.cosc322.Graph.*;
import ubc.cosc322.GameStateManager;
import ubc.cosc322.GameStateManager.Square;

public class Heuristic {

    private Heuristic(){

    }

    public static float calculateHeuristicValue(Graph board, Square player) {
        float numFilledTiles = 0; 
        for (GraphNode node : board.getAllGraphNodes()) {
            if (!node.getNodeValue().isEmpty()) {
                numFilledTiles++;
            }
        }
        numFilledTiles = 100 - numFilledTiles;

        float numNonEmptyTiles = 0;
        for (GraphNode node : board.getAllGraphNodes()) {
            if (!node.getNodeValue().isEmpty()) {
                numNonEmptyTiles++;
            }
        }
        numNonEmptyTiles = (numNonEmptyTiles - 30) / 10;
        numNonEmptyTiles = -(numNonEmptyTiles * numNonEmptyTiles) + 40;

        float numAttackPiecesOnBoard = 0;
        for (GraphNode node : board.getAllGraphNodes()) {
            if (!node.getNodeValue().isEmpty()) {
                numAttackPiecesOnBoard++;
            }
        }
        numAttackPiecesOnBoard = (float) ((numAttackPiecesOnBoard - 40));
        numAttackPiecesOnBoard = -(numAttackPiecesOnBoard * numAttackPiecesOnBoard) + 20;

        float numEmptyTiles = 0;
        numEmptyTiles = 100 - numFilledTiles;

        float numArrowPiecesOnBoard = 0;
        for (GraphNode node : board.getAllGraphNodes()) {
            if (node.getNodeValue().isArrow()) {
                numArrowPiecesOnBoard++;;
            }
        }
        numArrowPiecesOnBoard = (numArrowPiecesOnBoard - 20) / 10;
        numArrowPiecesOnBoard = (numArrowPiecesOnBoard * numArrowPiecesOnBoard) + 40;

        double weight = Math.sqrt(numFilledTiles * numFilledTiles + numNonEmptyTiles * numNonEmptyTiles +
                numAttackPiecesOnBoard * numAttackPiecesOnBoard + numEmptyTiles * numEmptyTiles + numArrowPiecesOnBoard * numArrowPiecesOnBoard);

        float tiValue1 = 0;
        float tiValue2 = 0;

        for (GraphNode node : board.getAllGraphNodes()) {
            if (node.getNodeValue().isEmpty()) {
                continue;
            }
            
            tiValue1 += calculate_TiValue(player, node.getQueenDistanceWhite(), node.getQueenDistanceBlack());
            tiValue2 += calculate_TiValue(player, node.getKingDistanceWhite(), node.getKingDistanceBlack());
            
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
        for (GraphNode n : board.getAllGraphNodes()) {
            sum += Math.min(1, Math.max(-1, ((n.getKingDistanceBlack() - n.getKingDistanceWhite()) / 6f)));
        }

        return sum;
    }
    private static float calcC2_value(Graph board){
    	float sum = 0;
        for (GraphNode n : board.getAllGraphNodes()) {
            float term1 = (float) Math.pow(2, -n.getQueenDistanceWhite());
            float term2 = (float) Math.pow(2, -n.getQueenDistanceBlack());
            sum += term1 - term2;
        }
        return 2 * sum;
    }

    
    
    }