package ubc.cosc322.Graph;

import ubc.cosc322.GameStateManager;
import ubc.cosc322.Algorithm.Distance;
import java.util.ArrayList;
import java.util.List;


public class Graph {

    private List<GraphNode> nodeList;
    private int h;
    private int w;

    /**
     * Creates an identical deep copy of the source graph
     * @param original
     * @return a copy of the source graph
     */
    public static Graph cloneGraph(Graph original){
        Graph clone = new Graph(null, original.w, original.h);
        for (GraphNode node: original.getAllGraphNodes()) {
            clone.nodeList.add(GraphNode.cloneNode(node));
        }

        for (GraphNode node: original.getAllGraphNodes()) {
            int id = node.getNodeId();
            GraphNode copyNode = clone.nodeList.get(id);
            for(GraphEdge edge: node.getAllEdges()){
                int endPointId = edge.getTargetNode().getNodeId();
                copyNode.getAllEdges().add(GraphEdge.cloneEdge(edge, clone.nodeList.get(endPointId)));
            }
        }
        return clone;
    }

    public Graph(int[][] gameBoard, int ...dim) {
        if(gameBoard == null){
            w = dim[0];
            h = dim[1];
            nodeList = new ArrayList<>();
            return;
        }else{
            w = gameBoard[0].length;
            h = gameBoard.length;
            nodeList = new ArrayList<>(w * h);
            initializeGraph(gameBoard);
        }
       
    }

    
    /**
     * Takes a player's move information and updates the graph accordingly.
     * @param move A move record containing the move information
     * @param player
     */
    public List<GraphNode> getAllGraphNodes(){
        return nodeList;
    }

    public void updateGraphWithNewMove(Moves.Move currentMove, GameStateManager.Square currentPlayer){

        GraphNode initialNode = nodeList.get(currentMove.current_Index());
        GraphNode newNode = nodeList.get(currentMove.next_Index());
        GraphNode arrowNode = nodeList.get(currentMove.arrow_Index());

        //Set current to empty
        initialNode.setNodeValue(GameStateManager.Square.EMPTY);
        //Enable edges for all connected nodes
        connectOrDisconnectEdges(initialNode, true);

        //Set next to player
        newNode.setNodeValue(currentPlayer);
        //Disable edges for all connected nodes
        connectOrDisconnectEdges(newNode, false);

        //Set arrow to arrow
        arrowNode.setNodeValue(GameStateManager.Square.ARROW);

        //Disable edges for all connected nodes
        connectOrDisconnectEdges(arrowNode, false);

        //Refresh the distances for all nodes
        for(GraphNode nodes : nodeList){
            nodes.initializeAllDistances();
        } 
        Distance.allDistances(this, GameStateManager.Square.WHITE);
        Distance.allDistances(this, GameStateManager.Square.BLACK);
    }

    private void initializeGraph(int[][] board){

        for(int j = 0; j < h; j++){
            for(int i = 0; i < w; i++){
                int id = j * w + i;
                GraphNode n = new GraphNode(id, GameStateManager.Square.valueOf(board[j][i]));
                nodeList.add(n);
            }
        }

      //Connect nodes to their neighbors
      for(int row = 0; row < h; row++){
        for(int col = 0; col < w; col++){
            int id = row * w + col;
            GraphNode node = nodeList.get(id);

            if(row - 1 >= 0){
                int top = id - w;
                GraphNode topNode = nodeList.get(top);
                addEdgeToNeighbour(node, topNode,GraphEdge.Direction.TOP, topNode.getNodeValue().isEmpty());
            }

            if(row + 1 < h){
                int bottom = id + h;
                GraphNode bottomNode = nodeList.get(bottom);
                addEdgeToNeighbour(node, bottomNode, GraphEdge.Direction.BOTTOM, bottomNode.getNodeValue().isEmpty());
            }

            if(col + 1 < w){
                int right = id + 1;
                GraphNode rightNode = nodeList.get(right);
                addEdgeToNeighbour(node, rightNode, GraphEdge.Direction.RIGHT, rightNode.getNodeValue().isEmpty());
            }

            if(col - 1 >= 0){
                int left = id - 1;
                GraphNode leftNode = nodeList.get(left);
                addEdgeToNeighbour(node, leftNode, GraphEdge.Direction.LEFT, leftNode.getNodeValue().isEmpty());
            }

            if(row + 1 < h && col + 1 < w){
                int bottomRight = id + w + 1;
                GraphNode bottomRightNode = nodeList.get(bottomRight);
                addEdgeToNeighbour(node, bottomRightNode, GraphEdge.Direction.BOTTOM_RIGHT, bottomRightNode.getNodeValue().isEmpty());
            }

            if(row + 1 < h && col - 1 >= 0){
                int bottomLeft = id + w - 1;
                GraphNode bottomLeftNode = nodeList.get(bottomLeft);
                addEdgeToNeighbour(node, bottomLeftNode, GraphEdge.Direction.BOTTOM_LEFT, bottomLeftNode.getNodeValue().isEmpty());
            }

            if(row - 1 >= 0 && col + 1 < w){
                int topRight = id - w + 1;
                GraphNode topRightNode = nodeList.get(topRight);
                addEdgeToNeighbour(node, topRightNode, GraphEdge.Direction.TOP_RIGHT, topRightNode.getNodeValue().isEmpty());
            }

            if(row - 1 >= 0 && col - 1 >= 0){
                int topLeft = id - w - 1;
                GraphNode topLeftNode = nodeList.get(topLeft);
                addEdgeToNeighbour(node, topLeftNode, GraphEdge.Direction.TOP_LEFT, topLeftNode.getNodeValue().isEmpty());
            }
            
        }
    }
    }

    private void connectOrDisconnectEdges(GraphNode node, boolean toggle) {
        for (GraphEdge forwardEdge : node.getAllEdges())
            for (GraphEdge backwardEdge : forwardEdge.getTargetNode().getAllEdges())
                if (backwardEdge.getTargetNode() == node){
                    backwardEdge.setEdgeExists(toggle);
                }
                    
    }


    private void addEdgeToNeighbour(GraphNode startNode, GraphNode neighbourNode, GraphEdge.Direction direction, boolean exists){
        if(startNode.getAllEdges().size() == 8){
            return;
        }
        GraphEdge newEdge = new GraphEdge(neighbourNode, direction, exists);
        startNode.getAllEdges().add(newEdge);
    }

    @Override
    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Graph graph = (Graph) o;
        if(nodeList.size() != graph.nodeList.size()) {
            return false;
        }
        for(int i = 0; i < nodeList.size(); i++){
            if(!nodeList.get(i).equals(graph.getAllGraphNodes().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

   }
