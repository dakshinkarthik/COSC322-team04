package ubc.cosc322.Algorithm;

import ubc.cosc322.Graph.Graph;
import ubc.cosc322.GameStateManager;

public class Heuristic {

    private Heuristic(){

    }

    public static float calculateT(Graph board, GameStateManager.Square turn){
       
    	float f1 = 0;                             //calculates the number of filled tiles
        for(Graph.Node n : board.getNodes()) {
            if(!n.getValue().isEmpty()) {
                f1++;
            }
        }
        f1 = 100 - f1;
        
        
        float f2 = 0;
        for (Graph.Node n : board.getNodes()) {
            if (n.getValue().isEmpty()) {
                continue;
            }

            int qDist = (n.getValue() == GameStateManager.Square.WHITE) ? n.getQdist1() : n.getQdist2();
            int kDist = (n.getValue() == GameStateManager.Square.WHITE) ? n.getKdist1() : n.getKdist2();

            f2 += Math.pow(qDist, 2) - Math.pow(kDist, 2);
        }
        
        
        float f3 = 0;                              
        for (Graph.Node n : board.getNodes()) {
            if (!n.getValue().isEmpty()) {
                f3++;
            }
        }
        f3 = (f3 - 60) / 10;
        f3 = -(f3 * f3) + 40;

        
        float f4 = 0;                              //calculates the number of empty tiles
        f4 = 100 - f1;
        

        double weight = Math.sqrt(f1*f1 + f2*f2 + f3*f3 + f4*f4);

        float t1 = 0;
        float t2 = 0;

        for (Graph.Node n : board.getNodes()) {
            if (n.getValue().isEmpty()) {
                continue;
            }
            
            t1 += Ti_value(turn, n.getQdist1(), n.getQdist2());
            t2 += Ti_value(turn, n.getKdist1(), n.getKdist2());
            
        }

        double x1 = (f1/weight) * t1;
        double x2 = (f2/weight) * C1_value(board);
        double x3 = (f3/weight) * C2_value(board);
        double x4 = (f4/weight) * t2;

        return (float) (x1 + x2 + x3 + x4);
    }

    private static float Ti_value(GameStateManager.Square player, int dist1, int dist2){
    	float k = 1/5f;

        if(dist1 == Integer.MAX_VALUE && dist2 == Integer.MAX_VALUE) 
            return 0;
        else if(dist1 == dist2) {
            if (player ==  GameStateManager.Square.BLACK)
                return -k;
            else
                return k;  
        }  
        else 
            return dist1 < dist2 ? 1 : -1;
    }

    private static float C1_value(Graph board){
    	float sum = 0;
        for (Graph.Node n : board.getNodes()) {
            float term1 = (float) Math.pow(2, -n.getQdist1());
            float term2 = (float) Math.pow(2, -n.getQdist2());
            sum += term1 - term2;
        }
        return 2 * sum;
    }

    private static float C2_value(Graph board){
        float sum = 0;
        for (Graph.Node n : board.getNodes()) {
            sum += Math.min(1, Math.max(-1, ((n.getKdist2() - n.getKdist1()) / 6f)));
        }

        return sum;
    }


}
