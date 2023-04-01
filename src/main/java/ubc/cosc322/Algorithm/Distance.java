package ubc.cosc322.Algorithm;

import ubc.cosc322.Graph.*;
import ubc.cosc322.GameStateManager;

import java.util.*;

public class Distance {

	public class DistanceNode {
	    public  GraphNode node = null;
	    public  GameStateManager.Square nextPlayer = null;

	    public DistanceNode(GraphNode node, GameStateManager.Square nextPlayer) {
	        this.node = node;
	        this.nextPlayer = nextPlayer;
	    }

	    public GraphNode getNode() {
	        return node;
	    }

	    public GameStateManager.Square getNextPlayer() {
	        return nextPlayer;
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof DistanceNode)) return false;
	        DistanceNode that = (DistanceNode) o;
	        return Objects.equals(node, that.node);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(node);
	    }
	}

    /**
     * Sets the kDist and qDist values on each empty tile
     * on the board for a given player
     * @param g
     * @param player
     */

    public static void allDistances(Graph graph, GameStateManager.Square startingTile){

        List<DistanceNode> nodeSearch = new LinkedList<>();
        Set<GraphNode> unvisitedNodes = new HashSet<>();
        List<GraphNode> nl = graph.getNodes();
        int distanceBetweenNodes = 1;
        
        for (int i = 0; i < nl.size(); i++) {
        	GraphNode node = nl.get(i);
            GameStateManager.Square tile = node.getNodeValue();
            //Skip fire nodes
            if (tile.isFire()) {
                continue;
            }
            //Add all non-player nodes to unvisited set
            if (!tile.isPlayer()) 
            {
                unvisitedNodes.add(node);
            }
            //Add player nodes to search list
            if (node.getNodeValue() == startingTile) {
                node.setPlayerDistancesZero(node.getNodeValue());
                Distance.DistanceNode distanceNode = new Distance().new DistanceNode(node, node.getNodeValue());
                nodeSearch.add(distanceNode);
            }

        }

        // While there are still unvisited nodes, search the board iteratively
        while (!unvisitedNodes.isEmpty()) {
            // Generate a new search list using the previous list and the current distance
            nodeSearch = allDistancesHelper(nodeSearch, distanceBetweenNodes, unvisitedNodes);
            distanceBetweenNodes++;

            // If the returned search list is empty, the remaining unvisited nodes must be unreachable
            if (nodeSearch.isEmpty()) {
                break;
            }
        }
	}


    /**
     * Sets the kDist and qDist values for each node exactly
     * one Chess Queen move away from the list of starting nodes.
     * Returns the list of nodes that were visited and had not been
     * visited before.
     * @param startingNodes List of starting nodes
     * @param qDist the Queens Distance value away from the player
     * @param unvisited a set of unvisited nodes
     * @return a List of newly visited nodes
     */

    private static List<DistanceNode> allDistancesHelper(List<DistanceNode> initiaNode, int queenDistance, Set<GraphNode> unvisitedNode){

        //A list of all newly visited startingNodes this iteration
        List<DistanceNode> newVistedNodes = new LinkedList<>();

        for(DistanceNode start : initiaNode) {

        	GraphNode initNode = start.node;
            unvisitedNode.remove(initNode);

            //Move in each direction 1 tile at a time
            for (GraphEdge.Direction direction : GraphEdge.Direction.getAllDirections()) {

            	GraphEdge current = initNode.getExistingEdge(direction);

                //kDist is set to the starting startingNodes k distance from the player
                int arrowDistance = 1;
                if(start.nextPlayer.isWhite())
                    arrowDistance = initNode.getKingDistanceWhite();
                else if(start.nextPlayer.isBlack())
                    arrowDistance = initNode.getKingDistanceBlack();

                //Keep moving until we hit a wall, fire tile or another player
                while(current!=null){
                	GraphNode currNode = current.getTargetNode();

                    //Update distances
                    if(start.getNextPlayer().isWhite()){
                        if(currNode.getQueenDistanceWhite() > queenDistance) currNode.setQueenDistanceWhite(queenDistance);
                        if(currNode.getKingDistanceWhite() > arrowDistance) currNode.setKingDistanceWhite(++arrowDistance);
                    }
                    
                    else if(start.getNextPlayer().isBlack())
                    {
                        if(currNode.getQueenDistanceBlack() > queenDistance) currNode.setQueenDistanceBlack(queenDistance);
                        if(currNode.getKingDistanceBlack() > arrowDistance) currNode.setKingDistanceBlack(++arrowDistance);
                    }

                    //Add the node to the return list if it was visited for the first time this iteration
                    if (unvisitedNode.remove(currNode)) {
                        DistanceNode distanceNode = new Distance().new DistanceNode(currNode, start.nextPlayer);
                        newVistedNodes.add(distanceNode);
                    }

                    //Move to the next tile
                    current = currNode.getExistingEdge(direction);
                }


            }
        }

        return newVistedNodes;
    }


}
