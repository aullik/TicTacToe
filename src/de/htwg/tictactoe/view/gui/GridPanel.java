package de.htwg.tictactoe.view.gui;


import de.htwg.tictactoe.controller.IController;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * 
 * @author Youssef Idelhoussain
 *
 */
public class GridPanel extends Group {
    final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
    IController controller;
    private Color color =  Color.rgb(237, 255, 250);
    final CellPanel[][] cells;
    		
    public GridPanel(final IController controller, double size, double shade, int id) {
        getTransforms().addAll(rz, ry, rx);
        this.controller = controller;
        
        cells = new CellPanel[3][3]; 
        double width = 0.5;
        double col = 0.1;
        double height = 0.335;  
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
            	cells[row][column] = new CellPanel(controller.getGame().getGrids()[id].getCell(row, column),
        				row, column, size / 3, id, width*size/3, height*size,
        				color.deriveColor(0.0, 1.0, 1 - 0.3*col, 1.0));
				height -= 0.335;
				col+=0.2;
				setOnClickListener(cells[row][column]);
            }
			width-= 1.001;
			height = 0.335; 
        }
        
        getChildren().addAll(cells[0][0], cells[0][1], cells[0][2]
        		, cells[1][0], cells[1][1], cells[1][2], cells[2][0],cells[2][1], cells[2][2]);    
    }
    public void setOnClickListener(final CellPanel cell){
    	cell.setOnMouseClicked(
			new EventHandler<MouseEvent>(){
	            public void handle(MouseEvent t) {
	            	String[] data = cell.getId().split(" ");
	            	if(!controller.getWin(0) && !controller.getWin(1)){	
	            		controller.setValue(Integer.parseInt(data[0]),Integer.parseInt(data[1]), Integer.parseInt(data[2]));
	            	}
	            }
			});
    }
    
    public void resetGrid(){ 
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                double col = 0.1;
            	cells[row][column].resetCell(color.deriveColor(0.0, 1.0, 1 - 0.3*col, 1.0));
				col+=0.2;
            }
        }	
    }
}