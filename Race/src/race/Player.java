/*
 * Allyn Vo
 * Carlos Valdez
 * 
 * 
 */
package race;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Player implements Runnable {

    private final String text;

    private final Lock lock;
    private final Condition myTurn;
    private Condition nextTurn;

    private Player nextPlayer;

    private volatile boolean play = false;

    //other fields
    int x, y;

    int id;
    int condition;
    int CARROT;
    int copy_COUNT_GLOBAL;
    int copy_FINISH_LINE;
    String name;
    char letter;

    //boolean FINISH_LINE;
    //String WINNER;
    boolean IS_ALIVE;
    boolean HAS_CARROT;

    public Player(String text,
            Lock lock) {
        this.text = text;
        this.lock = lock;
        this.myTurn = lock.newCondition();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            lock.lock();

            try {
                while (!play) {
                    myTurn.awaitUninterruptibly();
                }

                if (this.IS_ALIVE) {

                    System.out.println("Hi from " + name + " thread.");
                    Race.COUNT_GLOBAL++;
                    if (Race.COUNT_GLOBAL == 12) {
                        Race.COUNT_GLOBAL = 0;
                        Race.place_mountain();
                    }
                    if (this.letter == 'M') {
                        Race.walking_marvin(this);
                    } else {
                        Race.walking_tooneys(this);
                    }
                    Thread.sleep(100);
                }

                    

                    this.play = false;
                    nextPlayer.play = true;

                    nextTurn.signal();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                lock.unlock();
            }
        }
    }

    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
        this.nextTurn = nextPlayer.myTurn;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }
}
