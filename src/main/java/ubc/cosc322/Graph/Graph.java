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
        toggleConnectedNodeEdges(initialNode, true);

        //Set next to player
        newNode.setNodeValue(currentPlayer);
        //Disable edges for all connected nodes
        toggleConnectedNodeEdges(newNode, false);

        //Set arrow to arrow
        arrowNode.setNodeValue(GameStateManager.Square.ARROW);

        //Disable edges for all connected nodes
        toggleConnectedNodeEdges(arrowNode, false);

        //Refresh the distances for all nodes
        updateDistances();
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
        for(int j = 0; j < h; j++){
            for(int i = 0; i < w; i++){
                int id = j * w + i;

                GraphNode node = nodeList.get(id);

                //Connect north node
                if(j - 1 >= 0){
                    int north = id - w;
                    GraphNode northNode = nodeList.get(north);
                    addEdge(node, northNode,GraphEdge.Direction.TOP, northNode.getNodeValue().isEmpty());
                }

                //Connect northeast node
                if(j - 1 >= 0 && i + 1 < w){
                    int northEast = id - w + 1;
                    GraphNode northEastNode = nodeList.get(northEast);
                    addEdge(node, northEastNode, GraphEdge.Direction.TOP_RIGHT, northEastNode.getNodeValue().isEmpty());
                }

                //Connect east node
                if(i + 1 < w){
                    int east = id + 1;
                    GraphNode eastNode = nodeList.get(east);
                    addEdge(node, eastNode, GraphEdge.Direction.RIGHT, eastNode.getNodeValue().isEmpty());
                }

                //Connect southeast node
                if(j + 1 < h && i + 1 < w){
                    int southEast = id + w + 1;
                    GraphNode southEastNode = nodeList.get(southEast);
                    addEdge(node, southEastNode, GraphEdge.Direction.BOTTOM_RIGHT, southEastNode.getNodeValue().isEmpty());
                }

                //Connect south node
                if(j + 1 < h){
                    int south = id + w;
                    GraphNode southNode = nodeList.get(south);
                    addEdge(node, southNode, GraphEdge.Direction.BOTTOM, southNode.getNodeValue().isEmpty());
                }

                //Connect southwest node
                if(j + 1 < h && i - 1 >= 0){
                    int southWest = id + w - 1;
                    GraphNode southWestNode = nodeList.get(southWest);
                    addEdge(node, southWestNode, GraphEdge.Direction.BOTTOM_LEFT, southWestNode.getNodeValue().isEmpty());
                }

                //Connect west node
                if(i - 1 >= 0){
                    int west = id - 1;
                    GraphNode westNode = nodeList.get(west);
                    addEdge(node, westNode, GraphEdge.Direction.LEFT, westNode.getNodeValue().isEmpty());
                }

                //Connect northwest node
                if(j - 1 >= 0 && i - 1 >= 0){
                    int northWest = id - w - 1;
                    GraphNode northWestNode = nodeList.get(northWest);
                    addEdge(node, northWestNode, GraphEdge.Direction.TOP_LEFT, northWestNode.getNodeValue().isEmpty());
                }
            }
        }

        updateDistances();

    }

   
    private void addEdge(GraphNode n1, GraphNode n2, GraphEdge.Direction direction, boolean enabled){
        if(n1.getAllEdges().size() == 8){
            return;
        }
        n1.getAllEdges().add(new GraphEdge(n2, direction, enabled));
    }

    private void toggleConnectedNodeEdges(GraphNode n, boolean toggle) {
        for (GraphEdge e : n.getAllEdges())
            for (GraphEdge e2 : e.getTargetNode().getAllEdges())
                if (e2.getTargetNode() == n)
                    e2.setEdgeExists(toggle);
    }

    private void updateDistances(){
        for(GraphNode n : nodeList) n.initializeAllDistances();
        Distance.allDistances(this, GameStateManager.Square.WHITE);
        Distance.allDistances(this, GameStateManager.Square.BLACK);
    }


    @Override
    public boolean equals(Object o){
        if(!(o instanceof Graph g)) return false;
        if(nodeList.size() != g.nodeList.size()) return false;
        for(int i = 0; i < nodeList.size(); i++){
            if(!nodeList.get(i).equals(g.getAllGraphNodes().get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

   }
