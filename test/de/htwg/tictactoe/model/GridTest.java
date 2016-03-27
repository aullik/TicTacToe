package de.htwg.tictactoe.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GridTest {
	
	Grid grid;
	
	/**
	 * set up a Grid 
	 * @throws Exception
	 */
	@Before
    public void setUp() throws Exception {
        grid = new Grid();
    }
	
	@Test
	public void testGetGridSize(){
		assertEquals(3, grid.getGridSize());
	}
	
	@Test
	public void testsetCell(){
		assertTrue(grid.setCell(1, 2, "O"));
	}
	
	@Test
	public void testGetCell(){
		Cell cell = grid.getCell(1, 2);
		assertEquals(1, cell.getRow());
		assertEquals(2, cell.getColumn());
		assertEquals("", cell.getValue());
	}
	
}
