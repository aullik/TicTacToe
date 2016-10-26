package de.htwg.tictactoe.model;

/**
 * Created by Daniel on 17.06.2016.
 * 
 * Modified by Youssef 21.06.2016
 */
public interface IPlayer {

    /**
     * Gives the name of the player
     *
     * @return  a string with the name of the player
     */
    String getName();
    

	/**
	 * the player plays, sets his symbol in the given row, column at the given Grid place
	 * @param row
	 * @param column
	 * @param grid
	 * @return
	 */
	public boolean setSymbol(int row, int column, int grid);
}
