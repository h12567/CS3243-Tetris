import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class PlayerSkeleton {

    /* Around 2M one */
//    private double[] gene = {-0.11053540341073291,-0.36883615584872065,0.1804241717873332,-0.625281961800296,-0.18321114527215862,-0.3366977548511917,-0.20063655121632362,-0.4909426378466667};

    /* At least 9M one */
//    private double[] gene = {-0.16323974806803318,-0.21318127671949377,0.14111723105868468,-0.574190533892977,-0.27427659655036474,-0.20417815295663141,-0.17740726106192445,-0.6556736840497965};

    /* Might be even higher one */
    private double[] gene = {-0.1706414053296784,-0.22883826801801754,0.2675572656690145,-0.46109713967090993,-0.24485952196208405,-0.1419941368875221,-0.20668067834516804,-0.7151794684171942};

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
            if (s.getRowsCleared() % 100000 == 0) {
                System.out.println("Cleared " + s.getRowsCleared() + " lines");
            }
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
