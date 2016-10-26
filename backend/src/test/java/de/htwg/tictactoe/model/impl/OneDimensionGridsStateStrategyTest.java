package de.htwg.tictactoe.model.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OneDimensionGridsStateStrategyTest {

   WinStateStrategyTemplate state;

   /**
    * set up state
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      state = FactoryProducer.getOneDimensionFactory().getInstance();
   }

   @Test
   public void testCheckRow() {
      // from top to bottom
      Assert.assertFalse(state.checkForWin(0, 0, 0));
      Assert.assertFalse(state.checkForWin(0, 1, 0));
      Assert.assertTrue(state.checkForWin(0, 2, 0));

   }

   @Test
   public void testCheckColumn() {
      Assert.assertFalse(state.checkForWin(0, 0, 0));
      Assert.assertFalse(state.checkForWin(1, 0, 0));
      Assert.assertTrue(state.checkForWin(2, 0, 0));
   }

   @Test
   public void testCheckDiagonal() {
      // row == column
      Assert.assertFalse(state.checkForWin(0, 0, 0));
      Assert.assertFalse(state.checkForWin(1, 1, 0));
      Assert.assertTrue(state.checkForWin(2, 2, 0));
      // row != column
      Assert.assertFalse(state.checkForWin(0, 2, 0));
      Assert.assertTrue(state.checkForWin(2, 0, 0));
   }

}
