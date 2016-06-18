package de.htwg.tictactoe.model;

/**
 * 
 * @author Youssef Idelhoussain
 *
 */
public class Game implements IGame {
	
	Grid[] grids;
	private int size = 3;
	
	/**
	 * constructor
	 */
	public Game() {
		grids = new Grid[size];
		for (int i = 0; i < grids.length; i++) {
			grids[i] = new Grid();
		}
	}
	/**
	 * get all grids
	 * @return
	 */
	public Grid[] getGrids(){
		return grids;
	}
	
	 /**
	  * checks if cell is set
	  * @param row
	  * @param column
	  * @param grid
	  * @return
	  */
	public boolean cellIsSet(int row, int column, int grid){
		return grids[grid].cellIsSet(row, column);
	}
	
	/**
	 * resets all Grids
	 */
	public void resetGame(){
		for (int i = 0; i < grids.length; i++) {
			grids[i].resetGrid();
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < grids.length; i++) {
			sb.append(grids[i].toString(i));
		}
		return sb.toString();
	}
}
