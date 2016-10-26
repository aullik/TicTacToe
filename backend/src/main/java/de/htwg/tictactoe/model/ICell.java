package de.htwg.tictactoe.model;

/**
 * @author Youssef Idelhoussain
 */
public interface ICell {

   /**
    * get the value of the given Cell.
    *
    * @return String
    */
   public String getValue();

   /**
    * checks is the given Cell is set.
    *
    * @return
    */
   public boolean isSet();
}
