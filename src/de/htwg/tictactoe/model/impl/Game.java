package de.htwg.tictactoe.model.impl;

import de.htwg.tictactoe.model.IGame;
import de.htwg.tictactoe.model.IGrid;

/**
 * 
 * @author Youssef Idelhoussain
 *
 */
public class Game implements IGame{
	
	Grid[] grids;
	private int size = 3;
	private Player[] players = new Player[2];
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
	@Override
	public Grid[] getGrids(){
		return grids;
	}
	
	@Override
	public Player getPlayer(int player){
		if(player < 2){
			return players[player];
		}else{
			return null;
		}
	}
	
	@Override
	public void setPlayer(int i, String player, String symbol, IGrid[] grid){
		this.players[i] = new Player(player, symbol, grid);
	}
	 /**
	  * checks if cell is set
	  * @param row
	  * @param column
	  * @param grid
	  * @return
	  */
	@Override
	public boolean cellIsSet(int row, int column, int grid){
		return grids[grid].cellIsSet(row, column);
	}
	
	/**
	 * resets all Grids
	 */
	@Override
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
