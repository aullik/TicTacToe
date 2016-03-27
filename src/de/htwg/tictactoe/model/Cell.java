package de.htwg.tictactoe.model;

public class Cell {
		
	private int row;
	private int column;
	private String value;
	
	/**
	 * the Constructor
	 * @param row number
	 * @param column number
	 */
	public Cell(int row, int column) {
		this.row = row;
		this.column = column;
		this.value = "";
	}
	
	/**
	 * Get the Value of the Cell
	 * @return the Value of the Cell.
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * Sets the new Value to the Cell
	 * @param value to be set
	 */
	public void setValue(String value){
		this.value = value;
	}
	
	/**
	 * 	Check if Cell is set
	 * @return true if the Cell has a value other then ""
	 */
	public boolean isSet() {
		return value != "";
	}

	/**Get the row
	 * @return row number
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * Get the column
	 * @return column number
	 */
	public int getColumn() {
		return column;
	}
	
	@Override
	public String toString(){
		return "Cell("+row+","+column+")";
	}

}
