public class Simulator {
    private double blockageParam;
    private double heightParam;
    private double bumpinessParam;
    private double clearedParam;

    public Simulator(double[] gene) {
        this.blockageParam = gene[0];
        this.heightParam = gene[1];
        this.bumpinessParam = gene[2];
        this.clearedParam = gene[3];
    }

    public int pickMove(State s, int[][] legalMoves) {
        return Logic.getBestMove(s, legalMoves, blockageParam, heightParam,
            bumpinessParam, clearedParam);
    }

    public int getScore() {
        State s = new State();
        while(!s.hasLost()) {
            s.makeMove(pickMove(s,s.legalMoves()));
        }
        return s.getRowsCleared();
    }
}
