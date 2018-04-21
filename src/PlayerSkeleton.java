public class PlayerSkeleton {

    private double landingBottomParam = -0.16323974806803318;
    private double landingTopParam = -0.21318127671949377;
    private double linesClearedParam = 0.14111723105868468;
    private double holesParam = -0.574190533892977;
    private double outerWellSumParam = -0.27427659655036474;
    private double innerWellSumParam = -0.20417815295663141;
    private double rowTransitionParam = -0.17740726106192445;
    private double colTransitionParam = -0.6556736840497965;

    // Clone a state to simulate moves
    class StateClone extends State {

        private int turn;
        private int cleared;

        private int[][] field = new int[ROWS][COLS];
        private int[] top = new int[COLS];

        StateClone(State s) {
            this.lost = s.hasLost();
            this.turn = s.getTurnNumber();
            this.cleared = s.getRowsCleared();

            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    this.field[i][j] = s.getField()[i][j];
                }
            }

            for (int i = 0; i < COLS; i++) {
                this.top[i] = s.getTop()[i];
            }

            this.nextPiece = s.getNextPiece();
        }

        @Override
        public boolean makeMove(int orient, int slot) {
            turn++;
            //height if the first column makes contact
            int height = top[slot] - State.getpBottom()[nextPiece][orient][0];
            //for each column beyond the first in the piece
            for (int c = 1; c < pWidth[nextPiece][orient]; c++) {
                height = Math.max(height, top[slot + c] - State.getpBottom()[nextPiece][orient][c]);
            }

            //check if game ended
            if (height + State.getpHeight()[nextPiece][orient] >= ROWS) {
                lost = true;
                return false;
            }

            //for each column in the piece - fill in the appropriate blocks
            for (int i = 0; i < pWidth[nextPiece][orient]; i++) {

                //from bottom to top of brick
                for (int h = height + State.getpBottom()[nextPiece][orient][i];
                        h < height + State.getpTop()[nextPiece][orient][i]; h++) {
                    field[h][i + slot] = turn;
                }
            }

            //adjust top
            for (int c = 0; c < pWidth[nextPiece][orient]; c++) {
                top[slot + c] = height + State.getpTop()[nextPiece][orient][c];
            }

            int rowsCleared = 0;

            //check for full rows - starting at the top
            for (int r = height + State.getpHeight()[nextPiece][orient] - 1; r >= height; r--) {
                //check all columns in the row
                boolean full = true;
                for (int c = 0; c < COLS; c++) {
                    if (field[r][c] == 0) {
                        full = false;
                        break;
                    }
                }
                //if the row was full - remove it and slide above stuff down
                if (full) {
                    rowsCleared++;
                    cleared++;
                    //for each column
                    for (int c = 0; c < COLS; c++) {

                        //slide down all bricks
                        for (int i = r; i < top[c]; i++) {
                            field[i][c] = field[i + 1][c];
                        }
                        //lower the top
                        top[c]--;
                        while (top[c] >= 1 && field[top[c] - 1][c] == 0) {
                            top[c]--;
                        }
                    }
                }
            }

            return true;
        }
    }

    // Evaluate the heuristic
    public double evaluate(State s, int orient, int slot) {
        StateClone next = new StateClone(s);
        next.makeMove(orient, slot);
        if (next.lost) {
            return -Double.MAX_VALUE;
        }

        double landingBottom = getLandingBottom(s, next, orient, slot);
        double landingTop = landingBottom + (double) State.getpHeight()[s.nextPiece][orient];
        double linesCleared = getRowsCleared(s, next, orient, slot);
        double holes = getHoles(s, next, orient, slot);
        double outerWellSum = getOuterWellSum(s, next, orient, slot);
        double innerWellSum = getInnerWellSum(s, next, orient, slot);
        double rowTransition = getRowTransitions(s, next, orient, slot);
        double colTransition = getColTransitions(s, next, orient, slot);

        return landingBottom * landingBottomParam +
                landingTop * landingTopParam +
                linesCleared * linesClearedParam +
                holes * holesParam +
                outerWellSum * outerWellSumParam +
                innerWellSum * innerWellSumParam +
                rowTransition * rowTransitionParam +
                colTransition * colTransitionParam;
    }

    public double getLandingBottom(State s, StateClone next, int orient, int slot) {
        int height = s.getTop()[slot] - State.getpBottom()[s.nextPiece][orient][0];
        //for each column beyond the first in the piece
        for (int c = 1; c < State.getpWidth()[s.nextPiece][orient]; c++) {
            height = Math
                    .max(height, s.getTop()[slot + c] - State.getpBottom()[s.nextPiece][orient][c]);
        }
        return (double) height;
    }

    public double getRowsCleared(State s, StateClone next, int orient, int slot) {
        return (double) next.cleared - s.getRowsCleared();
    }

    public double getRowTransitions(State s, StateClone next, int orient, int slot) {
        int rowTransitions = 0;
        // For each row
        for (int i = 0; i < State.ROWS - 1; i++) {
            // Suppose the cell outside the playfield is always occupied
            int lastCell = 1;
            // For each cell in the row
            for (int j = 0; j < State.COLS; j++) {
                // If next cell does not have the same state as last cell
                if ((next.field[i][j] == 0) != (lastCell == 0)) {
                    // Add 1 to row transition
                    rowTransitions++;
                }
                lastCell = next.field[i][j];
            }
            // If final cell in the row is empty, add 1 to transition
            if (lastCell == 0) {
                rowTransitions++;
            }
        }
        return (double) rowTransitions;
    }

    public double getColTransitions(State s, StateClone next, int orient, int slot) {
        int colTransitions = 0;
        // For each column
        for (int j = 0; j < State.COLS; j++) {
            // Suppose the cell outside the playfield is always occupied
            int lastCell = 1;
            // For each cell in the col
            for (int i = 0; i < State.ROWS - 1; i++) {
                // If next cell does not have the same state as last cell
                if ((next.field[i][j] == 0) != (lastCell == 0)) {
                    // Add 1 to col transition
                    colTransitions++;
                }
                lastCell = next.field[i][j];
            }
        }
        return (double) colTransitions;
    }

    public double getHoles(State s, StateClone next, int orient, int slot) {
        int numHoles = 0;
        for (int j = 0; j < State.COLS; j++) {
            for (int i = next.top[j] - 1; i >= 0; i--) {
                if (next.field[i][j] == 0) {
                    numHoles++;
                }
            }
        }
        return (double) numHoles;
    }

    public double getOuterWellSum(State s, StateClone next, int orient, int slot) {
        int outerWellSum = 0;
        // Count the middle columns
        for (int j = 1; j < State.COLS - 1; j++) {
            for (int i = State.ROWS - 2; i >= next.top[j]; i--) {
                if (next.field[i][j] == 0 && next.field[i][j - 1] > 0
                        && next.field[i][j + 1] > 0) {
                    outerWellSum += (i - next.top[j] + 1);
                }
            }
        }
        // Count the leftmost column
        for (int i = State.ROWS - 2; i >= next.top[0]; i--) {
            if (next.field[i][0] == 0 && next.field[i][1] > 0) {
                outerWellSum += (i - next.top[0] + 1);
            }
        }
        // Count the rightmost column
        for (int i = State.ROWS - 2; i >= next.top[State.COLS - 1]; i--) {
            if (next.field[i][State.COLS - 1] == 0
                    && next.field[i][State.COLS - 2] > 0) {
                outerWellSum += (i - next.top[State.COLS - 1] + 1);
            }
        }
        return outerWellSum;
    }

    public double getInnerWellSum(State s, StateClone next, int orient, int slot) {
        int innerWellSum = 0;
        // Count the middle columns
        for (int j = 1; j < State.COLS - 1; j++) {
            for (int i = next.top[j] - 1; i >= 0; i--) {
                if (next.field[i][j] == 0 && next.field[i][j - 1] > 0
                        && next.field[i][j + 1] > 0) {
                    innerWellSum += 1;
                    for (int k = i - 1; k >= 0; k--) {
                        if (next.field[k][j] == 0) {
                            innerWellSum += 1;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        // Count the leftmost column
        for (int i = next.top[0] - 1; i >= 0; i--) {
            if (next.field[i][0] == 0 && next.field[i][1] > 0) {
                innerWellSum += 1;
                for (int k = i - 1; k >= 0; k--) {
                    if (next.field[k][0] == 0) {
                        innerWellSum += 1;
                    } else {
                        break;
                    }
                }
            }
        }
        // Count the rightmost column
        for (int i = next.top[State.COLS - 1] - 1; i >= 0; i--) {
            if (next.field[i][State.COLS - 1] == 0
                    && next.field[i][State.COLS - 2] > 0) {
                innerWellSum += 1;
                for (int k = i - 1; k >= 0; k--) {
                    if (next.field[k][State.COLS - 1] == 0) {
                        innerWellSum += 1;
                    } else {
                        break;
                    }
                }
            }
        }
        return innerWellSum;
    }


    // legalMoves[a][b], a is the index of the legal move
    // b = ORIENT is the orient, b = SLOT is the slot
    public int pickMove(State s, int[][] legalMoves) {
        double bestHeuristic = -Double.MAX_VALUE;
        int bestMove = 0;

        for (int i = 0; i < legalMoves.length; i++) {
            double heuristic = evaluate(s, legalMoves[i][State.ORIENT], legalMoves[i][State.SLOT]);
            if (bestHeuristic < heuristic) {
                bestHeuristic = heuristic;
                bestMove = i;
            }
        }

        return bestMove;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            State s = new State();
//        new TFrame(s);
            PlayerSkeleton p = new PlayerSkeleton();
            while (!s.hasLost()) {
                if (s.getRowsCleared() % 100000 == 0) {
                    System.out.println("Cleared " + s.getRowsCleared());
                }
                s.makeMove(p.pickMove(s, s.legalMoves()));
//            s.draw();
//            s.drawNext(0, 0);
//            try {
//                Thread.sleep(t);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            }
            System.out.println("You have completed " + s.getRowsCleared() + " rows.");
        }
    }
}
