package de.htwg.tictactoe.model.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Youssef Idelhoussain
 */
public class OneDimensionGridsStateStrategy extends WinStateStrategyTemplate {


   private List<int[]> rowGridScore;
   private List<int[]> colsGridScore;
   private List<int[]> diagGridScore;

   public OneDimensionGridsStateStrategy() {
      initRowsArrays();
      initColumnsArrays();
      initDiagArrays();
   }

   private void initRowsArrays() {
      rowGridScore = new ArrayList<int[]>(3);
      // adding 3 intArrays for the 3 grids
      for (int i = 0; i < 3; i++) {
         rowGridScore.add(new int[3]);
      }

   }

   private void initColumnsArrays() {
      colsGridScore = new ArrayList<int[]>(3);
      // adding 3 intArrays for the 3 grids
      for (int i = 0; i < 3; i++) {
         colsGridScore.add(new int[3]);
      }
   }

   private void initDiagArrays() {
      diagGridScore = new ArrayList<int[]>(3);
      // adding 3 intArrays for the 3 grids
      for (int i = 0; i < 3; i++) {
         diagGridScore.add(new int[2]);
      }
   }

   @Override
   public boolean checkRow(int row, int grid) {
      if (++rowGridScore.get(grid)[row] == NUMBER_TO_WIN) {
         return true;
      }
      return false;
   }

   @Override
   public boolean checkColumn(int row, int column, int grid) {
      if (++colsGridScore.get(grid)[column] == NUMBER_TO_WIN) {
         return true;
      }
      return false;
   }

   @Override
   public boolean checkDiagonal(int row, int column, int grid) {
      if (row == column && ++diagGridScore.get(grid)[0] == NUMBER_TO_WIN) {
         return true;
      }
      return checkCellInOtherSideOfDiagOneGrid(row, column, grid);
   }

   public boolean checkCellInOtherSideOfDiagOneGrid(int row, int column, int grid) {
      for (int i = 0; i < NUMBER_TO_WIN; i++) {
         if (row == i && column == (NUMBER_TO_WIN - 1 - i) && ++diagGridScore.get(grid)[1] == NUMBER_TO_WIN) {
            return true;
         }
      }
      return false;
   }

}
