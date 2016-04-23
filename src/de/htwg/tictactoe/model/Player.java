package de.htwg.tictactoe.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 21.04.2016.
 */
public class Player {

    private final String name;
    private final String symbole;
    private List<int[]> checkforWinStatus;

    public Player(String name, String symbole){
        this.name = name;
        this.symbole = symbole;
        this.checkforWinStatus = new ArrayList<int[]>();
        this.checkforWinStatus.add( 0, new int[3]);
        this.checkforWinStatus.add( 1, new int[3]);
        this.checkforWinStatus.add( 2, new int[2]);
        this.checkforWinStatus.add( 3, new int[2]);
    }

    /**
     * get the name of the player
     * @return the name of the player as String
     */
    public String getName(){
        return this.name;
    }

    /**
     * get the name of the player
     * @return a Cell object or null
     */
    public String getSymbole(){
        return this.symbole;
    }

    /**
     * get the status of winning
     * @return a List with int Arrays
     */
    public List<int[]> getCheckforWinStatus(){
        return this.checkforWinStatus;
    }

    /**
     * increment a element of the winning status
     */
    public void incrementCheckforWinStatus(int indexOfTheList, int indexOfTheIntArray){
        this.checkforWinStatus.get(indexOfTheList)[indexOfTheIntArray] = checkforWinStatus.
                                                                         get(indexOfTheList)[indexOfTheIntArray] + 1;
    }
}
