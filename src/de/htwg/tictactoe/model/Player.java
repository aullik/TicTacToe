package de.htwg.tictactoe.model;

/**
 * Created by Daniel on 21.04.2016.
 */
public class Player {

	private final String name;
	private final String symbole;
	private Grid[] grids;
	GameLogic logic;
	
	public Player(String name, String symbole,Grid[] grids) {
		this.name = name;
		this.symbole = symbole;
		this.grids = grids;
		logic = new GameLogic();
	}
	public boolean move(int row, int column, int gridPlace){
		if(grids[gridPlace].setCell(row, column, symbole)){
			return checkForWin(row, column, gridPlace);
		}
		return false;
	}
	
	public boolean CellIsSet(int row, int column, int gridPlace){
		return grids[gridPlace].cellIsSet(row, column);
	}
	
	public void resetPlayer(){
		logic = new GameLogic();
	}
	
	public boolean checkForWin(int row, int column, int gridPlace){
		return logic.checkForWin(row, column, gridPlace);
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
