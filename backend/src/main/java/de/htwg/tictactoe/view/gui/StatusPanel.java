package de.htwg.tictactoe.view.gui;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * @author Youssef Idelhoussain
 */
public class StatusPanel extends Group {

   private final Label statusLabel = new Label("");

   public StatusPanel() {
      super();
      getChildren().add(statusLabel);
   }

   public final void setText(final String text) {
      statusLabel.setText(text);
      statusLabel.setTextFill(Color.web("#ffffff"));
   }

   public void clear() {
      statusLabel.setText(" ");
   }
}