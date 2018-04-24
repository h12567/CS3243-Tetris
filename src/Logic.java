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
        private int orient;
        private int slot;
        private int landingHeight;

        private double value;
        public int[] top;
        public int[][] field;

        private double landingBottomParam;
        private double landingTopParam;
        private double linesClearedParam;
        private double holesParam;
        private double outerWellSumParam;
        private double innerWellSumParam;
        private double rowTransitionParam;
        private double colTransitionParam;

        public void setLandingHeight() {
            int height = initialState.getTop()[slot] - State
                    .getpBottom()[initialState.nextPiece][orient][0];
            //for each column beyond the first in the piece
            for (int c = 1; c < State.getpWidth()[initialState.nextPiece][orient]; c++) {
                height = Math
                        .max(height, initialState.getTop()[slot + c] - State
                                .getpBottom()[initialState.nextPiece][orient][c]);
            }
            landingHeight = height;
        }

        public double getLandingBottom() {
            return landingHeight;
        }

        public double getLandingTop() {
            return landingHeight + State.getpHeight()[initialState.nextPiece][orient];
        }

        public double getLinesCleared() {
            return newState.getRowsCleared() - initialState.getRowsCleared();
        }

        public double getNumHoles() {
            int numHoles = 0;
            for (int j = 0; j < State.COLS; j++) {
                if (newState.getTop()[j] != 0) {
                    for (int i = newState.getTop()[j] - 1; i >= 0; i--) {
                        if (newState.getField()[i][j] == 0) {
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
            return (double) numHoles;
        }

        public double getOuterWellSum() {
            int outerWellSum = 0;
            // Count the middle columns
            for (int j = 1; j < State.COLS - 1; j++) {
                for (int i = State.ROWS - 2; i >= newState.getTop()[j]; i--) {
                    if (newState.getField()[i][j] == 0 && newState.getField()[i][j - 1] > 0
                            && newState.getField()[i][j + 1] > 0) {
                        outerWellSum += (i - newState.getTop()[j] + 1);
                    }
                }
            }
            // Count the leftmost column
            for (int i = State.ROWS - 2; i >= newState.getTop()[0]; i--) {
                if (newState.getField()[i][0] == 0 && newState.getField()[i][1] > 0) {
                    outerWellSum += (i - newState.getTop()[0] + 1);
                }
            }
            // Count the rightmost column
            for (int i = State.ROWS - 2; i >= newState.getTop()[State.COLS - 1]; i--) {
                if (newState.getField()[i][State.COLS - 1] == 0
                        && newState.getField()[i][State.COLS - 2] > 0) {
                    outerWellSum += (i - newState.getTop()[State.COLS - 1] + 1);
                }
            }
            return outerWellSum;
        }

        public double getInnerWellSum() {
            int innerWellSum = 0;
            // Count the middle columns
            for (int j = 1; j < State.COLS - 1; j++) {
                for (int i = newState.getTop()[j] - 1; i >= 0; i--) {
                    if (newState.getField()[i][j] == 0 && newState.getField()[i][j - 1] > 0
                            && newState.getField()[i][j + 1] > 0) {
                        innerWellSum += 1;
                        for (int k = i - 1; k >= 0; k--) {
                            if (newState.getField()[k][j] == 0) {
                                innerWellSum += 1;
                            } else {
                                break;
                            }
                        }
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
            // Count the leftmost column
            for (int i = newState.getTop()[0] - 1; i >= 0; i--) {
                if (newState.getField()[i][0] == 0 && newState.getField()[i][1] > 0) {
                    innerWellSum += 1;
                    for (int k = i - 1; k >= 0; k--) {
                        if (newState.getField()[k][0] == 0) {
                            innerWellSum += 1;
                        } else {
                            break;
                        }
                    }
                }
            }
            // Count the rightmost column
            for (int i = newState.getTop()[State.COLS - 1] - 1; i >= 0; i--) {
                if (newState.getField()[i][State.COLS - 1] == 0
                        && newState.getField()[i][State.COLS - 2] > 0) {
                    innerWellSum += 1;
                    for (int k = i - 1; k >= 0; k--) {
                        if (newState.getField()[k][State.COLS - 1] == 0) {
                            innerWellSum += 1;
                        } else {
                            break;
                        }
                    }
                }
            }
            return innerWellSum;
        }

        public double getRowTransitions() {
            int rowTransitions = 0;
            // For each row
            for (int i = 0; i < State.ROWS - 1; i++) {
                // Suppose the cell outside the playfield is always occupied
                int lastCell = 1;
                // For each cell in the row
                for (int j = 0; j < State.COLS; j++) {
                    // If next cell does not have the same state as last cell
                    if ((newState.getField()[i][j] == 0) != (lastCell == 0)) {
                        // Add 1 to row transition
                        rowTransitions++;
                    }
                    lastCell = newState.getField()[i][j];
                }
                // If final cell in the row is empty, add 1 to transition
                if (lastCell == 0) {
                    rowTransitions++;
                }
            }
            return (double) rowTransitions;
        }

        public double getColTransitions() {
            int colTransitions = 0;
            // For each column
            for (int j = 0; j < State.COLS; j++) {
                // Suppose the cell outside the playfield is always occupied
                int lastCell = 1;
                // For each cell in the col
                for (int i = 0; i < State.ROWS - 1; i++) {
                    // If next cell does not have the same state as last cell
                    if ((newState.getField()[i][j] == 0) != (lastCell == 0)) {
                        // Add 1 to col transition
                        colTransitions++;
                    }
                    lastCell = newState.getField()[i][j];
                }
            }
            return (double) colTransitions;
        }

        public Helper(State s, int move, double[] gene, int[][] legalMoves) {
            this.initialState = new State(s);
            this.newState = new State(s);
            this.move = move;
            this.orient = legalMoves[move][State.ORIENT];
            this.slot = legalMoves[move][State.SLOT];

            setLandingHeight();

            this.landingBottomParam = gene[0];
            this.landingTopParam = gene[1];
            this.linesClearedParam = gene[2];
            this.holesParam = gene[3];
            this.outerWellSumParam = gene[4];
            this.innerWellSumParam = gene[5];
            this.rowTransitionParam = gene[6];
            this.colTransitionParam = gene[7];

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
            double landingBottom = getLandingBottom();
            double landingTop = getLandingTop();
            double linesCleared = getLinesCleared();
            double holes = getNumHoles();
            double outerWellSum = getOuterWellSum();
            double innerWellSum = getInnerWellSum();
            double rowTransition = getRowTransitions();
            double colTransition = getColTransitions();

//            System.out.println((landingBottom + landingTop) / 2 + "\t" +
//                    linesCleared + "\t" +
//                    rowTransition + "\t" +
//                    colTransition + "\t" +
//                    holes + "\t" +
//                    (outerWellSum + innerWellSum));

            landingBottom *= landingBottomParam;
            landingTop *= landingTopParam;
            linesCleared *= linesClearedParam;
            holes *= holesParam;
            outerWellSum *= outerWellSumParam;
            innerWellSum *= innerWellSumParam;
            rowTransition *= rowTransitionParam;
            colTransition *= colTransitionParam;

            value = landingBottom + landingTop + linesCleared + holes + outerWellSum + innerWellSum
                    + rowTransition + colTransition;
        }

        public void run() {
            this.newState.makeMove(this.move);
            updateValue(this.newState);
        }
    }

    public static int getBestMove(State s, int[][] legalMoves, double[] gene) {
        int bestMove = 0;
        double bestResult = Integer.MIN_VALUE;

        Thread threads[] = new Thread[legalMoves.length];
        Helper helpers[] = new Helper[legalMoves.length];

        for (int i = 0; i < legalMoves.length; i++) {
            helpers[i] = new Helper(s, i, gene, legalMoves);
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
        for (int i = 0; i < legalMoves.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("THE FUCK?");
            }
        }

        for (int i = 0; i < legalMoves.length; i++) {
            if (helpers[i].value > bestResult) {
                bestResult = helpers[i].value;
                bestMove = i;
            }
        }

        return bestMove;
    }
}
