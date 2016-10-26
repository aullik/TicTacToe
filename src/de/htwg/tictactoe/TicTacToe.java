package de.htwg.tictactoe;

import java.util.Scanner;

import org.apache.log4j.PropertyConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.htwg.tictactoe.view.TextUI;
import de.htwg.tictactoe.view.gui.TicTacToeGUI;

public class TicTacToe { 
	static Scanner scanner;
	String line = ""; 
	
	public static void main(String[] args) {
		
		// Set up logging through log4j
        PropertyConfigurator.configure("log4j.properties");
		
		// Set up Google Guice Dependency Injector
		Injector injector = Guice.createInjector(new TicTacToeModule());
		
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
}
