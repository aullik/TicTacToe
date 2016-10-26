package de.htwg.tictactoe.model.impl;

/**
 * @author Youssef Idelhoussain
 */
public final class Messages {

   public static final String PLEASE_MESSAGES = "please enter the two players name with - between then (don't forget " +
         "no spacing)";

   public static final String WELCOME_MESSAGE = "Welcome to HTWG TicTacToe 3D! \n" + PLEASE_MESSAGES;

   public static final String ERROR_GIVE_PLAYERS_RESET = "you can't reset the Game without giving the name of the " +
         "players\n"
         + PLEASE_MESSAGES;
   public static final String ERROR_GIVE_PLAYERS_START = "you can't start the Game without giving the name of the " +
         "players\n"
         + PLEASE_MESSAGES;

   public static final String WIN_MESSAGE = " you won !! congratulation \n "
         + " if you want to start again press r + enter, if not press x + enter to exit";

   public static final String PLAYER_DEFINED_MESSAGE = "Players are defined!!!! \n";

   public static final String GAME_RESET_MESSAGE = "Game was reseted!!!! \n";

   public static final String INFO_ABOUT_THE_GAME = " you can start, the grids with the number in them are\n" +
         " an example of what you should give here if you want to player in one of the cells,\n" +
         " the 3 numbers are the row, column, grid. Now, MAKE YOU FIRST MOVE :) !!\n";

   public static final String CELL_IS_SET = "This Cell is Already been setted, please try another one \n";

   public static final String NEXT = " you are next!! \n";

   public static final String ERROR_MOVE = "you are out of the limit of the grid!! please retry with correct move";

   public static final String PLAYER_NAME = "please enter players name again";

   public static final String MOVEMENT = "To move the Grids : D to go right, A to go left, W to go up, S to go " +
         "down\n\n";


   public static final String USER_ERROR = "You did not give both users name try again";

   public static final String TITLE = "TicTacToe 3D";

   private Messages() {
   }

   public static String playerMoveToString(String player, int row, int column, int grid) {
      return player + " played : (" + row + "," + column + ") in Grid " + grid + "\n";
   }

}
