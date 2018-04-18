import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.Random;

public class Logic {

    static class Helper implements Runnable {

        public static final int COLS = 10;  
        public static final int ROWS = 21;
        private State initialState;
        private State newState;
        private int move;

        private Pair1 value;
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

        public int getHighestColumn(State s) {
            int[][] field = s.getField();
            int[] top = s.getTop();
            int highestCol = 0;
            for (int i = 0;  i < COLS;  i++) {
                highestCol = Math.max(highestCol, top[i]);
            }
            return highestCol;
        }

        public Helper(State s, int move, double[] gene) {
            this.initialState = new State(s);
            this.newState = new State(s);
            this.move = move;
            this.gene = gene;

            value = new Pair1(0, -Double.MAX_VALUE);
        }


        public void updateValue(State s) {
            if (s.hasLost()) {
                return;
            }

            ExtendedState eS = new ExtendedState(s);
            if(getHighestColumn(s) > 12) {
                value = eS.calculateValueWithLookahead(gene);
            } else {
                value = new Pair1(0, eS.calculateValue(gene));
            }
        }

        public void run() {
            this.newState.makeMove(this.move);
            updateValue(this.newState);
        }
    }

    public static int getBestMove(State s, int[][] legalMoves, double[] gene) {
        int bestMove = 0;

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
            }
        
            Pair1 bestResult = new Pair1(1000, -Double.MAX_VALUE);

            for(int i = 0; i < legalMoves.length; i ++) {
                if (helpers[i].value.first < bestResult.first
                    || (helpers[i].value.first == bestResult.first && helpers[i].value.second > bestResult.second)) {
                    
                    if(helpers[i].value.first < bestResult.first) {
                        bestResult.first = helpers[i].value.first;
                    }
                    bestResult.second = helpers[i].value.second;
                    bestMove = i;
                }
            }

        } catch (InterruptedException e) {
            System.out.println("WTF");
        }

        return bestMove;
    }
}
