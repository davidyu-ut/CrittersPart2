/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * <Student2 Name>
 * <Student2 EID>
 * <Student2 5-digit Unique No.>
 * Slip days used: <0>
 * Fall 2015
 */
package assignment5;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.*;

public class Painter {
	
	static int numRows = Params.world_height;
	static int numCols = Params.world_width;
	static int size = 600 / numRows;	//TODO: mess around with this num?

	/*
	 * Returns a square or a circle, according to shapeIndex
	 */
	static Shape getIcon(int shapeIndex) {
		Shape s = null;
		int size = 100;
		
		switch(shapeIndex) {
		case 0: s = new Rectangle(size, size); 
			s.setFill(javafx.scene.paint.Color.RED); break;
		case 1: s = new Circle(size/2); 
			s.setFill(javafx.scene.paint.Color.GREEN); break;
		}
		// set the outline of the shape
		s.setStroke(javafx.scene.paint.Color.BLUE); // outline
		return s;
	}
	
	/*
	 * Paints the shape on a grid.
	 */
	public static void paint(GridPane grid) {
		Main.grid.getChildren().clear(); // clean up grid.
		paintGridLines(grid);
		
	}
	
	// Paints grid lines
	private static void paintGridLines(GridPane grid) {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				Shape sector = new Rectangle(size, size);
				sector.setFill(null);
				sector.setStroke(Color.BLACK);
				grid.add(sector, col, row);
			}
		}
	}
}
