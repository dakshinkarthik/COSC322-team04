package ubc.cosc322.Algorithm;

import ubc.cosc322.Graph.Graph;
import ubc.cosc322.GameStateManager;

import java.util.*;

public class Distance {
public class DistanceNode {
    private Graph.Node node = null;
    private GameStateManager.Square nextPlayer = null;

    public DistanceNode(Graph.Node node, GameStateManager.Square nextPlayer) {
        this.node = node;
        this.nextPlayer = nextPlayer;
    }

    public Graph.Node getNode() {
        return node;
    }

    public GameStateManager.Square getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DistanceNode)) return false;
        DistanceNode other = (DistanceNode) o;
        return Objects.equals(node, other.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}

public static void calculateDistances(Graph graph, GameStateManager.Square initialTile) {
    List<DistanceNode> search = new LinkedList<>();
    Set<Graph.Node> unvisitedNodes = new HashSet<>();
    List<Graph.Node> nodeList = graph.getNodes();
    int distance = 1;

    for (Graph.Node n : nodeList) {
        GameStateManager.Square tile = n.getValue();
        if (tile.isFire() == true) {
            continue;
        }
        if (tile.isPlayer() == false) {
            unvisitedNodes.add(n);
        }
        if (n.getValue() == initialTile) {
            n.playerZeroDistances(n.getValue());
            DistanceNode distanceNode = new Distance().new DistanceNode(n, n.getValue());
            search.add(distanceNode);
        }
    }

    while (unvisitedNodes.isEmpty() == false) {
        search = findDistances(search, distance, unvisitedNodes);
        distance = distance+1;

        if (search.isEmpty() == true) {
            break;
        }
    }
}

private static List<DistanceNode> findDistances(List<DistanceNode> initialNodes, int qDist, Set<Graph.Node> unvisited) {
    List<DistanceNode> visitedNodes = new LinkedList<>();

    for (DistanceNode start : initialNodes) {
        Graph.Node initialNode = start.node;
        unvisited.remove(initialNode);

        for (Graph.Edge.Direction dir : Graph.Edge.Direction.values()) {

            Graph.Edge currentEdge = initialNode.getEdgeInDirection(dir);
            int arrowDist = 1;            
            if (start.nextPlayer.isBlack()) 
            {
                arrowDist = initialNode.getKdist2();
            }

            if (start.nextPlayer.isWhite()) 
            {
                arrowDist = initialNode.getKdist1();
            } 

            while (currentEdge != null) {
                Graph.Node currentNode = currentEdge.getNode();

                
                if (start.getNextPlayer().isBlack()) 
                {
                    if (currentNode.getQdist2() > qDist)
                    {
                         currentNode.setQdist2(qDist);
                    }
                    if (currentNode.getKdist2() > arrowDist)
                    { 
                        currentNode.setKdist2(arrowDist++);
                    }
                }
                if (start.getNextPlayer().isWhite()) {
                    if (currentNode.getQdist1() > qDist) 
                    {
                        currentNode.setQdist1(qDist);
                    }
                    if (currentNode.getKdist1() > arrowDist)
                    { 
                        currentNode.setKdist1(arrowDist++);
                    }
                } 
                if (unvisited.remove(currentNode)) {
                    DistanceNode dn = new Distance().new DistanceNode(currentNode, start.nextPlayer);
                    
                    visitedNodes.add(dn);
                }

                currentEdge = currentNode.getEdgeInDirection(dir);
            }
        }
    }

    return visitedNodes;
}
}