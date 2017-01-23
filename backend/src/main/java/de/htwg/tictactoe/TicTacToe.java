package de.htwg.tictactoe;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import de.htwg.tictactoe.controller.IController;
import de.htwg.tictactoe.view.TextUI;
import de.htwg.tictactoe.view.gui.TicTacToeGUI;
import org.apache.log4j.PropertyConfigurator;

import java.util.Scanner;

public class TicTacToe {

   private final IController controller;
   private final Injector injector;

   public TicTacToe() {
      this(new TicTacToeModule());
   }

   public TicTacToe(final Module module) {
      injector = Guice.createInjector(module);
      controller = injector.getInstance(IController.class);
   }


   void startUI() {
      TextUI tui = injector.getInstance(TextUI.class);
      //injector.getInstance(TicTacToeGUI.class);

      tui.printTUI();
      //continue until the user decides to quit
      boolean running = true;
      Scanner scanner = new Scanner(System.in);
      while (running) {
         running = tui.processInputLine(scanner.next());
      }
   }

   public static TicTacToe apply() {
      return new TicTacToe(new TicTacToeModule());
   }

   public static void main(String[] args) {

      // Set up logging through log4j
      PropertyConfigurator.configure("log4j.properties");


      // Set up Google Guice Dependency Injector
      TicTacToe ttt = new TicTacToe(new TicTacToeModule());
      ttt.startUI();

   }

   public IController getController() {
      return controller;
   }
}
