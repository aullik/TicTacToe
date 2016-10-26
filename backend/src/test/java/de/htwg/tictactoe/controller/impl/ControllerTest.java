package de.htwg.tictactoe.controller.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.htwg.tictactoe.controller.impl.Controller;
import de.htwg.tictactoe.model.impl.Game;

public class ControllerTest {

   Controller controller;

   /**
    * set up a Cell
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      controller = new Controller(new Game());
   }

   @Test
   public void testSetPlayers() {
      controller.setPlayers("player2", "player1");
      assertEquals("player2", controller.getPlayer(0).getName());
      assertEquals("player1", controller.getPlayer(1).getName());
   }

   @Test
   public void testgetPlayer() {
      controller.setPlayers("player1", "player2");
      assertEquals("player1", controller.getPlayer(0).getName());
      assertEquals("player2", controller.getPlayer(1).getName());
      Assert.assertNull(controller.getPlayer(3));
   }

   @Test
   public void testReset() {
      controller.reset();
      controller.setPlayers("player1", "");
      controller.reset();
      controller.setPlayers("player1", "player2");
      controller.reset();
      Assert.assertFalse(controller.getWin(0));
      Assert.assertFalse(controller.getWin(1));
      Assert.assertFalse(controller.getWin(3));
   }


   @Test
   public void testTryToMove() {
      controller.setPlayers("player1", "player2");

      controller.tryToMove(0, 0, 0, 0);
      Assert.assertTrue(controller.getTurn());
      controller.tryToMove(0, 0, 0, 0);
      Assert.assertFalse(controller.getTurn());
      controller.tryToMove(0, 0, 0, 0);

   }

   @Test
   public void testSetValue() {
      controller.setValue(0, 0, 0);
      controller.setPlayers("player1", "");
      controller.setValue(0, 0, 0);
      controller.setPlayers("player1", "player2");
      controller.setValue(0, 0, 0);
      controller.setValue(1, 1, 0);
      controller.setValue(0, 0, 1);
      controller.setValue(1, 1, 1);
      controller.setValue(0, 0, 2);
      controller.reset();
      controller.setPlayers("", "player2");
      controller.setValue(0, 0, 0);
      controller.setPlayers("player1", "player2");
      controller.setValue(0, 0, 0);
      controller.setValue(1, 1, 0);
      controller.setValue(0, 1, 1);
      controller.setValue(1, 1, 1);
      controller.setValue(0, 0, 2);
      controller.setValue(1, 1, 2);


      controller.setValue(3, 0, 0);
      controller.setValue(0, 3, 0);
      controller.setValue(0, 0, 3);
   }

   @Test
   public void testGetGame() {
      Assert.assertTrue(controller.getGame() instanceof Game);
   }

   @Test
   public void testGetGameString() {
      Assert.assertNotNull(controller.getGameString());
   }

   @Test
   public void testGetStatus() {
      Assert.assertNotNull(controller.getStatus());
   }
}
