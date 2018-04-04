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
        private double towerParam;
        private double holeParam;

        public Helper(State s, int move, double[] gene) {
            this.initialState = new State(s);
            this.newState = new State(s);
            this.move = move;

            this.blockageParam = gene[0];
            this.heightParam = gene[1];
            this.bumpinessParam = gene[2];
            this.clearedParam = gene[3];
            this.towerParam = gene[4];
            this.holeParam = gene[5];

            value = Integer.MAX_VALUE;
        }

        public void updateValue(State s) {
            if (s.hasLost()) {
                return;
            }

            double blockage = 0;
            double height = 0;
            double bumpiness = 0;
            double cleared = 0;
            double tower = 0;
            double hole = 0;

            for (int i = 0; i < s.ROWS; i++) {
                for (int j = 0; j < s.COLS; j++) {
                    if (i >= s.getTop()[j]) {
                        continue;
                    }
                    if (s.getField()[i][j] == 0) {
                        blockage += s.getTop()[j] - i;
                        hole += 1;
                    }
                }
            }

            for (int i = 0; i < s.COLS; i++) {
                height += s.getTop()[i];
            }

            for (int i = 1; i < s.COLS; i++) {
                int dist = Math.abs(s.getTop()[i] - s.getTop()[i - 1]);
                bumpiness += dist;
                if (dist >= 5) {
                    tower += dist;
                }
            }

            cleared = s.getRowsCleared() - initialState.getRowsCleared();

            blockage *= blockageParam;
            height *= heightParam;
            bumpiness *= bumpinessParam;
            cleared *= clearedParam;
            tower *= towerParam;
            hole *= holeParam;

            value = blockage + height + bumpiness + cleared + tower + hole;
        }

        public void run() {
            this.newState.makeMove(this.move);
            updateValue(this.newState);
        }
    }

    public static int getBestMove(State s, int[][] legalMoves, double[] gene) {
        int bestMove = 0;
        double bestResult = Integer.MAX_VALUE;

        Helper helpers[] = new Helper[legalMoves.length];
        Thread threads[] = new Thread[legalMoves.length];

        for (int i = 0; i < legalMoves.length; i++) {
            helpers[i] = new Helper(s, i, gene);
            threads[i] = new Thread(helpers[i]);
            threads[i].run();
        }

        try {
            for (int i = 0; i < legalMoves.length; i++) {
                threads[0].join();

                if (helpers[i].value < bestResult) {
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
