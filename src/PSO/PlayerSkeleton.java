public class PlayerSkeleton {

    // private double[] gene = {0.06545828543097637, 0.5753446174725069, 0.15964887748502163,
    //         -0.3896014639203992, 0.6981523610521604, 0};

     private double[] gene = {-0.3248439725450173,-0.08835846891082666,0.32662901476308354,-0.5749706245176583,-0.22131977084810642,-0.12949923671115146,-0.25600602975964676,-0.5640034233754717};

    //implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        return Logic.getBestMove(s, legalMoves, gene);
    }

    public static void main(String[] args) {
        for(int m = 0; m < 50; m ++) {
            System.out.println("GOOG VERSION " + m);
            int t = 100;
            if (args.length == 1) {
                t = Integer.parseInt(args[0]);
            }

            State s = new State();
            new TFrame(s);
            PlayerSkeleton p = new PlayerSkeleton();
            int cnt = 0;
            while (!s.hasLost()) {
                s.makeMove(p.pickMove(s, s.legalMoves()));
                cnt ++;
                if(cnt % 200000 == 0) {
                    System.out.println("HERE " + s.getRowsCleared());
                }
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

}
