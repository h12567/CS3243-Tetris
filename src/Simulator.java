import java.util.Random;

public class Simulator implements Runnable {

    private double[] gene;

    private int scoreLah;

    public Simulator(double[] gene) {
        this.gene = gene;
    }

    public int pickMove(State s, int[][] legalMoves) {
        return Logic.getBestMove(s, legalMoves, gene);
    }

    public int getScore() {
        return scoreLah;
    }

    @Override
    public void run() {
        Random random = new Random();
        State s = new State();
        while (!s.hasLost()) {
            if (s.getTurnNumber() < Config.TURN_NUMBER_LIMIT) {
                int next = random.nextInt(100);
                if (next < 10) {
                    s.nextPiece = 0;
                } else if (next < 20) {
                    s.nextPiece = 1;
                } else if (next < 30) {
                    s.nextPiece = 2;
                } else if (next < 40) {
                    s.nextPiece = 3;
                } else if (next < 50) {
                    s.nextPiece = 4;
                } else if (next < 75) {
                    s.nextPiece = 5;
                } else {
                    s.nextPiece = 6;
                }
                s.makeMove(pickMove(s, s.legalMoves()));
            } else {
                break;
            }
        }
        scoreLah = s.getRowsCleared();
    }
}
