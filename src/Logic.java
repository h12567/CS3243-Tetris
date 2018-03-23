import java.util.concurrent.atomic.AtomicInteger;

public class Logic {
    
    static class Helper implements Runnable {
        private State initialState;
        private State newState;
        private int move;

        private double value;

        private double blockageParam;
        private double heightParam;
        private double bumpinessParam;
        private double clearedParam;

        public Helper(State s, int move, double blockageParam, double heightParam,
                    double bumpinessParam, double clearedParam) {
            this.initialState = new State(s);
            this.newState = new State(s);
            this.move = move;

            this.blockageParam = blockageParam;
            this.heightParam = heightParam;
            this.bumpinessParam = bumpinessParam;
            this.clearedParam = clearedParam;

            value = Integer.MAX_VALUE;
        }

        public void updateValue(State s) {
            if(s.hasLost()) return;

            double blockage = 0;
            double height = 0;
            double bumpiness = 0;
            double cleared = 0;

            for(int i = 0 ; i < s.ROWS ; i ++) {
                for(int j = 0 ; j < s.COLS ; j ++) {
                    if(i >= s.getTop()[j]) continue;
                    if(s.getField()[i][j] == 0) {
                        blockage += s.getTop()[j] - i;
                    }
                }
            }

            for(int i = 0 ; i < s.COLS ; i ++)
                height += s.getTop()[i];


            for(int i = 1 ; i < s.COLS ; i ++)
                bumpiness += Math.abs(s.getTop()[i] - s.getTop()[i - 1]);

            cleared = s.getRowsCleared() - initialState.getRowsCleared();

            blockage *= blockageParam;
            height *= heightParam;
            bumpiness *= bumpinessParam;
            cleared *= clearedParam;

            value = blockage + height + bumpiness + cleared;
        }

        public void run() {
            this.newState.makeMove(this.move);
            updateValue(this.newState);
        }
    }

    public static int getBestMove(State s, int[][] legalMoves, double blockageParam, 
                double heightParam, double bumpinessParam, double clearedParam) {
        int bestMove = 0;
        double bestResult = Integer.MAX_VALUE;

        Helper helpers[] = new Helper[legalMoves.length];
        Thread threads[] = new Thread[legalMoves.length];

        for(int i = 0 ; i < legalMoves.length ; i ++) {
            helpers[i] = new Helper(s, i, blockageParam, heightParam, 
                bumpinessParam, clearedParam);
            threads[i] = new Thread(helpers[i]);
            threads[i].run();
        }
        

        try {
            for(int i = 0 ; i < legalMoves.length ; i ++) {
                threads[0].join();

                if(helpers[i].value < bestResult) {
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
