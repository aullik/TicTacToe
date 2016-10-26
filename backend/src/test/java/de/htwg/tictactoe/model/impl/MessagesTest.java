package de.htwg.tictactoe.model.impl;


import org.junit.Test;

import de.htwg.tictactoe.model.impl.Messages;

public class MessagesTest {

   @Test
   public void testMessages() {
      assertEquals("Game was reseted!!!! \n", Messages.GAME_RESET_MESSAGE);
   }

}
