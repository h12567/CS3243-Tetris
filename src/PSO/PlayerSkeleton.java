public class PlayerSkeleton {

    // private double[] gene = {0.06545828543097637, 0.5753446174725069, 0.15964887748502163,
    //         -0.3896014639203992, 0.6981523610521604, 0};

     private double[] gene = {5.2754064283197515, -7.482132285849006, -4.004265343670941, -4.695960336613728, -3.871846939245422, -5.0660076175918505, -8.369042512375499, -3.8848884135100454 };


    //implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        return Logic.getBestMove(s, legalMoves, gene);
    }

    public static void main(String[] args) {

    System.out.println("GOOG VERSION");
        int t = 100;
        if (args.length == 1) {
            t = Integer.parseInt(args[0]);
        }

        State s = new State();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            s.draw();
            s.drawNext(0, 0);
            try {
                Thread.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}
