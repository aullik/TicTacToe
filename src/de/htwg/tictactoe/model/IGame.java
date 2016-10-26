package de.htwg.tictactoe.model;

/**
 * Created by Daniel on 17.06.2016.
 * 
 * modified by Youssef 21.06.2016
 */
public interface IGame {

    /**
     * Sets all values ​​of the current game state to the initial state.
     */
    public void resetGame();

    /**
     * Checks the value in one cell of tree possible cells,
     * whether the value is set.
     *
     * @param row    the row of the current move
     * @param column the column of the current move
     * @param grid   the grid with the row and the column
     *               of the current move
     *
     * @return       if the cell is set, the method will
     *               return true, otherwise false.
     */
    public boolean cellIsSet(int row, int column, int grid);

    /**
     * Gives the grids of the current game state with all
     * current values of the grids.
     *
     * @return       a array with all grids of the current game
     */
    public IGrid[] getGrids();
    

	/**
	 * gets the player at the given index.
	 * @param player
	 * @return IPlayer object
	 */
	public IPlayer getPlayer(int player);

	/**
	 * create a new player object.
	 * @param i
	 * @param player
	 * @param symbol
	 * @param grids
	 */
	public void setPlayer(int i, String player, String symbol, IGrid[] grids);

}
