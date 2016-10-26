package de.htwg.tictactoe.model.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GridTest {

   Grid grid;

   /**
    * set up a Grid
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      grid = new Grid();
   }

   @Test
   public void testGetGridSize() {
      assertEquals(3, grid.getGridSize());
   }

   @Test
   public void testsetCell() {
      Assert.assertTrue(grid.setCell(1, 2, "O"));
      Assert.assertFalse(grid.setCell(1, 2, "O"));
      Assert.assertFalse(grid.setCell(1, 6, "O"));
      Assert.assertFalse(grid.setCell(6, 1, "O"));

   }

   @Test
   public void testGetCell() {
      Cell cell = (Cell) grid.getCell(1, 2);
      assertEquals(1, cell.getRow());
      assertEquals(2, cell.getColumn());
      assertEquals("", cell.getValue());
      Assert.assertNull(grid.getCell(3, 4));
      Assert.assertNull(grid.getCell(1, 4));
   }

   @Test
   public void testResetGrid() {
      grid.setCell(1, 2, "O");
      assertEquals("O", grid.getCell(1, 2).getValue());
      grid.resetGrid();
      assertEquals("", grid.getCell(1, 2).getValue());
   }

   @Test
   public void testCellIsSet() {
      grid.setCell(1, 2, "O");
      Assert.assertTrue(grid.cellIsSet(1, 2));
      Assert.assertFalse(grid.cellIsSet(2, 2));

   }

   @Test
   public void testToString() {
      grid.toString(1);
      grid.setCell(1, 2, "O");
      grid.toString(1);
   }

}
