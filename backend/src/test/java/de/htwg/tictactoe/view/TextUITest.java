package de.htwg.tictactoe.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.htwg.tictactoe.controller.impl.Controller;
import de.htwg.tictactoe.model.impl.Game;

public class TextUITest {

   private Controller controller;
   private TextUI tui1;

   @Before
   public void setUp() throws Exception {
      controller = new Controller(new Game());
      tui1 = new TextUI(controller);
   }

   @Test
   public void testprocessInputLine() {
      tui1.processInputLine("player1-player2");
      assertEquals("player1", controller.getPlayer(0).getName());
      assertEquals("player2", controller.getPlayer(1).getName());

      Assert.assertFalse(tui1.processInputLine("q"));
      tui1.processInputLine("+9+9");
      tui1.processInputLine("player1-player2-player3");
      //reset
      tui1.processInputLine("r");
      Assert.assertFalse(controller.getWin(0));
      Assert.assertFalse(controller.getWin(1));
      //moves
      tui1.processInputLine("000");
      assertEquals("X", controller.getGame()
            .getGrids()[0].getCell(0, 0).getValue());
   }

}
