import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import javafx.util.Pair;
import java.util.Random;

public class Logic {

    static class Helper implements Runnable {

        public static final int COLS = 10;  
        public static final int ROWS = 21;
        private State initialState;
        private State newState;
        private int move;

        private Pair<Integer, Double> value;
        public int[] top;
        public int[][] field;

        double[] gene;

        public void cleanField() {
            for (int j = 0;  j < COLS;  j++) {
                for (int i = top[j];  i < ROWS;  i++) {
                    field[i][j] = 0;
                }
            }
        }

        public Helper(State s, int move, double[] gene) {
            this.initialState = new State(s);
            this.newState = new State(s);
            this.move = move;
            this.gene = gene;

            value = new Pair<>(Integer.MIN_VALUE, -Double.MAX_VALUE);
        }


        public void updateValue(State s) {
            if (s.hasLost()) {
                return;
            }

            ExtendedState eS = new ExtendedState(s);
            value = eS.calculateValueWithLookahead(gene);
        }

        public void run() {
            this.newState.makeMove(this.move);
            updateValue(this.newState);
        }
    }

    public static int getBestMove(State s, int[][] legalMoves, double[] gene) {
        int bestMove = 0;
       Pair<Integer, Double> bestResult = new Pair<>(Integer.MAX_VALUE, -Double.MAX_VALUE);

        Helper helpers[] = new Helper[legalMoves.length];
        Thread threads[] = new Thread[legalMoves.length];

        for (int i = 0; i < legalMoves.length; i++) {
            helpers[i] = new Helper(s, i, gene);
            threads[i] = new Thread(helpers[i]);
            threads[i].run();
        }

        try {
            for (int i = 0; i < legalMoves.length; i++) {
                threads[i].join();

                if (helpers[i].value.getKey() < bestResult.getKey() 
                    || (helpers[i].value.getKey() == bestResult.getKey() && helpers[i].value.getValue() > bestResult.getValue())) {
                    bestResult = helpers[i].value;
                    bestMove = i;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("WTF");
        }

        return bestMove;
    }
}
