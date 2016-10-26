package de.htwg.tictactoe.model.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Youssef Idelhoussain
 *
 */
public class ThreeDimensionGridsStateStrategy extends WinStateStrategyTemplate{


	private int[][] verticalColumn;
	private List<int[][]> diagOfAllGrids;
	
	public ThreeDimensionGridsStateStrategy() {
		initColumnsArray();
		initAllDiagArrays();
	}
	
	private void initColumnsArray() {
		// vertical column of all grids
		verticalColumn = new int[3][3];
	}
	private void initAllDiagArrays() {
		diagOfAllGrids = new ArrayList<int[][]>(3);
		//3 * 3 matrix for the three Grid
		for (int i = 0; i < 3; i++) {
			diagOfAllGrids.add(new int[3][3]);
		}	
	}
	@Override
	public boolean checkRow(int row, int grid) {
		int rowLength = 0;
		int reverseRowLength = 0;
		for (int i = 0; i < diagOfAllGrids.size(); i++) {
			rowLength += diagOfAllGrids.get(i)[row][i];
			reverseRowLength+= diagOfAllGrids.get(i)[row][2 - i];
		}
		return rowLength == NUMBER_TO_WIN || reverseRowLength == NUMBER_TO_WIN;
	}

	public boolean checkVerticalColumn(int row, int column){
		if(++verticalColumn[row][column] == NUMBER_TO_WIN){
			return true;
		}
		return false;
	}
	@Override
	public boolean checkColumn(int row, int column, int grid) {
		int columnLength = 0;
		int reverseColumnLength = 0;
		for (int i = 0; i < diagOfAllGrids.size(); i++) {
			columnLength += diagOfAllGrids.get(i)[i][column];
			reverseColumnLength+= diagOfAllGrids.get(i)[2 - i][column];
		}
		return checkVerticalColumn(row, column) 
				|| columnLength == NUMBER_TO_WIN 
				|| reverseColumnLength == NUMBER_TO_WIN;
	}

	@Override
	public boolean checkDiagonal(int row, int column, int grid) {
		++diagOfAllGrids.get(grid)[row][column];
		if(row == column){
			return checkDiagOfAllGridsTogetherColumnEqualRow();
		}else if(row + column == 2){
			return checkDiagOfAllGridsTogetherInOtherSideOfDiagOneGrid();
		}
		return false;
	}

	private boolean checkDiagOfAllGridsTogetherColumnEqualRow(){
		int diagLength = 0;
		int reverseDiagLength = 0;
		for (int i = 0; i < diagOfAllGrids.size(); i++) {
			diagLength += diagOfAllGrids.get(i)[i][i];
			reverseDiagLength+= diagOfAllGrids.get(i)[2 - i][2 - i];
		}
		return diagLength == NUMBER_TO_WIN || reverseDiagLength == NUMBER_TO_WIN;
	}

	private boolean checkDiagOfAllGridsTogetherInOtherSideOfDiagOneGrid(){
		int diagLength = 0;
		int reverseDiagLength = 0;
		for (int i = 0; i < diagOfAllGrids.size(); i++) {
			diagLength += diagOfAllGrids.get(i)[2 - i][i];
			reverseDiagLength+= diagOfAllGrids.get(i)[i][2 - i];
			
		}
		return diagLength == NUMBER_TO_WIN || reverseDiagLength == NUMBER_TO_WIN;
	} 
	
		

}
