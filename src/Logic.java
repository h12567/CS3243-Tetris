import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Logic {

    static class Helper implements Runnable {

        public static final int COLS = 10;  
        public static final int ROWS = 21;
        private State initialState;
        private State newState;
        private int move;

        private double value;
        public int[] top;
        public int[][] field;

        private double holeParam;
        private double rowTransitionParam;
        private double columnTransitionParam;
        private double highestColParam;
        private double wellParam;
        private double clearedParam;

        public void cleanField() {
            for (int j = 0;  j < COLS;  j++) {
                for (int i = top[j];  i < ROWS;  i++) {
                    field[i][j] = 0;
                }
            }
        }

        public int getAggregateHeight() {
            int aggregateHeight = IntStream.of(top).sum();
            return aggregateHeight;
        }
    
        public double getNumHoles(State s) {
            int[][] field = s.getField();
            int[] top = s.getTop();

            int numHoles = 0;
            for (int j = 0;  j < State.COLS;  j++) {
                if (top[j] != 0) {
                    for (int i = top[j] - 1;  i >= 0;  i--) {
                        if (field[i][j] == 0) {
                            numHoles++;
                        }
                    }
                }
            }
            return (double) numHoles * 10;
        }

        public double getRowTransitions(State s) {
            int[][] field = s.getField();
            int rowTransitions = 0;
            int lastCell = 1;
            for (int i = 0;  i < State.ROWS;  i++) {
                for (int j = 0;  j < State.COLS;  j++) {
                    if ((field[i][j] == 0) != (lastCell == 0)) {
                        rowTransitions++;
                    }
                    lastCell = field[i][j];
                }
                if (lastCell == 0) rowTransitions++;
            }
            return (double) rowTransitions;
        }

        public double getColTransitions(State s) {
            int[][] field = s.getField();
            int[] top = s.getTop();
            int colTransitions = 0;
            for (int j = 0;  j < State.COLS;  j++) {
                for (int i = top[j] - 2;  i >= 0;  i--) {
                    if ((field[i][j] == 0) != (field[i + 1][j] == 0)) {
                        colTransitions++;
                    }
                }
                if (field[0][j] == 0 && top[j] > 0) colTransitions++;
            }
            return (double) colTransitions;
        }
        
        public int getBumpiness() {
            int bumpiness = 0;
            for (int i = 0; i < COLS - 1; i++) {
                bumpiness += Math.abs(top[i] - top[i + 1]);
            }
            return bumpiness;
        }

        public int getHighestColumn() {
            int highestCol = 0;
            for (int i = 0;  i < COLS;  i++) {
                highestCol = Math.max(highestCol, top[i]);
            }
            return highestCol;
        }

        public double getWellSum(State s) {
            int[][] field = s.getField();
            int[] top = s.getTop();
            int wellSum = 0;
            for (int j = 0;  j < State.COLS;  j++) {
                for (int i = State.ROWS -1;  i >= 0;  i--) {
                    if (field[i][j] == 0) {
                        if (j == 0 || field[i][j - 1] != 0) {
                            if (j == State.COLS - 1 || field[i][j + 1] != 0) {
                                int wellHeight = i - top[j] + 1;
                                wellSum += wellHeight * (wellHeight + 1) / 2;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
            return wellSum;
        }

        public Helper(State s, int move, double[] gene) {
            this.initialState = new State(s);
            this.newState = new State(s);
            this.move = move;

            this.holeParam = gene[3];
            this.rowTransitionParam = gene[1];
            this.columnTransitionParam = gene[2];
            this.wellParam = gene[4];
            this.clearedParam = gene[0];

            value = Integer.MIN_VALUE;
        }


        public void updateValue(State s) {
            if (s.hasLost()) {
                return;
            }

            top = s.getTop();
            field = s.getField();

            double hole = getNumHoles(s);
            double rowTransition = getRowTransitions(s);
            double columnTransition = getColTransitions(s);
            double well = getWellSum(s);
            double cleared = s.getRowsCleared();

            cleared *= clearedParam;
            hole *= holeParam;
            rowTransition *= rowTransitionParam;
            columnTransition *= columnTransitionParam;
            well *= wellParam;

            value = cleared + hole + rowTransition + columnTransition + well;
        }

        public void run() {
            this.newState.makeMove(this.move);
            updateValue(this.newState);
        }
    }

    public static int getBestMove(State s, int[][] legalMoves, double[] gene) {
        int bestMove = 0;
        double bestResult = Integer.MIN_VALUE;

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

                if (helpers[i].value > bestResult) {
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
