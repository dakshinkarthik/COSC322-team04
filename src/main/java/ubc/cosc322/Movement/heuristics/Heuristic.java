package ubc.cosc322.movement.heuristics;

import ubc.cosc322.movement.Graph;
import ubc.cosc322.GameStateManager;

public class Heuristic {

    private Heuristic(){

    }

    public static float calculateT(Graph board, GameStateManager.Tile turn){
       
    	float w = calculateW(board);

        float f1 = function1(w,board);
        float f2 = function2(board);
        float f3 = function3(w,board);
        float f4 = function4(w,board);

        double magnitude = Math.sqrt(f1*f1 + f2*f2+ f3*f3 + f4*f4);

        float t1 = 0;
        float t2 = 0;

        for(Graph.Node n : board.getNodes()){
        	
                if(!n.getValue().isEmpty()) continue;
                t1 += calculateTi(turn, n.getQdist1(), n.getQdist2());
                t2 += calculateTi(turn, n.getKdist1(), n.getKdist2());
                
        }

        float c1 = calculateC1(board);
        float c2 = calculateC2(board);

        double p1 = (f1/magnitude) * t1;
        double p2 = (f2/magnitude) * c1;
        double p3 = (f3/magnitude) * c2;
        double p4 = (f4/magnitude) * t2;

        return (float) (p1 + p2 + p3 + p4);
    }

    private static float calculateTi(GameStateManager.Tile player, int dist1, int dist2){
        float k = 1/5f;

        //n = m = infinity
        if(dist1 == Integer.MAX_VALUE && dist2 == Integer.MAX_VALUE) return 0;
        //n = m < infinity
        else if(dist1 == dist2) return player == GameStateManager.Tile.WHITE ? k : -k;
        //n < m (player 1 is closer)
        else if(dist1 < dist2) return 1;
        //n > m (player 2 is closer)
        else return -1;
    }

    private static float calculateC1(Graph board){
        float sum = 0;
        for (Graph.Node n : board.getNodes()) {
            sum += Math.pow(2, -n.getQdist1()) - Math.pow(2, -n.getQdist2());
        }

        return 2 * sum;
    }

    private static float calculateC2(Graph board){
        float sum = 0;
        for (Graph.Node n : board.getNodes()) {
            float difference = (n.getKdist2() - n.getKdist1()) / 6f;
            sum += Math.min(1, Math.max(-1, difference));
        }

        return sum;
    }

    private static float calculateW(Graph board){
        float sum = 0;
        for (Graph.Node n: board.getNodes()) {
            sum += Math.pow(2, -Math.abs(n.getQdist1()-n.getQdist2()));
        }
        return sum;
    }

    /*TODO Functions need implementation
        Should fluctuate in importance over the course of the game
    */

    //Increasingly important during the game
    //Gives good estimates of expected territory shortly before filling phase
    private static float function1(float w, Graph board){
 
        w = 100; 
	    for(Graph.Node n : board.getNodes()){
	       if(!n.getValue().isEmpty()) w--;
	       
	    }
	        return w;
    }

    //Supports positional play in the opening
    //Smooths transition between the beginning and later phases of the game
//    private static float function2(float w, Graph board){
//    	
//    	w = 0;
// 	   
//   	    for(Graph.Node n : board.getNodes()){
//   	       if(!n.getValue().isEmpty()) w++;
//   	    }
//   	    
//   	    w = ((w-30)/10);
//   	    w *= w; 
//   	    w *= (-1);
//   	    w += 40; 
//   	    return w;
//	        
//    	}
    
//    public static float function2(float factor, Graph board) {
//        float w = 0;
//        for(Graph.Node n : board.getNodes()) {
//        	for (Graph.Edge e: n.getEdges()) {
//                if (e == null) {
//                	System.out.println("this is not empty");
//                    w++;
//                   
//                }
//            }
//        }
//        
//        return  w;
//    }
//    	

    private static float function2(Graph board) {
        float value = 0;

        for (Graph.Node n : board.getNodes()) {
            if (n.getValue().isEmpty()) {
                continue;
            }
            int qDist = (n.getValue() == GameStateManager.Tile.WHITE) ? n.getQdist1() : n.getQdist2();
            int kDist = (n.getValue() == GameStateManager.Tile.WHITE) ? n.getKdist1() : n.getKdist2();

            value += Math.pow(qDist, 2) - Math.pow(kDist, 2);
        }

        return value;
    }
        
  

    //Supports positional play in the opening
    //Smooths transition between the beginning and later phases of the game
    private static float function3(float w, Graph board){

    	w = 0;
    	   
   	    for(Graph.Node n : board.getNodes()){
   	       if(!n.getValue().isEmpty()) {
   	    	   w++;
   	    	
   	       }
   	    }
   	    
   	    w = ((w-60)/10);
   	    w *= w; 
   	    w *= (-1);
   	    w += 40; 
   	    return w;
  
        
    }

    //Most important at the beginning of the game
    private static float function4 (float w, Graph board){
      	
        w = 0; 
        for(Graph.Node n : board.getNodes()){
           if(!n.getValue().isEmpty()) w++;
        }
            return w;

    }

}