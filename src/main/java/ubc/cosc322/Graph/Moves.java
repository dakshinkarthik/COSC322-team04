package ubc.cosc322.Graph;

import ubc.cosc322.GameStateManager;
import java.util.*;

public class Moves {

    public static Map<Move, Graph> possMoves(Graph graph, GameStateManager.Square player){
        Map<Move, Graph> moveMap = new HashMap<>();

        List<GraphNode> playerNodes = getPlayerNodes(graph, player);
        Iterator<GraphNode> iter = playerNodes.iterator();
        GraphEdge.Direction[] directions = GraphEdge.Direction.getAllDirections();
        while (iter.hasNext()) {
            //Check all possible moves in each direction
            GraphNode currentNode = iter.next();
            for(GraphEdge.Direction nextDir : directions){

                GraphEdge next = currentNode.getExistingEdge(nextDir);

                while(next!=null){

                    //Check all possible arrow shows in each direction
                    for(GraphEdge.Direction arrowDir : directions){

                        GraphEdge arrow = next.getTargetNode().getAvailableOrStartEdge(currentNode, arrowDir );

                        while(arrow!=null){

                            //Create a new board state
                            Graph move_State = Graph.cloneGraph(graph);
                            Move move_Now = new Move(currentNode.getNodeId(), next.getTargetNode().getNodeId(), arrow.getTargetNode().getNodeId());
                            move_State.updateGraphWithNewMove(move_Now, player);

                            moveMap.put(move_Now, move_State);

                            arrow = arrow.getTargetNode().getAvailableOrStartEdge(currentNode, arrowDir);
                        }
                    }
                    next = next.getTargetNode().getExistingEdge(nextDir);
                }

            }
        }
        
        return moveMap;
    }

 

   

    private static List<GraphNode> getPlayerNodes(Graph graph, GameStateManager.Square player){
        List<GraphNode> playerNodes = new LinkedList<>();
        Iterator<GraphNode> iter = graph.getAllGraphNodes().iterator();
        //Determine all possible moves for each player
        while(iter.hasNext()){
            GraphNode currentNode = iter.next();
            if(currentNode.getNodeValue() == player)
                playerNodes.add(currentNode);
        }

        return playerNodes;
    }

    

    public record Move(int current_Index, int next_Index, int arrow_Index){

        

    }

}
