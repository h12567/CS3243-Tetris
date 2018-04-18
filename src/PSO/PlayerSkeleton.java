public class PlayerSkeleton {

    // private double[] gene = {0.06545828543097637, 0.5753446174725069, 0.15964887748502163,
    //         -0.3896014639203992, 0.6981523610521604, 0};

     private double[] gene = {8.59375,-10.195506393033147,-3.984375,-6.09375,-5.305685878057154,-1.09375,-3.59375,-3.1781569810552517};


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
            // s.draw();
            // s.drawNext(0, 0);
            // try {
            //     Thread.sleep(t);
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}
