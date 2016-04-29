package de.htwg.tictactoe.model;

/**
 * Created by Youssef on 26.03.2016.
 */
public class Grid {

	private int GRIDE_SIZE = 3;
	
	// 2D Cell field
	private Cell[][] cell;
	
	/**
	 * Constructor 
	 */
	public Grid() {
		cell = new Cell[GRIDE_SIZE][GRIDE_SIZE];
		for (int row = 0; row < cell.length; row++) {
			for (int column = 0; column < cell[row].length; column++) {
				cell[row][column] = new Cell(row, column);
			}
		}
	}
	/**
	 * get the cell in a position in Grid 
	 * @param row
	 * @param column
	 * @return a Cell object or null
	 */
	public Cell getCell(int row, int column){
		if(row < cell.length && column < cell[row].length){
			return cell[row][column];
		}else {
			return null;
		}
	}
	
	/**
	 * set the Cell with the Value given
	 * @param row number
	 * @param column number
	 * @param value
	 * @return true if is Set or false if it isn't
	 */
	public boolean setCell(int row, int column, String value){
		if(row < cell.length && column < cell[row].length){
			cell[row][column].setValue(value);
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Get Grid size
	 * @return Grid size
	 */
	public int getGridSize(){
		return GRIDE_SIZE;
	}
}
