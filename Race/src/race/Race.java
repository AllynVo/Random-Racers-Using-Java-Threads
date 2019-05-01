/*
 * Allyn Vo
 * Carlos Valdez
 * 
 * 
 */
package race;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author allynvo
 */
public class Race {

    public static final int NUM_PLAYERS = 4;
    
    public static final int RUNNING_THREADS = 4;

    public static char[][] table = new char[5][5];

    public static int COUNT_GLOBAL = 0;

    public static Player[] players;

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                table[i][j] = '-';
            }
        }

        Lock lock = new ReentrantLock();

        int length = NUM_PLAYERS;

        players = new Player[length];

        for (int i = 0; i < length; i++) {
            Player player = new Player("player" + i, lock);
            players[i] = player;
            players[i].IS_ALIVE = true;
            players[i].HAS_CARROT = false;
        }

        //initiate players fields
        players[0].name = "Bunny";
        players[1].name = "Devil";
        players[2].name = "Tweety";
        players[3].name = "Marvin";
        players[0].letter = 'B';
        players[1].letter = 'D';
        players[2].letter = 'T';
        players[3].letter = 'M';
        players[0].HAS_CARROT = false;
        players[1].HAS_CARROT = false;
        players[2].HAS_CARROT = false;
        players[3].HAS_CARROT = false;

        //table setup
        pick_square('B', players[0]);//Bunny
        pick_square('D', players[1]);//Devil
        pick_square('T', players[2]);//Tweety
        pick_square('M', players[3]);//Marvin
        place_carrot();
        place_carrot();
        place_mountain();
        print_table();

        for (int i = 0; i < length - 1; i++) {
            players[i].setNextPlayer(players[i + 1]);
        }
        players[length - 1].setNextPlayer(players[0]);

        System.out.println("Game starting...!");

        players[0].setPlay(true);

        //Threads creation
        Thread[] threads = new Thread[length];
        for (int i = 0; i < length; i++) {
            Thread thread = new Thread(players[i]);
            threads[i] = thread;
            thread.start();
        }

        //Let the players play!
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Tell the players to stop
        //for (Thread thread : threads) {
        //   thread.interrupt();
        //}
        //Don't progress main thread until all players have finished
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void print_table() {
        int i, j;
        System.out.println("-------------------------");
        for (i = 0; i < 5; i++) {
            for (j = 0; j < 5; j++) {
                if (table[i][j] == '-') {
                    System.out.print("-");
                    System.out.print("     ");
                }//printf("   ");
                else if (table[i][j] == 'D' && players[1].HAS_CARROT) {
                    System.out.print("D(C)  ");
                } else if (table[i][j] == 'B' && players[0].HAS_CARROT) {
                    System.out.print("B(C)  ");
                } else if (table[i][j] == 'T' && players[2].HAS_CARROT) {
                    System.out.print("T(C)  ");
                } else if (table[i][j] == 'M' && players[3].HAS_CARROT) {
                    System.out.print("M(C)  ");
                } else {
                    System.out.print(table[i][j]);
                    System.out.print("     ");

                }

            }
            System.out.println();
        }
        System.out.print("-------------------------");
        System.out.println();
    }

    public static void pick_square(char toon, Player player) {
        Random random = new Random();
        int x, y;
        boolean done = false;

        while (!done) {
            x = random.nextInt(5);
            y = random.nextInt(5);

            if (table[y][x] == '-') {
                table[y][x] = toon;
                player.x = x;
                player.y = y;
                done = true;
            }
        }

    }

    public static void place_carrot() {
        Random random = new Random();
        int x, y;
        boolean done = false;

        while (!done) {
            x = random.nextInt(5);
            y = random.nextInt(5);

            if (table[y][x] == '-') {
                table[y][x] = 'C';
                done = true;
            }
        }
    }

    public static void place_mountain() {
        System.out.println("                * * * RANDOMIZING MOUNTAIN * * * ");
        Random random = new Random();
        int x, y;
        boolean done = false;
        

        while (!done) {
            x = random.nextInt(5);
            y = random.nextInt(5);

            if (table[y][x] == '-') {
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if(table[i][j] == 'F'){
                            table[i][j] = '-';
                        }
                    }
                }
                table[y][x] = 'F';
                done = true;

            }
        }
    }

    public static void free_old_spot(Player player) {
        int old_x, old_y;
        old_x = player.x;
        old_y = player.y;
        table[old_y][old_x] = '-';
    }

    public static void walking_tooneys(Player toon) {
        Random random = new Random();

        boolean done = false;

        int tries = 0;

        while (!done) {
            tries++;
            int move = random.nextInt(4);
            switch (move) {
                //move up 1
                case 0:
                    if (toon.y - 1 < 0) {
                        break;
                    }
                    if (table[toon.y - 1][toon.x] == 'F'
                            && toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y - 1);
                        table[toon.y][toon.x] = toon.letter;
                        System.out.println(toon.name + " won the race");
                        print_table();
                        
                        System.exit(0);
                    } else if (table[toon.y - 1][toon.x] == '-') {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y - 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        break;
                    } else if (table[toon.y - 1][toon.x] == 'C' && !toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y - 1);
                        table[toon.y][toon.x] = toon.letter;
                        toon.HAS_CARROT = true;
                        done = true;
                        break;
                    } else {
                        break;
                    }

                //move right 1
                case 1:
                    if (toon.x + 1 > 4) {
                        break;
                    }
                    if (table[toon.y][toon.x + 1] == 'F'
                            && toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x + 1);
                        table[toon.y][toon.x] = toon.letter;
                        System.out.println(toon.name + " won the race");
                        print_table();
                        System.exit(0);
                    } else if (table[toon.y][toon.x + 1] == '-') {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x + 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        break;
                    } else if (table[toon.y][toon.x + 1] == 'C' && !toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x + 1);
                        table[toon.y][toon.x] = toon.letter;
                        toon.HAS_CARROT = true;
                        done = true;
                        break;
                    } else {
                        break;
                    }

                //move down 1    
                case 2:
                    if (toon.y + 1 > 4) {
                        break;
                    }
                    if (table[toon.y + 1][toon.x] == 'F'
                            && toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y + 1);
                        table[toon.y][toon.x] = toon.letter;
                        System.out.println(toon.name + " won the race");
                        print_table();
                        System.exit(0);
                    } else if (table[toon.y + 1][toon.x] == '-') {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y + 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        break;
                    } else if (table[toon.y + 1][toon.x] == 'C' && !toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y + 1);
                        table[toon.y][toon.x] = toon.letter;
                        toon.HAS_CARROT = true;
                        done = true;
                        break;
                    } else {
                        break;
                    }

                //move left 1    
                case 3:
                    if (toon.x - 1 < 0) {
                        break;
                    }
                    if (table[toon.y][toon.x - 1] == 'F'
                            && toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x - 1);
                        table[toon.y][toon.x] = toon.letter;
                        System.out.println(toon.name + " won the race");
                        print_table();
                        System.exit(0);
                    } else if (table[toon.y][toon.x - 1] == '-') {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x - 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        break;
                    } else if (table[toon.y][toon.x - 1] == 'C' && !toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x - 1);
                        table[toon.y][toon.x] = toon.letter;
                        toon.HAS_CARROT = true;
                        done = true;
                        break;
                    } else {
                        break;
                    }

                default:
                    break;

            }//switch
            if (tries > 5) {
                break;
            }
        }
        print_table();
    }

    public static void walking_marvin(Player toon) {
        Random random = new Random();

        boolean done = false;

        int tries = 0;

        while (!done) {
            tries++;
            int move = random.nextInt(4);
            switch (move) {
                //move up 1
                case 0:
                    if (toon.y - 1 < 0) {
                        break;
                    }
                    if (table[toon.y - 1][toon.x] == 'F'
                            && toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y - 1);
                        table[toon.y][toon.x] = toon.letter;
                        System.out.println(toon.name + " won the race");
                        print_table();
                        System.exit(0);
                    } else if (table[toon.y - 1][toon.x] == '-') {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y - 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        break;
                    } else if (table[toon.y - 1][toon.x] == 'C' && !toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y - 1);
                        table[toon.y][toon.x] = toon.letter;
                        toon.HAS_CARROT = true;
                        done = true;
                        break;
                    } else if (table[toon.y - 1][toon.x] == 'F' && !toon.HAS_CARROT) {
                        break;
                    } else {
                        if(table[toon.y - 1][toon.x] == 'B'){
                            System.out.println("Marvin killed Bunny!");
                            if(players[0].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[0].IS_ALIVE = false;
                        }else if (table[toon.y - 1][toon.x] == 'D'){
                            System.out.println("Marvin killed Devil!");
                            if(players[1].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[1].IS_ALIVE = false;
                        }else if (table[toon.y - 1][toon.x] == 'T'){
                            System.out.println("Marvin killed Tweety!");
                            if(players[2].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[2].IS_ALIVE = false;
                        }
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y - 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        
                        break;
                    }

                //move right 1
                case 1:
                    if (toon.x + 1 > 4) {
                        break;
                    }
                    if (table[toon.y][toon.x + 1] == 'F'
                            && toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x + 1);
                        table[toon.y][toon.x] = toon.letter;
                        System.out.println(toon.name + " won the race");
                        print_table();
                        System.exit(0);
                    } else if (table[toon.y][toon.x + 1] == '-') {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x + 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        break;
                    } else if (table[toon.y][toon.x + 1] == 'C' && !toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x + 1);
                        table[toon.y][toon.x] = toon.letter;
                        toon.HAS_CARROT = true;
                        done = true;
                        break;
                    } else if (table[toon.y][toon.x + 1] == 'F' && !toon.HAS_CARROT) {
                        break;
                    } else {
                        if(table[toon.y][toon.x + 1] == 'B'){
                            players[0].IS_ALIVE = false;
                            System.out.println("Marvin killed Bunny!");
                            if(players[0].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                        }else if (table[toon.y][toon.x + 1] == 'D'){
                            System.out.println("Marvin killed Devil!");
                            if(players[1].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[1].IS_ALIVE = false;
                        }else if (table[toon.y][toon.x + 1] == 'T'){
                            System.out.println("Marvin killed Tweety!");
                            if(players[2].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[2].IS_ALIVE = false;
                        }
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x + 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;

                        break;
                    }

                //move down 1    
                case 2:
                    if (toon.y + 1 > 4) {
                        break;
                    }
                    if (table[toon.y + 1][toon.x] == 'F'
                            && toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y + 1);
                        table[toon.y][toon.x] = toon.letter;
                        System.out.println(toon.name + " won the race");
                        print_table();
                        System.exit(0);
                    } else if (table[toon.y + 1][toon.x] == '-') {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y + 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        break;
                    } else if (table[toon.y + 1][toon.x] == 'C' && !toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y + 1);
                        table[toon.y][toon.x] = toon.letter;
                        toon.HAS_CARROT = true;
                        done = true;
                        break;
                    } else if (table[toon.y + 1][toon.x] == 'F' && !toon.HAS_CARROT) {
                        break;
                    } else {
                        if(table[toon.y + 1][toon.x] == 'B'){
                            System.out.println("Marvin killed Bunny!");
                            if(players[0].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[0].IS_ALIVE = false;
                        }else if (table[toon.y + 1][toon.x] == 'D'){
                            System.out.println("Marvin killed Devil!");
                            if(players[1].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[1].IS_ALIVE = false;
                        }else if (table[toon.y + 1][toon.x] == 'T'){
                            System.out.println("Marvin killed Tweety!");
                            if(players[2].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[2].IS_ALIVE = false;
                        }
                        table[toon.y][toon.x] = '-';
                        toon.y = (toon.y + 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;

                        break;
                    }

                //move left 1    
                case 3:
                    if (toon.x - 1 < 0) {
                        break;
                    }
                    if (table[toon.y][toon.x - 1] == 'F'
                            && toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x - 1);
                        table[toon.y][toon.x] = toon.letter;
                        System.out.println(toon.name + " won the race");
                        print_table();
                        System.exit(0);
                    } else if (table[toon.y][toon.x - 1] == '-') {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x - 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;
                        break;
                    } else if (table[toon.y][toon.x - 1] == 'C' && !toon.HAS_CARROT) {
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x - 1);
                        table[toon.y][toon.x] = toon.letter;
                        toon.HAS_CARROT = true;
                        done = true;
                        break;
                    } else if (table[toon.y][toon.x - 1] == 'F' && !toon.HAS_CARROT) {
                        break;
                    } else {
                        if(table[toon.y][toon.x - 1] == 'B'){
                            System.out.println("Marvin killed Bunny!");
                            if(players[0].HAS_CARROT){
                                toon.HAS_CARROT = true;
                                System.out.println("And took a carrot.");
                            }
                            players[0].IS_ALIVE = false;
                        }else if (table[toon.y][toon.x - 1] == 'D'){
                            System.out.println("Marvin killed Devil!");
                            if(players[1].HAS_CARROT){
                                toon.HAS_CARROT = true;
                            }
                            players[1].IS_ALIVE = false;
                        }else if (table[toon.y][toon.x - 1] == 'T'){
                            System.out.println("Marvin killed Tweety!");
                            if(players[2].HAS_CARROT){
                                toon.HAS_CARROT = true;
                            }
                            players[2].IS_ALIVE = false;
                        }
                        table[toon.y][toon.x] = '-';
                        toon.x = (toon.x - 1);
                        table[toon.y][toon.x] = toon.letter;
                        done = true;

                        break;
                    }

                default:
                    break;

            }//switch
            if (tries > 5) {
                break;
            }
        }
        print_table();
    }

}
