public class Simulator implements Runnable {

    private double[] gene;

    private int scoreLah;
    private int[] sequence;

    public Simulator(double[] gene, int[] seq) {
        this.gene = gene;
        this.sequence = seq;
    }

    public int pickMove(State s, int[][] legalMoves) {
        return Logic.getBestMove(s, legalMoves, gene);
    }

    public int getScore() {
        return scoreLah;
    }

    @Override
    public void run() {
        State s = new State(sequence);
        while (!s.hasLost()) {
            if (s.getTurnNumber() == -1
                    || (s.getTurnNumber() != -1 && s.getTurnNumber() < Config.TURN_NUMBER_LIMIT)) {
                s.makeMove(pickMove(s, s.legalMoves()));
            } else {
                break;
            }
        }
        scoreLah = s.getRowsCleared();
    }
}
