package de.htwg.tictactoe.model.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ThreeDimensionGridsStateStrategyTest {

   WinStateStrategyTemplate state;

   /**
    * set up state
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      state = FactoryProducer.getThreeDimensionFactory().getInstance();
   }

   @Test
   public void testCheckRow() {
      // from top to bottom
      Assert.assertFalse(state.checkForWin(0, 0, 0));
      Assert.assertFalse(state.checkForWin(0, 1, 1));
      Assert.assertTrue(state.checkForWin(0, 2, 2));

   }

   @Test
   public void testCheckRowReverse() {
      // reverse
      Assert.assertFalse(state.checkForWin(0, 0, 2));
      Assert.assertFalse(state.checkForWin(0, 1, 1));
      Assert.assertTrue(state.checkForWin(0, 2, 0));
   }

   @Test
   public void testCheckVerticalColumn() {
      Assert.assertFalse(state.checkForWin(1, 1, 1));
      Assert.assertFalse(state.checkForWin(1, 1, 0));
      Assert.assertTrue(state.checkForWin(1, 1, 2));
   }

   @Test
   public void testCheckColumn() {
      Assert.assertFalse(state.checkForWin(0, 0, 0));
      Assert.assertFalse(state.checkForWin(1, 0, 1));
      Assert.assertTrue(state.checkForWin(2, 0, 2));
   }

   @Test
   public void testCheckReverseColumn() {
      Assert.assertFalse(state.checkForWin(0, 0, 2));
      Assert.assertFalse(state.checkForWin(1, 0, 1));
      Assert.assertTrue(state.checkForWin(2, 0, 0));
   }

   @Test
   public void testCheckDiagonal() {
      // row == column
      Assert.assertFalse(state.checkForWin(0, 0, 0));
      Assert.assertFalse(state.checkForWin(1, 1, 1));
      Assert.assertTrue(state.checkForWin(2, 2, 2));
      // row != column
      Assert.assertFalse(state.checkForWin(0, 2, 0));
      Assert.assertTrue(state.checkForWin(2, 0, 2));
   }

   @Test
   public void testCheckReverseDiagonal() {
      // row == column
      Assert.assertFalse(state.checkForWin(0, 0, 2));
      Assert.assertFalse(state.checkForWin(1, 1, 1));
      Assert.assertTrue(state.checkForWin(2, 2, 0));
      // row != column
      Assert.assertFalse(state.checkForWin(0, 2, 2));
      Assert.assertTrue(state.checkForWin(2, 0, 0));
   }

}
