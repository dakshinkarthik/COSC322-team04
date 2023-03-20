package ubc.cosc322.movement.heuristics;

import ubc.cosc322.movement.Graph;
import ubc.cosc322.GameStateManager;

import java.util.*;

public class Distance {

	public class DistanceNode {
	    public static Graph.Node node = null;
	    public static GameStateManager.Tile nextPlayer = null;

	    public DistanceNode(Graph.Node node, GameStateManager.Tile nextPlayer) {
	        this.node = node;
	        this.nextPlayer = nextPlayer;
	    }

	    public Graph.Node getNode() {
	        return node;
	    }

	    public GameStateManager.Tile getNextPlayer() {
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

    public static void allDistances(Graph graph, GameStateManager.Tile startingTile){

        List<DistanceNode> nodeSearch = new LinkedList<>();
        Set<Graph.Node> unvisitedNodes = new HashSet<>();
        List<Graph.Node> nl = graph.getNodes();
        int distanceBetweenNodes = 1;
        
        for (int i = 0; i < nl.size(); i++) {
        	Graph.Node node = nl.get(i);
            GameStateManager.Tile tile = node.getValue();
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
            if (node.getValue() == startingTile) {
                node.playerZeroDistances(node.getValue());
                Distance.DistanceNode distanceNode = new Distance().new DistanceNode(node, node.getValue());
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

    private static List<DistanceNode> allDistancesHelper(List<DistanceNode> initiaNode, int queenDistance, Set<Graph.Node> unvisitedNode){

        //A list of all newly visited startingNodes this iteration
        List<DistanceNode> newVistedNodes = new LinkedList<>();

        for(DistanceNode start : initiaNode) {

        	Graph.Node initNode = start.node;
            unvisitedNode.remove(initNode);

            //Move in each direction 1 tile at a time
            for (Graph.Edge.Direction direction : Graph.Edge.Direction.values()) {

            	Graph.Edge current = initNode.getEdgeInDirection(direction);

                //kDist is set to the starting startingNodes k distance from the player
                int arrowDistance = 1;
                if(start.nextPlayer.isWhite())
                    arrowDistance = initNode.getKdist1();
                else if(start.nextPlayer.isBlack())
                    arrowDistance = initNode.getKdist2();

                //Keep moving until we hit a wall, fire tile or another player
                while(current!=null){
                	Graph.Node currNode = current.getNode();

                    //Update distances
                    if(start.getNextPlayer().isWhite()){
                        if(currNode.getQdist1() > queenDistance) currNode.setQdist1(queenDistance);
                        if(currNode.getKdist1() > arrowDistance) currNode.setKdist1(++arrowDistance);
                    }
                    
                    else if(start.getNextPlayer().isBlack())
                    {
                        if(currNode.getQdist2() > queenDistance) currNode.setQdist2(queenDistance);
                        if(currNode.getKdist2() > arrowDistance) currNode.setKdist2(++arrowDistance);
                    }

                    //Add the node to the return list if it was visited for the first time this iteration
                    if (unvisitedNode.remove(currNode)) {
                        DistanceNode distanceNode = new Distance().new DistanceNode(currNode, start.nextPlayer);
                        newVistedNodes.add(distanceNode);
                    }

                    //Move to the next tile
                    current = currNode.getEdgeInDirection(direction);
                }


            }
        }

        return newVistedNodes;
    }


}
