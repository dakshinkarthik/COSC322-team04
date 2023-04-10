package ubc.cosc322.Algorithm;

import ubc.cosc322.Graph.*;
import ubc.cosc322.AmazonsGameManager.Square;

import java.util.*;

public class AmazonsDistanceHeuristic {

    public class Heuristic {


        public static float calculateHeuristicValue(Graph board, Square player){
        

            float territoryScore = territoryHeuristic(board);
            float mobilityScore = mobilityHeuristic(board);
           // float transitionScore1 = transistionHeuristic1(board);
            float gameStartScore = gameStartHeuristic(board);
            float clusteringScore = clusteringHeuristic(board);
            float attackingPotentialScore = attackingPotentialHeuristic(board);
            float attackingPotential1Score = attackingPotential1Heuristic(board);

            // Add a weight to control the importance of the clustering heuristic
            float clusteringWeight = 1f;
            clusteringScore = clusteringWeight * clusteringScore;
            
            // Add a weight to control the importance of the attacking potential heuristic
            float attackingPotentialWeight = 1f;
            attackingPotentialScore = attackingPotentialWeight * attackingPotentialScore;

            // Add a weight to control the importance of the attacking potential heuristic
            float attackingPotential1Weight = 0.5f;
            attackingPotential1Score = attackingPotential1Weight * attackingPotentialScore;

            double weight = Math.sqrt(Math.pow(territoryScore,2) + Math.pow(mobilityScore,2)+gameStartScore*gameStartScore + Math.pow(attackingPotential1Score,2) + Math.pow(clusteringScore,2));

            float t1 = 0;
            float t2 = 0;

            for(GraphNode n : board.getAllGraphNodes()){
                
                    if(!n.getNodeValue().isEmpty()) {
                        continue;
                    }
                    t1 += Ti_value(player, n.getQueenDistanceWhite(), n.getQueenDistanceBlack());
                    t2 += Ti_value(player, n.getKingDistanceWhite(), n.getKingDistanceBlack());
                    
            }

            float c1 = C1_value(board);
            float c2 = C2_value(board);

            double heuristic1 = (territoryScore/weight) * t1;
            double heuristic2 = (mobilityScore/weight) * c1;
           // double heuristic3 = (transitionScore1/weight) * c2;
            double heuristic4 = (gameStartScore/weight) * t2;
            double heuristic5 = (clusteringScore/weight) * t1;
            double heuristic6 = (attackingPotential1Score/weight) * t2;
      
            return (float) (heuristic1 + heuristic2 + heuristic4 + heuristic5 + heuristic6);
        }

        private static float Ti_value(Square player, int dist1, int dist2){
            float k = 1/5f;
    
            if(dist1 == Integer.MAX_VALUE && dist2 == Integer.MAX_VALUE) 
                return 0;
            else if(dist1 == dist2) {
                if (player ==  Square.BLACK)
                    return -k;
                else
                    return k;
            }
            else 
                return dist1 < dist2 ? 1 : -1;
        }
    private static float C1_value(Graph board){
            float sum = 0;
            for (GraphNode n : board.getAllGraphNodes()) {
                float term1 = (float) Math.pow(2, -n.getQueenDistanceWhite());
                float term2 = (float) Math.pow(2, -n.getQueenDistanceBlack());
                sum += term1 - term2;
            }
            return 2 * sum;
        }

        private static float C2_value(Graph board){
            float sum = 0;
            for (GraphNode n : board.getAllGraphNodes()) {
                sum += Math.min(1, Math.max(-1, ((n.getKingDistanceBlack() - n.getKingDistanceWhite()) / 6f)));
            }
    
            return sum;
        }


        /***
         * 
         * @param board The current board state
         * @return Returns a heursitic based on the number of empty nodes
         */
        private static float territoryHeuristic(Graph board){

        float score = 0;                             //calculates the number of filled tiles
        for(GraphNode n : board.getAllGraphNodes()) {
            if(!n.getNodeValue().isEmpty()) {
                score++;
            }
        }
        score = 100 - score;
        return score;
    }


        
    private static float mobilityHeuristic(Graph board) {

        float score = 0;
        for (GraphNode n : board.getAllGraphNodes()) {
            if (n.getNodeValue().isEmpty()) {
                continue;
            }

            int qDist = (n.getNodeValue() == Square.WHITE) ? n.getQueenDistanceWhite() : n.getQueenDistanceBlack();
            int kDist = (n.getNodeValue() == Square.WHITE) ? n.getKingDistanceWhite() : n.getKingDistanceBlack();

            score += Math.pow(qDist, 2) - Math.pow(kDist, 2);
        }
        return score;
    }
        

        // /***
        //  * Returns a heuristic that supports transition from the earlier to the later stages of the game
        //  * @param board The current board state
        //  * @return Returns a heursitic based on the number of empty squares with penalty on empty squares
        //  */
        // private static float transistionHeuristic1(Graph board){

        //     float score = 0;
            
        //     for(GraphNode n : board.getAllGraphNodes()){
        //         if(!n.getNodeValue().isEmpty()) {
        //             score++;
                
        //         }
        //     }
            
        //     score = ((score-60)/10);
        //     score *= score; 
        //     score *= (-1);
        //     score += 40; 
        //     return score;
    
            
        // }


        /***
         * Calculates a heuristic that is crucial for the beginning of the game.
         * @param board The current board state
         * @return Returns a heursitic based on the number of empty squares with penalty on empty squares
         */
        private static float gameStartHeuristic (Graph board){
            
            float score = 0; 
            for(GraphNode n : board.getAllGraphNodes()){
            if(!n.getNodeValue().isEmpty()) {
                score++;
            }
            }
                return score;

        }

        /***
         * Calculates a heuristic that is penalizes clustering of pieces
         * @param board The current board state
         * @return Returns a heursitic based on the difference between queen distance values and king distance values.
         */
        private static float clusteringHeuristic(Graph board) {
            float score = 0;
        
            for (GraphNode node : board.getAllGraphNodes()) {
                if (!node.getNodeValue().isEmpty()) {
                    continue;
                }
        
                int qDistWhite = node.getQueenDistanceWhite();
                int qDistBlack = node.getQueenDistanceBlack();
                int kDistWhite = node.getKingDistanceWhite();
                int kDistBlack = node.getKingDistanceBlack();
        
                // Encourage larger kDist for both players to reduce clustering
                score += (kDistWhite - kDistBlack) * (qDistWhite - qDistBlack);
            }
        
            return score;
        }

        /***
         * Calculates a heuristic that is favors attacking potential of pieces
         * @param board The current board state
         * @return Returns a heursitic based on the difference between queen and king distance values.
         */
        private static float attackingPotentialHeuristic(Graph board) {
            float score = 0;

            for (GraphNode node : board.getAllGraphNodes()) {
                if (!node.getNodeValue().isEmpty()) {
                    continue;
                }

                int qDistWhite = node.getQueenDistanceWhite();
                int qDistBlack = node.getQueenDistanceBlack();
                int kDistWhite = node.getKingDistanceWhite();
                int kDistBlack = node.getKingDistanceBlack();

                // Encourage smaller qDist values for both players to increase attacking potential
                score += (qDistBlack - qDistWhite) * (kDistWhite - kDistBlack);
            }

            return score;
        }

        /***
         * Calculates a heuristic that favors attacking potential with respect to mobility of pieces
         * @param board The current board state
         * @return Returns a heursitic based on the queen and king distance values.
         */
        private static float attackingPotential1Heuristic(Graph board) {
            float score = 0;

            for (GraphNode node : board.getAllGraphNodes()) {
                if (!node.getNodeValue().isEmpty()) {
                    continue;
                }

                int qDistWhite = node.getQueenDistanceWhite();
                int qDistBlack = node.getQueenDistanceBlack();
                // Encourage smaller qDist values for both players to increase mobility
                score += (1.0 / qDistWhite) - (1.0 / qDistBlack);
            }

            return score;
        }


    }

    public static class GraphDistanceCalculator {

        public static void calculatePlayerDistances(Graph graph, Square playerNode){
    
            Set<GraphNode> unvisitedNodes = new HashSet<>();
            List<DistanceNode> nodeSearch = new LinkedList<>();
            List<GraphNode> allNodes = graph.getAllGraphNodes();
            int queenDistance = 1;
            
            for (int i = 0; i < allNodes.size(); i++) {
                GraphNode node = allNodes.get(i);
                Square tile = node.getNodeValue();
                
                if (tile.isArrow()) {
                    continue;
                }
               
                if (!tile.isPlayer()) 
                {
                    unvisitedNodes.add(node);
                }
             
                if (node.getNodeValue() == playerNode) {
                    node.setPlayerDistancesZero(node.getNodeValue());
                    GraphDistanceCalculator.DistanceNode distanceNode = new DistanceNode(node, node.getNodeValue());
                    nodeSearch.add(distanceNode);
                }
    
            }
    
            while (!unvisitedNodes.isEmpty()) {
                nodeSearch = exploreAndSetDistances(nodeSearch, unvisitedNodes, queenDistance);
                queenDistance++;
    
                if (nodeSearch.isEmpty()) {
                    break;
                }
            }
        }
    
    
        
        private static List<DistanceNode> exploreAndSetDistances(List<DistanceNode> initiaNodes,  Set<GraphNode> unvisitedNodes, int queenDistance){
    
            List<DistanceNode> newVistedNodes = new LinkedList<>();
    
            for(int i = 0; i < initiaNodes.size(); i++){
    
                GraphNode initNode = initiaNodes.get(i).node;
                unvisitedNodes.remove(initNode);
    
                for (int j = 0; j < GraphEdge.Direction.getAllDirections().length; j++) {
    
                    GraphEdge currEdge = initNode.getExistingEdge(GraphEdge.Direction.getAllDirections()[j]);
    
                    int kingDistance = 1;
                   
                    while(currEdge!=null){

                        GraphNode currNode = currEdge.getTargetNode();
                        
                        if(initiaNodes.get(i).currentPlayer().isWhite()){
                            kingDistance = initNode.getKingDistanceWhite();
                            if(currNode.getQueenDistanceWhite() > queenDistance) {
                                currNode.setQueenDistanceWhite(queenDistance);
                            }
                            if(currNode.getKingDistanceWhite() > kingDistance) {
                                ++kingDistance;
                                currNode.setKingDistanceWhite(kingDistance);
                            }
                        }
                        
                        else if(initiaNodes.get(i).currentPlayer().isBlack())
                        {
                            kingDistance = initNode.getKingDistanceBlack();
                            if(currNode.getQueenDistanceBlack() > queenDistance){
                                 currNode.setQueenDistanceBlack(queenDistance);
                            }
                            if(currNode.getKingDistanceBlack() > kingDistance) {
                                ++kingDistance;
                                currNode.setKingDistanceBlack(kingDistance);
                            }
                        }
    
                        if (unvisitedNodes.remove(currNode)) {
                            DistanceNode distanceNode = new DistanceNode(currNode, initiaNodes.get(i).currentPlayer);
                            newVistedNodes.add(distanceNode);
                        }
    
                        currEdge = currNode.getExistingEdge(GraphEdge.Direction.getAllDirections()[j]);
                    }
    
    
                }
            }
    
            return newVistedNodes;
        }

        private record DistanceNode(GraphNode node, Square currentPlayer){

            @Override
            public boolean equals(Object o) { return o instanceof DistanceNode d && d.node.equals(node); }
    
        }
    
    
    
    }
    
   

}
