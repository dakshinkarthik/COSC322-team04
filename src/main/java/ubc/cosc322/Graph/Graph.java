package ubc.cosc322.Graph;

import ubc.cosc322.GameStateManager;
import ubc.cosc322.Algorithm.Distance;
import java.util.ArrayList;
import java.util.List;


public class Graph {

    private List<GraphNode> nodeList;
    private int dim;

    public Graph(int[][] gameBoard, int ...dimension) {
        if(gameBoard == null){
            dim = dimension[0];
            nodeList = new ArrayList<>();
            return;
        }else{
            dim = gameBoard[0].length;
            nodeList = new ArrayList<>(dim * dim);
            connectGraphNodes(gameBoard);
        }
       
    }

    /**
     * Creates an identical deep copy of the source graph
     * @param original
     * @return a copy of the source graph
     */
    public static Graph cloneGraph(Graph original){
        Graph clone = new Graph(null, original.dim);
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

    private void connectGraphNodes(int[][] board){

        //Create nodes
        for(int j = 0; j < dim; j++){
            for(int i = 0; i < dim; i++){
                int id = j * dim + i;
                GraphNode n = new GraphNode(id, GameStateManager.Square.valueOf(board[j][i]));
                nodeList.add(n);
            }
        }

        //Connect nodes to their neighbors
        for(int row = 0; row < dim; row++){
            for(int col = 0; col < dim; col++){
                int id = row * dim + col;
                GraphNode node = nodeList.get(id);

                if(row - 1 >= 0){
                    int top = id - dim;
                    GraphNode topNode = nodeList.get(top);
                    addEdgeToNeighbour(node, topNode,GraphEdge.Direction.TOP, topNode.getNodeValue().isEmpty());
                }

                if(row + 1 < dim){
                    int bottom = id + dim;
                    GraphNode bottomNode = nodeList.get(bottom);
                    addEdgeToNeighbour(node, bottomNode, GraphEdge.Direction.BOTTOM, bottomNode.getNodeValue().isEmpty());
                }

                if(col + 1 < dim){
                    int right = id + 1;
                    GraphNode rightNode = nodeList.get(right);
                    addEdgeToNeighbour(node, rightNode, GraphEdge.Direction.RIGHT, rightNode.getNodeValue().isEmpty());
                }

                if(col - 1 >= 0){
                    int left = id - 1;
                    GraphNode leftNode = nodeList.get(left);
                    addEdgeToNeighbour(node, leftNode, GraphEdge.Direction.LEFT, leftNode.getNodeValue().isEmpty());
                }

                if(row + 1 < dim && col + 1 < dim){
                    int bottomRight = id + dim + 1;
                    GraphNode bottomRightNode = nodeList.get(bottomRight);
                    addEdgeToNeighbour(node, bottomRightNode, GraphEdge.Direction.BOTTOM_RIGHT, bottomRightNode.getNodeValue().isEmpty());
                }

                if(row + 1 < dim && col - 1 >= 0){
                    int bottomLeft = id + dim - 1;
                    GraphNode bottomLeftNode = nodeList.get(bottomLeft);
                    addEdgeToNeighbour(node, bottomLeftNode, GraphEdge.Direction.BOTTOM_LEFT, bottomLeftNode.getNodeValue().isEmpty());
                }

                if(row - 1 >= 0 && col + 1 < dim){
                    int topRight = id - dim + 1;
                    GraphNode topRightNode = nodeList.get(topRight);
                    addEdgeToNeighbour(node, topRightNode, GraphEdge.Direction.TOP_RIGHT, topRightNode.getNodeValue().isEmpty());
                }

                if(row - 1 >= 0 && col - 1 >= 0){
                    int topLeft = id - dim - 1;
                    GraphNode topLeftNode = nodeList.get(topLeft);
                    addEdgeToNeighbour(node, topLeftNode, GraphEdge.Direction.TOP_LEFT, topLeftNode.getNodeValue().isEmpty());
                }
                
            }
        }

        for(GraphNode nodes : nodeList){
            nodes.initializeAllDistances();
        } 
        Distance.allDistances(this, GameStateManager.Square.WHITE);
        Distance.allDistances(this, GameStateManager.Square.BLACK);
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
        neighbourNode.getAllEdges().add(newEdge);
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
