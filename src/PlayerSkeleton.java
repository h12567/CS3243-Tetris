import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.concurrent.CountDownLatch;

public class PlayerSkeleton {

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

    private double landingHeightParam = -4.500158825082766;
    private double rowsClearedParam = -3.4181268101392694;
    private double rowTransitionParam = -3.2178882868487753;
    private double colTransitionParam = -9.348695305445199;
    private double holesParam = -7.899265427351652;
    private double wellSumParam = -3.3855972247263626;

    // Evaluate the heuristic
    public double evaluate(State s, int orient, int slot) {
        StateClone next = new StateClone(s);
        next.makeMove(orient, slot);
        if (next.lost) {
            return -Double.MAX_VALUE;
        }

        double landingHeight = getLandingHeight(s, next, orient, slot);
        double rowsCleared = getRowsCleared(s, next, orient, slot);
        double rowTransition = getRowTransition(s, next, orient, slot);
        double colTransition = getColTransition(s, next, orient, slot);
        double holes = getHoles(s, next, orient, slot);
        double wellSum = getWellSum(s, next, orient, slot);

//        System.out.println(
//                landingHeight + "\t\t" + rowsCleared + "\t\t" + rowTransition + "\t\t"
//                        + colTransition
//                        + "\t\t" + holes + "\t\t" + wellSum);
//        System.out.println(
//                landingHeight * 10000000000.0 + rowsCleared * 100000000 + rowTransition * 1000000
//                        + colTransition * 10000 + holes * 100 + wellSum);

        return landingHeight * landingHeightParam +
                rowsCleared * rowsClearedParam +
                rowTransition * rowTransitionParam +
                colTransition * colTransitionParam +
                holes * holesParam +
                wellSum * wellSumParam;
    }

    public double getLandingHeight(State s, StateClone next, int orient, int slot) {
        int height = s.getTop()[slot] - State.getpBottom()[s.nextPiece][orient][0];
        //for each column beyond the first in the piece
        for (int c = 1; c < State.getpWidth()[s.nextPiece][orient]; c++) {
            height = Math
                    .max(height, s.getTop()[slot + c] - State.getpBottom()[s.nextPiece][orient][c]);
        }
        return (double) height + (double) (State.getpHeight()[s.nextPiece][orient] - 1) / 2;
    }

    public double getRowsCleared(State s, StateClone next, int orient, int slot) {
        return (double) next.cleared - s.getRowsCleared();
    }

    public double getRowTransition(State s, StateClone next, int orient, int slot) {
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

    public double getColTransition(State s, StateClone next, int orient, int slot) {
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

    public double getWellSum(State s, StateClone next, int orient, int slot) {
        int wellSum = 0;
        // Count the middle columns
        for (int j = 1; j < State.COLS - 1; j++) {
            for (int i = State.ROWS - 2; i >= 0; i--) {
                if (next.field[i][j] == 0 && next.field[i][j - 1] > 0 && next.field[i][j + 1] > 0) {
                    wellSum += 1;
                    for (int k = i - 1; k >= 0; k--) {
                        if (next.field[k][j] == 0) {
                            wellSum += 1;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        // Count the leftmost column
        for (int i = State.ROWS - 2; i >= 0; i--) {
            if (next.field[i][0] == 0 && next.field[i][1] > 0) {
                wellSum += 1;
                for (int k = i - 1; k >= 0; k--) {
                    if (next.field[k][0] != 0) {
                        wellSum += 1;
                    } else {
                        break;
                    }
                }
            }
        }
        // Count the rightmost column
        for (int i = State.ROWS - 2; i >= 0; i--) {
            if (next.field[i][State.COLS - 1] == 0 && next.field[i][State.COLS - 2] > 0) {
                wellSum += 1;
                for (int k = i - 1; k >= 0; k--) {
                    if (next.field[k][State.COLS - 1] == 0) {
                        wellSum += 1;
                    } else {
                        break;
                    }
                }
            }
        }
        return wellSum;
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
        State s = new State();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            s.draw();
            s.drawNext(0, 0);
            try {
                Thread.sleep(300);
                final CountDownLatch latch = new CountDownLatch(1);
                KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                            latch.countDown();
                        }
                        return false;
                    }
                };
                KeyboardFocusManager.getCurrentKeyboardFocusManager()
                        .addKeyEventDispatcher(dispatcher);
                latch.await();  // current thread waits here until countDown() is called
                KeyboardFocusManager.getCurrentKeyboardFocusManager()
                        .removeKeyEventDispatcher(dispatcher);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}
