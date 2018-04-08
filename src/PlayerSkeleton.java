import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class PlayerSkeleton {

    private double[] gene = {0.01307248686504499, 0.07085066054482314, 0.2137893669188931, 0.3251117124846825, 0.2609226094746969, 0.8805255060758526};

    //implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        return Logic.getBestMove(s, legalMoves, gene);
    }

    public static void main(String[] args) {

        int[] sequence = null;
        File file = new File(Config.TRAINING_SEQUENCE);
        Scanner sc = null;

        try {
            sc = new Scanner(file);
            int N = sc.nextInt();
            sequence = new int[N];
            for (int i = 0; i < N; i++) {
                sequence[i] = sc.nextInt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }

        int t = 100;
        if (args.length == 1) {
            t = Integer.parseInt(args[0]);
        }

        State s = new State(sequence);
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while (!s.hasLost() && s.getTurnNumber() < Config.TURN_NUMBER_LIMIT) {
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
