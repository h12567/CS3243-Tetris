public class Logic {

    static class Helper {

        private State initialState;
        private State newState;
        private int move;
        private int orient;
        private int slot;
        private int landingHeight;

        private double value;

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
            }
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

        Helper helpers[] = new Helper[legalMoves.length];

        for (int i = 0; i < legalMoves.length; i++) {
            helpers[i] = new Helper(s, i, gene, legalMoves);
            helpers[i].run();
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
