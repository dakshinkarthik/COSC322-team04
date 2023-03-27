package ubc.cosc322.Graph;

import ubc.cosc322.GameStateManager;
import ubc.cosc322.Algorithm.Distance;
import java.util.ArrayList;
import java.util.List;


public class Graph {

    private final List<GraphNode> nodes;
    private final int height;
    private final int width;
    private float heuristicValue;

    /**
     * Creates an identical deep copy of the source graph
     * @param original
     * @return a copy of the source graph
     */
    public static Graph cloneGraph(Graph original){
        Graph clone = new Graph(original.nodes.size(), original.height, original.width);
        for (GraphNode n: original.getNodes()) {
            clone.nodes.add(GraphNode.copy(n));
        }


        for (GraphNode n: original.getNodes()) {
            int index = n.getNodeId();

            for(Edge e: n.getEdges()){
                int otherIndex = e.other.getNodeId();

                GraphNode copyNode = clone.nodes.get(index);
                copyNode.getEdges().add(Edge.copy(e, clone.nodes.get(otherIndex)));
            }

        }

        return clone;
    }

    

    private Graph(int size, int height, int width){
        this.height = height;
        this.width = width;
        nodes = new ArrayList<>(size);
    }

    public Graph(int[][] board) {
        height = board.length;
        width = board[0].length;

        nodes = new ArrayList<>(height * width);

        initializeGraph(board);
    }

    /**
     * Takes a player's move information and updates the graph accordingly.
     * @param move A move record containing the move information
     * @param player
     */
    public void updateGraph(Moves.Move move, GameStateManager.Square player){

        if(!player.isPlayer()) return;


        GraphNode currNode = nodes.get(move.current_Index());
        GraphNode arrowNode = nodes.get(move.arrow_Index());
        GraphNode nextNode = nodes.get(move.next_Index());

        //Set current to empty
        currNode.setNodeValue(GameStateManager.Square.EMPTY);
        //Enable edges for all connected nodes
        toggleConnectedNodeEdges(currNode, true);

        //Set next to player
        nextNode.setNodeValue(player);
        //Disable edges for all connected nodes
        toggleConnectedNodeEdges(nextNode, false);

        //Set arrow to arrow
        arrowNode.setNodeValue(GameStateManager.Square.ARROW);

        //Disable edges for all connected nodes
        toggleConnectedNodeEdges(arrowNode, false);

        //Refresh the distances for all nodes
        updateDistances();
    }

    private void initializeGraph(int[][] board){

        createNodes(board);

        //Connect nodes to their neighbors
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int index = y * width + x;

                GraphNode node = nodes.get(index);

                //Connect north node
                if(y - 1 >= 0){
                    int north = index - width;
                    GraphNode northNode = nodes.get(north);
                    addEdge(node, northNode, Edge.Direction.NORTH, northNode.isEmpty());
                }

                //Connect northeast node
                if(y - 1 >= 0 && x + 1 < width){
                    int northEast = index - width + 1;
                    GraphNode northEastNode = nodes.get(northEast);
                    addEdge(node, northEastNode, Edge.Direction.NORTH_EAST, northEastNode.isEmpty());
                }

                //Connect east node
                if(x + 1 < width){
                    int east = index + 1;
                    GraphNode eastNode = nodes.get(east);
                    addEdge(node, eastNode, Edge.Direction.EAST, eastNode.isEmpty());
                }

                //Connect southeast node
                if(y + 1 < height && x + 1 < width){
                    int southEast = index + width + 1;
                    GraphNode southEastNode = nodes.get(southEast);
                    addEdge(node, southEastNode, Edge.Direction.SOUTH_EAST, southEastNode.isEmpty());
                }

                //Connect south node
                if(y + 1 < height){
                    int south = index + width;
                    GraphNode southNode = nodes.get(south);
                    addEdge(node, southNode, Edge.Direction.SOUTH, southNode.isEmpty());
                }

                //Connect southwest node
                if(y + 1 < height && x - 1 >= 0){
                    int southWest = index + width - 1;
                    GraphNode southWestNode = nodes.get(southWest);
                    addEdge(node, southWestNode, Edge.Direction.SOUTH_WEST, southWestNode.isEmpty());
                }

                //Connect west node
                if(x - 1 >= 0){
                    int west = index - 1;
                    GraphNode westNode = nodes.get(west);
                    addEdge(node, westNode, Edge.Direction.WEST, westNode.isEmpty());
                }

                //Connect northwest node
                if(y - 1 >= 0 && x - 1 >= 0){
                    int northWest = index - width - 1;
                    GraphNode northWestNode = nodes.get(northWest);
                    addEdge(node, northWestNode, Edge.Direction.NORTH_WEST, northWestNode.isEmpty());
                }
            }
        }

        updateDistances();

    }

    private void createNodes(int[][] board){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int index = y * width + x;
                GraphNode n = new GraphNode(index, GameStateManager.Square.valueOf(board[y][x]));
                nodes.add(n);
            }
        }
    }

    public List<GraphNode> getNodes(){
        return nodes;
    }

    private void addEdge(GraphNode n1, GraphNode n2, Edge.Direction direction, boolean enabled){
        if(n1.getEdges().size() == 8){
            return;
        }
        n1.getEdges().add(new Edge(n2, direction, enabled));
    }

    private void toggleConnectedNodeEdges(GraphNode n, boolean toggle) {
        for (Edge e : n.getEdges())
            for (Edge e2 : e.other.getEdges())
                if (e2.other == n)
                    e2.enabled = toggle;
    }

    private void updateDistances(){
        for(GraphNode n : nodes) n.resetDistances();
        Distance.allDistances(this, GameStateManager.Square.WHITE);
        Distance.allDistances(this, GameStateManager.Square.BLACK);
    }

    public List<GraphNode> getGraphNodes(){
        return nodes;
    }
    
    public void setHeuristicValue(float value) {
        this.heuristicValue = value;
    }
    public float getHeuristicValue() {
        return this.heuristicValue;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        for(int y = 0; y < height; y++){
            sb.append("[ ");
            for(int x = 0; x < width; x++){
                int index = y * width + x;
                GraphNode n = nodes.get(index);
                sb.append(n.getNodeValue().getId()).append(" ");
            }
            sb.append("]\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Graph g)) return false;

        if(nodes.size() != g.nodes.size()) return false;

        for(int i = 0; i < nodes.size(); i++){
            if(!nodes.get(i).equals(g.getNodes().get(i))) return false;
        }

        return true;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

    public static class Edge {

        public static Edge copy(Edge source, GraphNode otherCopy){
            return new Edge(otherCopy, source.direction, source.enabled);
        }

        public enum Direction {
            NORTH("North"),
            NORTH_EAST("North East"),
            EAST("East"),
            SOUTH_EAST("South East"),
            SOUTH("South"),
            SOUTH_WEST("South West"),
            WEST("West"),
            NORTH_WEST("North West");

            public final String label;

            Direction(String direction) {
                this.label = direction;
            }

            @Override
            public String toString(){
                return label;
            }

        }

        private final GraphNode other;
        private final Direction direction;
        private boolean enabled;

        public Edge(GraphNode other, Direction direction, boolean enabled){
            this.other = other;
            this.direction = direction;
            this.enabled = enabled;
        }

        @Override
        public String toString(){
            return "Node Id:" + other.getNodeId() + ", Node Value: "+ other.getNodeValue() + ", Direction: " + direction;
        }

        public GraphNode getNode(){
            return other;
        }

        public Direction getDirection() {
            return direction;
        }

        public boolean isEnabled() { return enabled; }

    }

    
}
