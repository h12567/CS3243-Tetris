public class Simulator implements Runnable {
    private double blockageParam;
    private double heightParam;
    private double bumpinessParam;
    private double clearedParam;

    private int scoreLah;

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
