package de.htwg.tictactoe.model.impl;


import org.junit.Before;
import org.junit.Test;

import de.htwg.tictactoe.model.impl.Cell;

import static org.junit.Assert.*;

public class CellTest {
	
	Cell cell;
	
	/**
	 * set up a Cell 
	 * @throws Exception
	 */
	@Before
    public void setUp() throws Exception {
        cell = new Cell(1, 2);
    }

	@Test
	public void testGetValue(){
		assertEquals("",cell.getValue());
	}
	
	@Test
	public void testIsSet(){
		assertFalse(cell.isSet());
		cell.setValue("X");
		assertTrue(cell.isSet());
	}

	@Test
	public void testSetValue(){
		cell.setValue("X");
		assertEquals("X",cell.getValue());
	}
	
	@Test
	public void testGetRow(){
		assertEquals(1, cell.getRow());
	}
	
	@Test
	public void testGetColumn(){
		assertEquals(2, cell.getColumn());
	}
	
	@Test
	public void testToString(){
		assertEquals("Cell(1,2)", cell.toString());
	}
}
