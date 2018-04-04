public class Simulator implements Runnable {
    private double blockageParam;
    private double heightParam;
    private double bumpinessParam;
    private double clearedParam;
    private double towerParam;

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
        State s = new State();
        while(!s.hasLost()) {
            if (s.getTurnNumber() != -1 && s.getTurnNumber() < Config.TURN_NUMBER_LIMIT) {
                s.makeMove(pickMove(s, s.legalMoves()));
            } else {
                break;
            }
        }
        scoreLah = s.getRowsCleared();
    }
}
