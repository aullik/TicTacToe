package de.htwg.tictactoe.model.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.htwg.tictactoe.model.impl.FactoryProducer;
import de.htwg.tictactoe.model.impl.WinStateStrategyTemplate;

public class ThreeDimensionGridsStateStrategyTest {

	WinStateStrategyTemplate state;
	
	/**
	 * set up state 
	 * @throws Exception 
	 */
	@Before
    public void setUp() throws Exception {
		state = FactoryProducer.getThreeDimensionFactory().getInstance(); 
    }
	
	@Test
	public void testCheckRow(){
		// from top to bottom
		assertFalse(state.checkForWin(0, 0, 0));
		assertFalse(state.checkForWin(0, 1, 1));
		assertTrue(state.checkForWin(0, 2, 2));
		
	}
	
	@Test
	public void testCheckRowReverse(){
		// reverse
		assertFalse(state.checkForWin(0, 0, 2));
		assertFalse(state.checkForWin(0, 1, 1));
		assertTrue(state.checkForWin(0, 2, 0));
	}
	
	@Test
	public void testCheckVerticalColumn(){
		assertFalse(state.checkForWin(1, 1, 1));
		assertFalse(state.checkForWin(1, 1, 0));
		assertTrue(state.checkForWin(1, 1, 2));
	}
	@Test
	public void testCheckColumn(){
		assertFalse(state.checkForWin(0, 0, 0));
		assertFalse(state.checkForWin(1, 0, 1));
		assertTrue(state.checkForWin(2, 0, 2));
	}
	@Test
	public void testCheckReverseColumn(){
		assertFalse(state.checkForWin(0, 0, 2));
		assertFalse(state.checkForWin(1, 0, 1));
		assertTrue(state.checkForWin(2, 0, 0));
	}
	
	@Test
	public void testCheckDiagonal(){
		// row == column
		assertFalse(state.checkForWin(0, 0, 0));
		assertFalse(state.checkForWin(1, 1, 1));
		assertTrue(state.checkForWin(2, 2, 2));	
		// row != column
		assertFalse(state.checkForWin(0, 2, 0));
		assertTrue(state.checkForWin(2, 0, 2));	
	}
	@Test
	public void testCheckReverseDiagonal(){
		// row == column
		assertFalse(state.checkForWin(0, 0, 2));
		assertFalse(state.checkForWin(1, 1, 1));
		assertTrue(state.checkForWin(2, 2, 0));	
		// row != column
		assertFalse(state.checkForWin(0, 2, 2));
		assertTrue(state.checkForWin(2, 0, 0));	
	}
	
}
