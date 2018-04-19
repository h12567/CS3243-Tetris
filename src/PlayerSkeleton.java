import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.concurrent.CountDownLatch;

public class PlayerSkeleton {

    private double[] gene = {-0.16423861,-0.16423861,0.249497149,-0.57658604,-0.123561369,-0.123561369,-0.234881266,-0.68238335};

    //implement this function to have a working system
    public int pickMove(State s, int[][] legalMoves) {
        return Logic.getBestMove(s, legalMoves, gene);
    }

    public static void main(String[] args) {
        int t = 100;
        if (args.length == 1) {
            t = Integer.parseInt(args[0]);
        }

        State s = new State();
//        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
//            s.draw();
//            s.drawNext(0, 0);
//            try {
//                System.out.println("==========================");
//                Thread.sleep(300);
//                final CountDownLatch latch = new CountDownLatch(1);
//                KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
//                    public boolean dispatchKeyEvent(KeyEvent e) {
//                        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
//                            latch.countDown();
//                        }
//                        return false;
//                    }
//                };
//                KeyboardFocusManager.getCurrentKeyboardFocusManager()
//                        .addKeyEventDispatcher(dispatcher);
//                latch.await();  // current thread waits here until countDown() is called
//                KeyboardFocusManager.getCurrentKeyboardFocusManager()
//                        .removeKeyEventDispatcher(dispatcher);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

}
