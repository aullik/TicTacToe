package de.htwg.tictactoe.model;

/**
 * 
 * @author Youssef Idelhoussain
 */
public interface IGrid {

	/**
	 * try to set Cell and if it is already set return false, if not set the Cell and return true.
	 * @param row
	 * @param column
	 * @param symbol
	 * @return
	 */
	public boolean setCell(int row, int column, String symbol);

	/**
	 * get Cell at the given row, column.
	 * @param row
	 * @param column
	 * @return
	 */
	public ICell getCell(int row, int column);
}
