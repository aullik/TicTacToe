package de.htwg.tictactoe.model.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.htwg.tictactoe.model.impl.FactoryProducer;
import de.htwg.tictactoe.model.impl.WinStateStrategyTemplate;

public class OneDimensionGridsStateStrategyTest {

	WinStateStrategyTemplate state;
	
	/**
	 * set up state 
	 * @throws Exception
	 */
	@Before
    public void setUp() throws Exception {
		state = FactoryProducer.getOneDimensionFactory().getInstance();
    }
	
	@Test
	public void testCheckRow(){
		// from top to bottom
		assertFalse(state.checkForWin(0, 0, 0));
		assertFalse(state.checkForWin(0, 1, 0));
		assertTrue(state.checkForWin(0, 2, 0));
		
	}
	
	@Test
	public void testCheckColumn(){
		assertFalse(state.checkForWin(0, 0, 0));
		assertFalse(state.checkForWin(1, 0, 0));
		assertTrue(state.checkForWin(2, 0, 0));
	}
	
	@Test
	public void testCheckDiagonal(){
		// row == column
		assertFalse(state.checkForWin(0, 0, 0));
		assertFalse(state.checkForWin(1, 1, 0));
		assertTrue(state.checkForWin(2, 2, 0));	
		// row != column
		assertFalse(state.checkForWin(0, 2, 0));
		assertTrue(state.checkForWin(2, 0, 0));	
	}
	
}
