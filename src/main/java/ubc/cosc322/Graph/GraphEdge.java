package ubc.cosc322.Graph;


public class GraphEdge {

        private final Direction edgeDirection;
        private boolean edgeExists;
        private final GraphNode targetNode;

        public GraphEdge(GraphNode targetNode, Direction direction, boolean edgeExists){
        	this.edgeDirection = direction;
            this.edgeExists = edgeExists;
        	this.targetNode = targetNode; 
        }

        public static GraphEdge clone(GraphEdge edge, GraphNode connectedNode){
            return new GraphEdge(connectedNode, edge.getEdgeDirection(), edge.edgeExists);
        }

        public static class Direction {
        	public static final Direction TOP = new Direction("Top");
            public static final Direction TOP_RIGHT = new Direction("Top-Right");
            public static final Direction TOP_LEFT = new Direction("Top-Left");
            public static final Direction RIGHT = new Direction("Right");
            public static final Direction LEFT = new Direction("Left");
            public static final Direction BOTTOM_RIGHT = new Direction("Bottom-Right");
            public static final Direction BOTTOM = new Direction("Bottom");
            public static final Direction BOTTOM_LEFT = new Direction("Bottom-Left");

            public  String directionLabel;

            Direction(String directionLabel) {
                this.directionLabel = directionLabel;
            }

            public String getDirectionLabel() {
                return directionLabel;
            }
            
            public static GraphEdge.Direction[] directions = {
            	    GraphEdge.Direction.TOP,
            	    GraphEdge.Direction.TOP_RIGHT,
            	    GraphEdge.Direction.RIGHT,
            	    GraphEdge.Direction.BOTTOM_RIGHT,
            	    GraphEdge.Direction.BOTTOM,
            	    GraphEdge.Direction.BOTTOM_LEFT,
            	    GraphEdge.Direction.LEFT,
            	    GraphEdge.Direction.TOP_LEFT
            	};
            
            public static GraphEdge.Direction[] getAllDirections(){
            	return directions;
            }

        } 

        public Direction getEdgeDirection() {
            return edgeDirection;
        }
        
        public GraphNode getTargetNode(){
            return targetNode;
        }

        public void setEdgeExists(boolean edgeExists) {
            this.edgeExists = edgeExists;
        }

        public boolean getEdgeExists() { 
        	return edgeExists; 
        }

       
        @Override
        public String toString(){
        	 return String.format("Index: %d, Value: %s, Direction: %s", targetNode.getNodeId(), targetNode.getNodeValue(), edgeDirection.getDirectionLabel());
        }
} 