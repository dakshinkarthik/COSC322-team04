package ubc.cosc322.Graph;

import ubc.cosc322.GameStateManager;
import ubc.cosc322.Algorithm.Distance;
import java.util.ArrayList;
import java.util.List;


public class Graph {

    /**
     * Creates an identical deep copy of the source graph
     * @param source
     * @return a copy of the source graph
     */
    public static Graph cloneGraph(Graph original){
        Graph clone = new Graph(original.nodes.size(), original.height, original.width);
        for (Node n: original.getNodes()) {
            clone.nodes.add(Node.copy(n));
        }


        for (Node n: original.getNodes()) {
            int index = n.getIndex();

            for(Edge e: n.edges){
                int otherIndex = e.other.getIndex();

                Node copyNode = clone.nodes.get(index);
                copyNode.edges.add(Edge.copy(e, clone.nodes.get(otherIndex)));
            }

        }

        return clone;
    }

    private final List<Node> nodes;
    private final int height;
    private final int width;
    private float heuristicValue;

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


        Node currNode = nodes.get(move.current_Index());
        Node arrowNode = nodes.get(move.arrow_Index());
        Node nextNode = nodes.get(move.next_Index());

        //Set current to empty
        currNode.setValue(GameStateManager.Square.EMPTY);
        //Enable edges for all connected nodes
        toggleConnectedNodeEdges(currNode, true);

        //Set next to player
        nextNode.setValue(player);
        //Disable edges for all connected nodes
        toggleConnectedNodeEdges(nextNode, false);

        //Set arrow to arrow
        arrowNode.setValue(GameStateManager.Square.ARROW);

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

                Node node = nodes.get(index);

                //Connect north node
                if(y - 1 >= 0){
                    int north = index - width;
                    Node northNode = nodes.get(north);
                    addEdge(node, northNode, Edge.Direction.NORTH, northNode.isEmpty());
                }

                //Connect northeast node
                if(y - 1 >= 0 && x + 1 < width){
                    int northEast = index - width + 1;
                    Node northEastNode = nodes.get(northEast);
                    addEdge(node, northEastNode, Edge.Direction.NORTH_EAST, northEastNode.isEmpty());
                }

                //Connect east node
                if(x + 1 < width){
                    int east = index + 1;
                    Node eastNode = nodes.get(east);
                    addEdge(node, eastNode, Edge.Direction.EAST, eastNode.isEmpty());
                }

                //Connect southeast node
                if(y + 1 < height && x + 1 < width){
                    int southEast = index + width + 1;
                    Node southEastNode = nodes.get(southEast);
                    addEdge(node, southEastNode, Edge.Direction.SOUTH_EAST, southEastNode.isEmpty());
                }

                //Connect south node
                if(y + 1 < height){
                    int south = index + width;
                    Node southNode = nodes.get(south);
                    addEdge(node, southNode, Edge.Direction.SOUTH, southNode.isEmpty());
                }

                //Connect southwest node
                if(y + 1 < height && x - 1 >= 0){
                    int southWest = index + width - 1;
                    Node southWestNode = nodes.get(southWest);
                    addEdge(node, southWestNode, Edge.Direction.SOUTH_WEST, southWestNode.isEmpty());
                }

                //Connect west node
                if(x - 1 >= 0){
                    int west = index - 1;
                    Node westNode = nodes.get(west);
                    addEdge(node, westNode, Edge.Direction.WEST, westNode.isEmpty());
                }

                //Connect northwest node
                if(y - 1 >= 0 && x - 1 >= 0){
                    int northWest = index - width - 1;
                    Node northWestNode = nodes.get(northWest);
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
                Node n = new Node(index, GameStateManager.Square.valueOf(board[y][x]));
                nodes.add(n);
            }
        }
    }

    private void addEdge(Node n1, Node n2, Edge.Direction direction, boolean enabled){
        if(n1.edges.size() == 8){
            return;
        }
        n1.edges.add(new Edge(n2, direction, enabled));
    }

    private void toggleConnectedNodeEdges(Node n, boolean toggle) {
        for (Edge e : n.edges)
            for (Edge e2 : e.other.edges)
                if (e2.other == n)
                    e2.enabled = toggle;
    }

    private void updateDistances(){
        for(Node n : nodes) n.resetDistances();
        Distance.allDistances(this, GameStateManager.Square.WHITE);
        Distance.allDistances(this, GameStateManager.Square.BLACK);
    }

    public List<Node> getNodes(){
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
                Node n = nodes.get(index);
                sb.append(n.value.getId()).append(" ");
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

        public static Edge copy(Edge source, Node otherCopy){
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

        private final Node other;
        private final Direction direction;
        private boolean enabled;

        public Edge(Node other, Direction direction, boolean enabled){
            this.other = other;
            this.direction = direction;
            this.enabled = enabled;
        }

        @Override
        public String toString(){
            return "Index:" + other.index + ", Value: "+ other.value + ", Direction: " + direction;
        }

        public Node getNode(){
            return other;
        }

        public Direction getDirection() {
            return direction;
        }

        public boolean isEnabled() { return enabled; }

    }

    public static class Node {

        public static Node copy(Node source){
            Node copy = new Node(source.index, source.value);
            copy.kdist1 = source.kdist1;
            copy.kdist2 = source.kdist2;
            copy.qdist1 = source.qdist1;
            copy.qdist2 = source.qdist2;

            return copy;
        }

        private final int index;
        private GameStateManager.Square value;

        private int qdist1;
        private int qdist2;
        private int kdist1;
        private int kdist2;

        private final List<Edge> edges;

        public Node(int index, GameStateManager.Square value){
            this.index = index;
            setValue(value);

            resetDistances();

            edges = new ArrayList<>();
        }

        /**
         * Sets all distances to Integer.MAX_VALUE
         */
        public void resetDistances(){
            qdist1 = Integer.MAX_VALUE;
            qdist2 = Integer.MAX_VALUE;
            kdist1 = Integer.MAX_VALUE;
            kdist2 = Integer.MAX_VALUE;
        }

        /**
         * Sets all distances to 0
         */
        public void zeroDistances(){
            qdist1 = 0;
            qdist2 = 0;
            kdist1 = 0;
            kdist2 = 0;
        }

        /**
         * Sets kDist and qDist to 0 for the given player
         * @param player
         */
        public void playerZeroDistances(GameStateManager.Square player){
            if(player.isWhite()){
                qdist1 = 0;
                kdist1 = 0;
            }else if(player.isBlack()){
                qdist2 = 0;
                kdist2 = 0;
            }
        }

        /**
         * Returns an edge connected to the node in the given direction
         * @param dir
         * @return An edge in the specified direction,
         * or null if the edge is disabled or non-existent.
         */
        public Edge getEdgeInDirection(Edge.Direction dir){
            for(Edge e : edges){
                if (e.getDirection() == dir && e.enabled) return e;
            }
            return null;
        }

        public Edge getEdgeInDirectionIgnoreStart(Edge.Direction dir, Node start){
            for(Edge e : edges){
                if (
                        (e.getDirection() == dir && e.enabled) ||
                        (e.getDirection() == dir && e.getNode().equals(start))
                ) return e;
            }
            return null;
        }

        public int getIndex(){ return index; }

        public boolean isEmpty(){
            return value.isEmpty();
        }

        public GameStateManager.Square getValue() {
            return value;
        }

        public void setValue(GameStateManager.Square value) {
            if(value.isFire()) zeroDistances();
            this.value = value;
        }

        public int getQdist1() {
            return qdist1;
        }

        public void setQdist1(int qdist1) {
            this.qdist1 = qdist1;
        }

        public int getQdist2() {
            return qdist2;
        }

        public void setQdist2(int qdist2) {
            this.qdist2 = qdist2;
        }

        public int getKdist1() {
            return kdist1;
        }

        public void setKdist1(int kdist1) {
            this.kdist1 = kdist1;
        }

        public int getKdist2() {
            return kdist2;
        }

        public void setKdist2(int kdist2) {
            this.kdist2 = kdist2;
        }

        public List<Edge> getEdges(){
            return edges;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Node n && n.index == index && n.value == value;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

}
