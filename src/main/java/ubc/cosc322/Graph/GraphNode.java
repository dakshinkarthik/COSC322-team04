package ubc.cosc322.Graph;

import java.util.ArrayList;
import java.util.List;

import ubc.cosc322.GameStateManager;
import ubc.cosc322.Graph.Graph.Edge;

public class GraphNode {

    private final int id;
    private GameStateManager.Square value;
    private final List<Edge> edges;

    private int qdist1;
    private int qdist2;
    private int kdist1;
    private int kdist2;

    

    public GraphNode(int id, GameStateManager.Square value){
        this.id = id;
        setNodeValue(value);
        resetDistances();
        edges = new ArrayList<>();
    }


    public static GraphNode copy(GraphNode source){
        GraphNode copy = new GraphNode(source.id, source.value);
        copy.kdist1 = source.kdist1;
        copy.kdist2 = source.kdist2;
        copy.qdist1 = source.qdist1;
        copy.qdist2 = source.qdist2;

        return copy;
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
            if (e.getDirection() == dir && e.isEnabled()) return e;
        }
        return null;
    }

    public Edge getEdgeInDirectionIgnoreStart(Edge.Direction dir, GraphNode start){
        for(Edge e : edges){
            if (
                    (e.getDirection() == dir && e.isEnabled()) ||
                    (e.getDirection() == dir && e.getNode().equals(start))
            ) return e;
        }
        return null;
    }

    public int getNodeId(){ return id; }

    public boolean isEmpty(){
        return value.isEmpty();
    }

    public GameStateManager.Square getNodeValue() {
        return value;
    }

    public void setNodeValue(GameStateManager.Square value) {
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
        return o instanceof GraphNode n && n.id == id && n.value == value;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
