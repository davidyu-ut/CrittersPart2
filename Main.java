package assignment5;
	
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.collections.*;

public class Main extends Application {
	static GridPane grid = new GridPane();
	

	@Override
	public void start(Stage primaryStage) {
		try {		
			// Take care of scene
			Scene scene = new Scene(grid, 1500, 800);	// Create scene obj with GridPane
			primaryStage.setScene(scene);				// Put scene onto stage
			primaryStage.setTitle("Primary Stage");		
			primaryStage.show();						// Display stage with scene
			Painter.paint(grid);						// Paint gridlines
			
			// Make new stage for buttons
			Stage btnStage = new Stage();
			btnStage.setTitle("Button Stage");
			btnStage.show();
			Pane btnPane = new Pane();
			Scene btnScene = new Scene(btnPane, 300, 250);
			Label notification = new Label();			// Node to place error messages
			notification.setLayoutX(0);	
			notification.setLayoutY(230);
			btnPane.getChildren().add(notification);
			
			// "quit" button
			Button quitBtn = new Button("Quit");
			quitBtn.setOnAction(e-> {
				System.exit(0);
			});
			quitBtn.setLayoutX(0);
			quitBtn.setLayoutY(70);
			btnPane.getChildren().add(quitBtn);
			
			// "make" button
			Button makeBtn = new Button("Make");
			ObservableList<String> critOptions = FXCollections.observableArrayList();
			
			// TODO: maybe /bin/ instead of src?
			File dir = new File("./src/assignment5"); 	// new file obj
			Class<?> critClass = Critter.class;
			for (File file : dir.listFiles()) {
				// TODO: better if-statement?
				if ((file.getName().endsWith(".class") || file.getName().endsWith(".java")) && (!file.getName().equals("Header.java"))) {
					try {
						String clsName = file.getName().split("\\.")[0];
						Class exCrit = Class.forName("assignment5." + clsName);
						Critter newCritter = (Critter)exCrit.newInstance();
						if (Critter.class.isAssignableFrom(newCritter.getClass())) {
							//System.out.println(clsName);
							critOptions.add(clsName);
						}
					} catch (Exception e) {}	
				}
			}
			
			final ComboBox critterCombo = new ComboBox(critOptions);	// Drop-down menu to select critter type
			critterCombo.setPromptText("Select Critter");
			critterCombo.setMaxWidth(110);
			critterCombo.setLayoutX(50);
			critterCombo.setLayoutY(0);
			btnPane.getChildren().add(critterCombo);
			
			TextField makeNum = new TextField("1");		// Text field to enter number of critters to make
			makeNum.setMaxWidth(50);
			makeNum.setLayoutX(165);
			makeNum.setLayoutY(0);
			
			makeBtn.setOnAction(e-> {
				int makeCnt;
				// Make sure number of critters to make is valid
				try {
					makeCnt = Integer.parseInt(makeNum.getText().trim());
					if (makeCnt < 1) {
						notification.setText("Invalid number of Critters");
						btnStage.show();
						return;
					}
				} catch (NumberFormatException nfe) {
					notification.setText("Invalid number of Critters");
					btnStage.show();
					return;
				}
				
				// Make sure user selects valid critter
				if ((critterCombo.getValue() != null) && !critterCombo.getValue().toString().isEmpty()) {
					// Create the Critters
					try {
						for (int cnt = 0; cnt < makeCnt; cnt++) {
							Critter.makeCritter(critterCombo.getValue().toString());
						}
					} catch (InvalidCritterException ice) {
						notification.setText("Cannot create " + makeCnt + " " + critterCombo.getValue().toString() + "(s)");
						btnStage.show();
					}
					
					notification.setText("Sucessfully created " + makeCnt + " " + critterCombo.getValue().toString() + "(s)");
					btnStage.show();
				} else {
					notification.setText("Invalid Critter type");
					btnStage.show();
				}
			});
			makeBtn.setLayoutX(0);
			makeBtn.setLayoutY(0);
			btnPane.getChildren().add(makeBtn);
			btnPane.getChildren().add(makeNum);
			
			
			
			
			btnStage.setScene(btnScene);
			btnStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();		
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
