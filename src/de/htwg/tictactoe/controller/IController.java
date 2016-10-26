package de.htwg.tictactoe.controller;

import de.htwg.tictactoe.model.IGame;
import de.htwg.util.observer.IObservable;

/**
 * Created by Daniel on 18.06.2016.
 * 
 * Modified by Youssef on 21.06.2016.
 */
public interface IController extends IObservable{

    /**
     * Resets the game and notify all observers
     * Sets all values ​​of the current game state to the
     * initial state.
     *
     * Note: this function notify all observers.
     */
    void reset();

    /**
     * Sets the players names of the current game session.
     *
     * @param player1 the name of the first player
     * @param player2 the name of the second player
     */
    void setPlayers(String player1, String player2);

    /**
     * Sets the move of a player and updates the grid of
     * the game. After this update, the function checks
     * if the player won the game.
     *
     * @param row    the row of the current move
     * @param column the column of the current move
     * @param grid   the grid with the row and the column
     *               of the current move
     */
    void setValue(int row, int column, int grid);

    /**
     * Gives all information about the playing field for the
     * user interface.
     *
     * @return a String with the playing field.
     */
    String getGameString();

    /**
     * Gives the current state of the game with all necessary
     * information about the players and their moves.
     * The extended scope of functions includes, for instance,
     * the welcome messages and information about the first
     * steps.
     *
     * Note: does not return any information about the playing
     *       field.
     *
     * @return a string with all information of the current
     * game state.
     */
    String getStatus();

	/**
	 * exit
	 */
	public void exit();

	/**
	 * return true if player[0] has won, false he lost.
	 * @param i
	 * @return boolean
	 */
	public boolean getWin(int i);
	
	/**
	 * get a IGame object.
	 * @return
	 */
	public IGame getGame();
}
