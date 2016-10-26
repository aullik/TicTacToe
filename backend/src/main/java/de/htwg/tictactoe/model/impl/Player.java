package de.htwg.tictactoe.model.impl;

import de.htwg.tictactoe.model.IGrid;
import de.htwg.tictactoe.model.IPlayer;

/**
 * @author Youssef Idelhoussain
 */
public class Player implements IPlayer {

   private String name;
   private String symbol;
   private Grid[] grids;
   WinStateStrategyTemplate oneGridStrategy;
   WinStateStrategyTemplate allGridStrategy;


   /**
    * Constructor
    *
    * @param name
    * @param symbol
    * @param grids
    */
   public Player(String name, String symbol, IGrid[] grids) {
      this.name = name;
      this.symbol = symbol;
      this.grids = (Grid[]) grids;
      oneGridStrategy = FactoryProducer.getOneDimensionFactory().getInstance();
      allGridStrategy = FactoryProducer.getThreeDimensionFactory().getInstance();
   }

   /**
    * sets a move and returns if a player won
    *
    * @param row
    * @param column
    * @param grid
    * @return
    */
   @Override
   public boolean setSymbol(int row, int column, int grid) {
      if (grids[grid].setCell(row, column, symbol)) {
         return playerWon(row, column, grid);
      }
      return false;
   }


   /**
    * reset the oneGridStrategy
    */
   public void resetPlayer() {
      oneGridStrategy = FactoryProducer.getOneDimensionFactory().getInstance();
      allGridStrategy = FactoryProducer.getThreeDimensionFactory().getInstance();
   }

   /**
    * checks if a player won
    *
    * @param row
    * @param column
    * @param grid
    * @return
    */
   private boolean playerWon(int row, int column, int grid) {
      return oneGridStrategy.checkForWin(row, column, grid) || allGridStrategy.checkForWin(row, column, grid);
   }

   @Override
   public String getName() {
      return name;
   }

   public String getSymbol() {
      return symbol;
   }
}
