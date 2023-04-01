package ubc.cosc322.Graph;

import java.util.ArrayList;
import java.util.List;


import ubc.cosc322.GameStateManager;

public class GraphNode {

    private int id;
    private  List<GraphEdge> edgeList;
    private int kingDistanceWhite;
    private int kingDistanceBlack;
    private int queenDistanceWhite;
    private int queenDistanceBlack;
    private GameStateManager.Square squareValue;

    

    public GraphNode(int id, GameStateManager.Square value){
        this.id = id;
        edgeList = new ArrayList<>();
        setNodeValue(value);
        initializeAllDistances();
    }


     //initializes all distances to infinity (max value of int)
     public void initializeAllDistances(){
        setQueenDistanceWhite(Integer.MAX_VALUE);
        setQueenDistanceBlack(Integer.MAX_VALUE);
        setKingDistanceWhite(Integer.MAX_VALUE);
        setKingDistanceBlack(Integer.MAX_VALUE);
    }


    /**
     * Creates an identical deep copy of the source node
     * @param original
     * @return a copy of the source node
     */
    public static GraphNode cloneNode(GraphNode original){
        GraphNode clone = new GraphNode(original.id, original.squareValue);
        clone.setKingDistanceWhite(original.getKingDistanceWhite());
        clone.setKingDistanceBlack(original.getKingDistanceBlack());
        clone.setQueenDistanceWhite(original.getQueenDistanceWhite());
        clone.setQueenDistanceBlack(original.getQueenDistanceBlack());
        return clone;
    }

    
    public void setPlayerDistancesZero(GameStateManager.Square player){
        if(player.isBlack()){
            setQueenDistanceBlack(0);
            setKingDistanceBlack(0);  
        }else {
            setQueenDistanceWhite(0);
            setKingDistanceWhite(0);
        }
    }

    
    public GraphEdge getExistingEdge(GraphEdge.Direction direction){
        for(GraphEdge edge : edgeList){

            if(!edge.getEdgeExists()){
                continue;
            }
            if (edge.getEdgeDirection() == direction) {
                return edge;
            }
        }
        return null;
    }


    public void setNodeValue(GameStateManager.Square value) {
        if(value.isFire()) {
            setQueenDistanceBlack(0);
            setKingDistanceBlack(0);  
            setQueenDistanceWhite(0);
            setKingDistanceWhite(0);
        }
        this.squareValue = value;
    }

    public GraphEdge getAvailableOrStartEdge(GraphNode start, GraphEdge.Direction direction){
        for(GraphEdge edge : edgeList){
            if(edge.getEdgeDirection() != direction) {
                continue;
            }
            if ((edge.getEdgeExists() || edge.getTargetNode().equals(start))) {
                return edge;
            }
        }
        return null;
    }

    public int getNodeId(){ 
        return id; 
    }

    public GameStateManager.Square getNodeValue() {
        return squareValue;
    }

    public List<GraphEdge> getEdges(){
        return edgeList;
    }


    public int getQueenDistanceWhite() {
        return queenDistanceWhite;
    }

    public void setQueenDistanceWhite(int queenDistanceWhite) {
        this.queenDistanceWhite = queenDistanceWhite;
    }

    public int getQueenDistanceBlack() {
        return queenDistanceBlack;
    }

    public void setQueenDistanceBlack(int queenDistanceBlack) {
        this.queenDistanceBlack = queenDistanceBlack;
    }

    public int getKingDistanceWhite() {
        return kingDistanceWhite;
    }

    public void setKingDistanceWhite(int kingDistanceWhite) {
        this.kingDistanceWhite = kingDistanceWhite;
    }

    public int getKingDistanceBlack() {
        return kingDistanceBlack;
    }

    public void setKingDistanceBlack(int kingDistanceBlack) {
        this.kingDistanceBlack = kingDistanceBlack;
    }

   

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphNode n = (GraphNode) o;
        if(id==n.id && squareValue==n.squareValue) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
