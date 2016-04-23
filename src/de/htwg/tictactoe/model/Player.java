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


    public Player(String name, String symbole){
        this.name = name;
        this.symbole = symbole;
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

}
