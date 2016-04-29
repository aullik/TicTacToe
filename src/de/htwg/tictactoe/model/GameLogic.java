package de.htwg.tictactoe.model;

import java.util.ArrayList;

/**
 * Created by Youssef on 26.03.2016.
 */
public class GameLogic {
	

	private final int SIZE = 3;
	private ArrayList<int[]> rowGridScore;
	private ArrayList<int[]> colsGridScore;
	private ArrayList<int[]> diagGridScore;
	private ArrayList<int[][]> diagAllGrid;
	private int[][] colsAllGrid;
	
	public GameLogic() {
		init();
	}
	public void init(){
		rowGridScore = new ArrayList<>(3);
		colsGridScore = new ArrayList<>(3);
		diagGridScore = new ArrayList<>(3);
		diagAllGrid = new ArrayList<>(2);
		for (int i = 0; i < 3; i++) {
			rowGridScore.add(new int[3]);
			colsGridScore.add(new int[3]);
			diagGridScore.add(new int[2]);
		}
		diagAllGrid.add(new int[3][3]);
		diagAllGrid.add(new int[3][3]);
		colsAllGrid = new int[3][3];
	}
	private boolean checkRowOneGrid(int row, int column, int gridPlace){
		if(++rowGridScore.get(gridPlace)[row] == SIZE){
			return true;
		}
		return false;
	}
	private boolean checkColumnOneGrid(int row, int column, int gridPlace){
		if(++colsAllGrid[row][column] == SIZE){
			return true;
		}
		if(++colsGridScore.get(gridPlace)[column] == SIZE){
			return true;
		}
		return false;
	}
	private boolean checkDiagonalOneGrid(int row, int column, int gridPlace){
		if(row == column){
			if(++diagGridScore.get(gridPlace)[0] == SIZE){
				return true;
			}
		}
		for (int i = 0; i < SIZE; i++) {
			if(row == i && column == (SIZE - 1 - i)){
				if(++diagGridScore.get(gridPlace)[1] == SIZE){
					return true;
				}
			}
		}
		return checkDiagInAllGrids(row, column, gridPlace);
	}
	private boolean incrementAllRow(int[][] is){
		for (int i = 0; i < is.length; i++) {
			for (int j = 0; j < is[i].length; j++) {
				if(++is[i][j] == SIZE){
					return true;
				}
			}
		}
		return false;
	}
	private boolean checkDiagInAllGrids(int row, int column, int gridPlace) {
		if(gridPlace == 1){
			if(row == 1 && column == 1){
				for (int[][] is : diagAllGrid) {
					incrementAllRow(is);
				}
			}else {
				return centerInAllGrid(row, column);
			}
		}else {
			if(gridPlace == 2){
				gridPlace = 1;
			}
			if(++diagAllGrid.get(gridPlace)[row][column] == SIZE){
				return true;
			}
			if(gridPlace == 0){
				return DiagOfAllGridsLogic(gridPlace + 1, row, column, 2);
			}else if(gridPlace == 1){
				return DiagOfAllGridsLogic(gridPlace - 1, row, column, -2);
			}			
		}
		return false;
	}
	
	private boolean centerInAllGrid(int row, int column){
		if (column + row == 1 && (column == 0 || row == 0)){
			int row1 = row == 0 ? 0 : 2;
			int column1 = column == 0 ? 0 : 2;
			for (int i = 0; i < diagAllGrid.size(); i++) {
				if(++diagAllGrid.get(i)[0][0] == SIZE 
						|| ++diagAllGrid.get(i)[row1][column1] == SIZE)
					return true;
			}
		}else if (column + row == SIZE && (column == 2 || row == 2)){
			int row1 = row == 1 ? 0 : 2;
			int column1 = column == 1 ? 0 : 2;
			for (int i = 0; i < diagAllGrid.size(); i++) {
				if(++diagAllGrid.get(i)[2][2] == SIZE 
						|| ++diagAllGrid.get(i)[row1][column1] == SIZE)
					return true;
			}
		}
		return false;
	}
	
	public boolean DiagOfAllGridsLogic(int gridPlace, int row, int column, int token){
		int mainRow = Math.abs(row + token);
		int mainColumn = Math.abs(column + token);
		if( (mainRow < SIZE && mainColumn < SIZE) &&
			(mainRow >= 0 && mainColumn >= 0) &&
				++diagAllGrid.get(gridPlace)[mainRow][mainColumn] == SIZE){
			return true;
		}
		if( ( mainColumn < SIZE) &&
			(mainColumn >= 0) &&
				++diagAllGrid.get(gridPlace)[row][mainColumn] == SIZE){
			return true;
		}
		if( (mainRow < SIZE) &&
			(mainRow >= 0) &&
				++diagAllGrid.get(gridPlace)[mainRow][column] == SIZE){
			return true;
		}
		return false;
	}
	public boolean checkForWin(int row, int column, int gridPlace){
		if(checkRowOneGrid(row, column, gridPlace) 
				|| checkColumnOneGrid(row, column, gridPlace) 
				|| checkDiagonalOneGrid(row, column, gridPlace)){
			return true;
		}
		return false;
	}
}
