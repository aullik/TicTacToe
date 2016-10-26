package de.htwg.tictactoe.view.gui;


import de.htwg.tictactoe.model.ICell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 * @author Youssef Idelhoussain
 */
public class CellPanel extends Rectangle {

   ICell cell;

   public CellPanel(ICell cell, int row, int column, double d, int grid, double e, double f, Color color) {
      this.cell = cell;
      setWidth(d);
      setHeight(d);
      setFill(color);
      setTranslateX(e);
      setTranslateY(-2.05 * d / 3);
      setTranslateZ(f);
      setRotationAxis(Rotate.X_AXIS);
      setRotate(90);
      setId(row + " " + column + " " + grid);
      setValue(cell);
   }

   public void setValue(ICell cell) {
      if ("X".equals(cell.getValue())) {
         this.setFill(Color.ORANGE);
      } else if ("O".equals(cell.getValue())) {
         this.setFill(Color.YELLOW);
      }
   }


   public boolean isSet() {
      return cell.isSet();
   }

   public void resetCell(Color color) {
      this.setFill(color);
   }
}
