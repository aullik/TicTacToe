package de.htwg.tictactoe;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.htwg.tictactoe.controller.IController;

import java.util.Scanner;

public class TicTacToe {

    static Scanner scanner;
    String line = "";

    private static IController controller;

    public TicTacToe() {
        Injector injector = Guice.createInjector(new TicTacToeModule());
        controller = injector.getInstance(IController.class);
    }

    /*
        public static void main(String[] args) {

            // Set up logging through log4j
            PropertyConfigurator.configure("log4j.properties");


            // Set up Google Guice Dependency Injector


            TextUI tui = injector.getInstance(TextUI.class);

            injector.getInstance(TicTacToeGUI.class);

            tui.printTUI();
            //continue until the user decides to quit
            boolean continu = true;
            scanner = new Scanner(System.in);
            while (continu) {
                continu = tui.processInputLine(scanner.next());
            }
        }
    */
    public IController getController() {
        return controller;
    }
}
