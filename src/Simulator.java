import java.util.Arrays;

public class Simulator implements Runnable {
    private double blockageParam;
    private double heightParam;
    private double bumpinessParam;
    private double clearedParam;
    private double towerParam;

    private double[] gene;

    private int limit;
    private int score;

    public Simulator(double[] gene) {
        this.gene = Arrays.copyOf(gene, gene.length);
        limit = Config.TURN_NUMBER_LIMIT;
    }

    public int pickMove(State s, int[][] legalMoves) {
        return Logic.getBestMove(s, legalMoves, gene);
    }

    public int getScore() {
        return score;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int x) {
        limit = x;
    }

    @Override
    public void run() {
        State s = new State();
        while(!s.hasLost()) {
            if (s.getRowsCleared() < limit) {
                s.makeMove(pickMove(s, s.legalMoves()));
            } else {
                break;
            }
        }
        score = s.getRowsCleared();
    }
}
