package de.htwg.tictactoe.model.impl;


import org.junit.Before;
import org.junit.Test;

import de.htwg.tictactoe.model.impl.Grid;
import de.htwg.tictactoe.model.impl.Player;

import static org.junit.Assert.*;

public class PlayerTest {
	
	Player player;
	Grid[] grids = new Grid[3];
	/**
	 * set up a Player 
	 * @throws Exception
	 */
	@Before
    public void setUp() throws Exception {
		for (int i = 0; i < grids.length; i++) {
			grids[i] = new Grid();
			
		}
        player = new Player("youssef", "X", grids);
    }
	
	@Test
	public void testGetName(){
		assertEquals("youssef", player.getName());
	}
	
	@Test
	public void testGetSymbol(){
		assertEquals("X", player.getSymbol());
	}

	@Test 
	public void testSetSymbol(){
		boolean move = player.setSymbol(1, 1, 0);
		assertTrue(grids[0].cellIsSet(1, 1));
		assertFalse(move);
		assertFalse(player.setSymbol(1, 3, 0));
	}
	@Test 
	public void testResetPlayer(){
		player.setSymbol(1, 1, 0);
		player.setSymbol(1, 1, 1);
		assertTrue(player.setSymbol(1, 1, 2));
		player.resetPlayer();
		assertFalse(player.setSymbol(1, 1, 2));
	}
	
	@Test
	public void testPlayerWon(){
		player.setSymbol(0, 0, 0);
		player.setSymbol(1, 0, 0);
		player.setSymbol(2, 0, 0);
	}

}
