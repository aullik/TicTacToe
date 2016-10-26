package de.htwg.tictactoe.model.impl;


/**
 * Patterns: Strategy and TemplateMethod
 * This class is the abstract strategy for the strategy pattern.
 * At the same time it is a template for the implementation of a strategy
 *
 * @author Youssef Idelhoussain
 */
public abstract class WinStateStrategyTemplate {

   protected final int NUMBER_TO_WIN = 3;

   public abstract boolean checkRow(int row, int grid);

   public abstract boolean checkColumn(int row, int column, int grid);

   public abstract boolean checkDiagonal(int row, int column, int grid);

   public boolean checkForWin(int row, int column, int grid) {
      // The order of this methods are strict
      if (checkDiagonal(row, column, grid)
            || checkRow(row, grid)
            || checkColumn(row, column, grid)) {
         return true;
      }
      return false;
   }
}
