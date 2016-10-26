package de.htwg.tictactoe;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.htwg.tictactoe.controller.IController;
import de.htwg.tictactoe.controller.impl.Controller;
import de.htwg.tictactoe.model.IGame;
import de.htwg.tictactoe.model.impl.Game;


public class TicTacToeModule extends AbstractModule {

   @Override
   protected void configure() {

      bind(IGame.class).to(Game.class).in(Singleton.class);
      bind(IController.class).to(Controller.class).in(Singleton.class);
   }

}
