package de.htwg.tictactoe.model.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameTest {

   Game game;

   /**
    * set up the Game
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      game = new Game();
   }

   @Test
   public void testGetGrid() {
      Grid[] grids = game.getGrids();
      Assert.assertEquals(3, grids.length);
      Assert.assertTrue(grids[0] instanceof Grid);
   }

   @Test
   public void testResetGame() {
      game.resetGame();
      String value = game.getGrids()[0].getCell(0, 0).getValue();
      Assert.assertEquals("", value);
   }

   @Test
   public void testCellIsSet() {
      game.getGrids()[0].setCell(0, 0, "X");
      Assert.assertTrue(game.cellIsSet(0, 0, 0));
   }
}
