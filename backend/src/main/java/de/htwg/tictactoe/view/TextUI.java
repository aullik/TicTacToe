package de.htwg.tictactoe.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.htwg.tictactoe.controller.IController;
import de.htwg.util.observer.IObserver;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class TextUI implements IObserver {

   private IController controller;

   private static final Logger LOGGER = Logger.getLogger("de.htwg.tictactoe.view.tui");

   @Inject
   public TextUI(IController controller) {
      this.controller = controller;
      controller.addObserver(this);
   }

   @Override
   public void update() {
      printTUI();
   }

   public boolean processInputLine(String line) {
      boolean continu = true;
      if ("q".equalsIgnoreCase(line)) {
         continu = false;
      }

      if ("r".equalsIgnoreCase(line)) {
         controller.reset();
      }
      checkIfUsernames(line);
      checkIfPlayerMove(line);
      return continu;
   }

   public void checkIfUsernames(String line) {
      if (line.matches("^[a-zA-Z0-9_-]*$") && line.contains("-")) {
         String[] players = line.split("-");
         if (players.length == 2) {
            controller.setPlayers(players[0], players[1]);
         }
      }
   }

   public void checkIfPlayerMove(String line) {
      // if the command line has the form 120, set the cell (1,2) in the grid 0
      if (line.matches("[0-9][0-9][0-9]")) {
         Pattern p = Pattern.compile("[0-9]");
         Matcher m = p.matcher(line);
         int[] arg = new int[3];
         for (int i = 0; i < arg.length; i++) {
            m.find();
            arg[i] = Integer.parseInt(m.group());
         }
         controller.setValue(arg[0], arg[1], arg[2]);
      }
   }

   public void printTUI() {
      LOGGER.info(controller.getGameString());
      LOGGER.info(controller.getStatus());
   }
}

