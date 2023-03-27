package ubc.cosc322.Graph;

import ubc.cosc322.GameStateManager;
import java.util.*;

public class Moves {

    /**
     * Generates all the possible moves for a given player and
     * returns them as a list of graphs with each new
     * potential board state
     * @param graph
     * @param player
     * @return A list of graphs corresponding to each possible move
     */
    // public static Map<Move, Graph> possMoves(Graph graph, GameStateManager.Tile player){
    //     Map<Move, Graph> moveMap = new HashMap<>();

    //     List<Graph.Node> playerNodes = getPlayerNodes(graph, player);

    //     for (Graph.Node currentNode: playerNodes) {
    //         //Check all possible moves in each direction
    //         for(Graph.Edge.Direction nextDir : Graph.Edge.Direction.values()){

    //             Graph.Edge next = currentNode.getEdgeInDirection(nextDir);

    //             while(next!=null){

    //                 //Check all possible arrow shows in each direction
    //                 for(Graph.Edge.Direction arrowDir : Graph.Edge.Direction.values()){

    //                     Graph.Edge arrow = next.getNode().getEdgeInDirectionIgnoreStart(arrowDir, currentNode);

    //                     while(arrow!=null){

    //                         //Create a new board state
    //                         Graph move_State = Graph.copy(graph);
    //                         Move move = new Move(currentNode.getIndex(), next.getNode().getIndex(), arrow.getNode().getIndex());
    //                         move_State.updateGraph(move, player);

    //                         moveMap.put(move, move_State);

    //                         arrow = arrow.getNode().getEdgeInDirectionIgnoreStart(arrowDir, currentNode);
    //                     }
    //                 }
    //                 next = next.getNode().getEdgeInDirection(nextDir);
    //             }

    //         }
    //     }
        
    //     return moveMap;
    // }

    public static Map<Move, Graph> possMoves(Graph graph, GameStateManager.Square player){
        Map<Move, Graph> moveMap = new HashMap<>();

        List<GraphNode> playerNodes = getPlayerNodes(graph, player);
        Iterator<GraphNode> iter = playerNodes.iterator();
        Graph.Edge.Direction[] directions = Graph.Edge.Direction.values();
        while (iter.hasNext()) {
            //Check all possible moves in each direction
            GraphNode currentNode = iter.next();
            for(Graph.Edge.Direction nextDir : directions){

                Graph.Edge next = currentNode.getEdgeInDirection(nextDir);

                while(next!=null){

                    //Check all possible arrow shows in each direction
                    for(Graph.Edge.Direction arrowDir : directions){

                        Graph.Edge arrow = next.getNode().getEdgeInDirectionIgnoreStart(arrowDir, currentNode);

                        while(arrow!=null){

                            //Create a new board state
                            Graph move_State = Graph.cloneGraph(graph);
                            Move move_Now = new Move(currentNode.getNodeId(), next.getNode().getNodeId(), arrow.getNode().getNodeId());
                            move_State.updateGraph(move_Now, player);

                            moveMap.put(move_Now, move_State);

                            arrow = arrow.getNode().getEdgeInDirectionIgnoreStart(arrowDir, currentNode);
                        }
                    }
                    next = next.getNode().getEdgeInDirection(nextDir);
                }

            }
        }
        
        return moveMap;
    }

 

    /**
     * Finds all nodes in a graph corresponding to a specified player
     * and returns them as a list of nodes.
     * @param graph
     * @param player
     * @return List of player nodes
     */
    // private static List<Graph.Node> getPlayerNodes(Graph graph, GameStateManager.Tile player){
    //     List<Graph.Node> playerNodes = new LinkedList<>();
    //     //Determine all possible moves for each player
    //     for(Graph.Node n : graph.getNodes()){
    //         if(n.getValue() == player)
    //             playerNodes.add(n);
    //     }

    //     return playerNodes;
    // }

    private static List<GraphNode> getPlayerNodes(Graph graph, GameStateManager.Square player){
        List<GraphNode> playerNodes = new LinkedList<>();
        Iterator<GraphNode> iter = graph.getNodes().iterator();
        //Determine all possible moves for each player
        while(iter.hasNext()){
            GraphNode currentNode = iter.next();
            if(currentNode.getNodeValue() == player)
                playerNodes.add(currentNode);
        }

        return playerNodes;
    }

    

    public record Move(int current_Index, int next_Index, int arrow_Index){

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();

            List<Integer> currInd = GameStateManager.indexToCoordinates(current_Index);
            List<Integer> nextInd = GameStateManager.indexToCoordinates(next_Index);
            List<Integer> arrowInd = GameStateManager.indexToCoordinates(arrow_Index);

            sb.append("Queen current: (").append(currInd.get(0)).append(",").append(toLetter(currInd.get(1))).append(")\n");
            sb.append("Queen New: (").append(nextInd.get(0)).append(",").append(toLetter(nextInd.get(1))).append(")\n");
            sb.append("Arrow: (").append(arrowInd.get(0)).append(",").append(toLetter(arrowInd.get(1))).append(")\n");

            return sb.toString();
        }

        private char toLetter(int x){
            return (char) (x + 'a' - 1);
        }

    }

}
