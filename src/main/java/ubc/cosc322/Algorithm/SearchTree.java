
package ubc.cosc322.Algorithm;

import java.util.Map;
import ubc.cosc322.Graph.*;
import ubc.cosc322.GameStateManager.Square;

public class SearchTree {

    private SearchTree() {

    }

    public record MinimaxMove(Moves.Move move, float heuristic) { }
    
    public static Moves.Move performAlphaBeta(Graph graph, Square currentPlayer, int depth) {
        MinimaxMove chosenOne = AlphaBeta(graph, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, currentPlayer, null);
        return chosenOne.move;
    }


    private static MinimaxMove AlphaBeta(Graph graph, int depth, float alpha, float beta, Square currentPlayer, Moves.Move prevMove) {
        if (depth == 0)
            return new MinimaxMove(prevMove, Heuristic.calculateT(graph, currentPlayer));

        float best_Heuristic;
        Moves.Move bestMove = null;
        Map<Moves.Move, Graph> movesMap = Moves.possMoves(graph, currentPlayer);

        if (currentPlayer.isWhite()) {

            best_Heuristic = Integer.MIN_VALUE;

            for (Map.Entry<Moves.Move, Graph> entry : movesMap.entrySet()) {
                Moves.Move currentMove = entry.getKey();
                float chosen_Heurtistic = AlphaBeta(entry.getValue(), depth - 1, alpha, beta, Square.BLACK, currentMove).heuristic;
                if (best_Heuristic < chosen_Heurtistic) {
                    best_Heuristic = chosen_Heurtistic;
                    bestMove = currentMove;
                }
                
//                best_Heuristic = (best_Heuristic < chosen_Heurtistic) ? chosen_Heurtistic : best_Heuristic;
//                bestMove = (best_Heuristic < chosen_Heurtistic) ? currentMove : bestMove;

                alpha = Math.max(alpha, best_Heuristic);
                if (beta <= alpha) {
                    break;
                }
            }

        } else {

            best_Heuristic = Integer.MAX_VALUE;

            for (Map.Entry<Moves.Move, Graph> entry : movesMap.entrySet()) {
                float chosen_Heurtistic = AlphaBeta(entry.getValue(), depth - 1, alpha, beta, Square.WHITE, entry.getKey()).heuristic;
                if (best_Heuristic > chosen_Heurtistic) {
                    best_Heuristic = chosen_Heurtistic;
                    bestMove = entry.getKey();
                }
                
//                best_Heuristic = (best_Heuristic > chosen_Heurtistic) ? chosen_Heurtistic : best_Heuristic;
//                bestMove = (best_Heuristic > chosen_Heurtistic) ? entry.getKey() : bestMove;


                beta = Math.max(alpha, best_Heuristic);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return new MinimaxMove(bestMove, best_Heuristic);
    }
}



