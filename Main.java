package assignment5;
	
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.collections.*;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class Main extends Application {
	static GridPane grid = new GridPane();
	static Boolean isStepping = new Boolean(false);

	@Override
	public void start(Stage primaryStage) {
		try {		
			// Take care of scene
			Scene scene = new Scene(grid, 1500, 800);	// Create scene obj with GridPane
			primaryStage.setScene(scene);				// Put scene onto stage
			primaryStage.setTitle("Primary Stage");		
			primaryStage.show();						// Display stage with scene
			Painter.paintGridLines(grid);				// Paint gridlines, TODO: might need to do this every time you call displayWorld()
			
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
			
			// Manual "step" button
			Button manStep = new Button("Manual step");
			manStep.setLayoutX(0);
			manStep.setLayoutY(30);
			btnPane.getChildren().add(manStep);
			TextField stepNum = new TextField("1");		// # of times to step
			stepNum.setMaxWidth(50);
			stepNum.setLayoutX(87);
			stepNum.setLayoutY(30);
			btnPane.getChildren().add(stepNum);
			manStep.setOnAction(e-> { 
				if (isStepping) {
					return;		// Do nothing if already stepping
				}
				int stepCnt;
				// Make sure number of steps is valid
				try {
					stepCnt = Integer.parseInt(stepNum.getText().trim());
					if (stepCnt < 1) {
						notification.setText("Invalid number of steps");
						btnStage.show();
						return;
					}
					
					// Perform the steps
	        		for (int cnt = 0; cnt < stepCnt; cnt++) {
	        			Critter.worldTimeStep();
	        		}
	        		notification.setText("Successfully stepped " + stepCnt + " time(s)");
	        		btnStage.show();
	        		
	        		//Display world
	        		Critter.displayWorld();
					
				} catch (NumberFormatException nfe) {
					notification.setText("Invalid number of steps");
					btnStage.show();
					return;
				}
			});
			
			// "stats" button
			Text statsText = new Text();
			statsText.setText("Stats for Critters");
			statsText.setLayoutX(2);
			statsText.setLayoutY(105);
			btnPane.getChildren().add(statsText);
			MenuButton selectStats = new MenuButton();			// Holds which critters stats to display
			HashMap<String, Boolean> statsShowing = new HashMap<String, Boolean>();		// Keeps track of already showing stats
			Stage statsStage = new Stage();		// Stage for showing stats
			statsStage.setTitle("Stats Stage");
			//statsStage.show();
			Pane statsPane = new Pane();
			Scene statsScene = new Scene(statsPane, 300, 250);
			statsStage.setScene(statsScene);
			
			// Automatic "step" button
			Button autoStep = new Button("Auto step");
			autoStep.setLayoutX(0);
			autoStep.setLayoutY(60);
			btnPane.getChildren().add(autoStep);
			final ComboBox stepSpeeds = new ComboBox();	// Drop-down menu to select step speed
			stepSpeeds.getItems().addAll(
				"1",
				"5",
				"10",
				"50",
				"100"
			);
			stepSpeeds.setPromptText("Speed");
			stepSpeeds.setMaxWidth(80);
			stepSpeeds.setLayoutX(72);
			stepSpeeds.setLayoutY(60);
			btnPane.getChildren().add(stepSpeeds);
			autoStep.setOnAction(e->{
				if (isStepping) {
					return;		// Do nothing if already stepping
				}
				// Check if user selected correct speed
				if ((stepSpeeds.getValue() == null) || stepSpeeds.getValue().toString().isEmpty()) {
					notification.setText("Invalid step speed");
					btnStage.show();
					return;
				} else {
					notification.setText("Stepping... Press STOP to stop stepping");
					btnStage.show();
				}
				
				// Create period stepping action (not yet started)
				Timeline timeline = new Timeline(new KeyFrame(
						Duration.millis(1500),
						ae-> {
							switch(stepSpeeds.getValue().toString()) {
							default:
							case "1":
								Critter.worldTimeStep(); break;
							case "5":
								for (int cnt = 0; cnt < 5; cnt++) {
									Critter.worldTimeStep();
								} break;
							case "10":
								for (int cnt = 0; cnt < 10; cnt++) {
									Critter.worldTimeStep();
								} break;
							case "50":
								for (int cnt = 0; cnt < 50; cnt++) {
									Critter.worldTimeStep();
								} break;
							case "100":
								for (int cnt = 0; cnt < 100; cnt++) {
									Critter.worldTimeStep();
								} break;
							}
							Critter.displayWorld();
						}
				));
				timeline.setCycleCount(Animation.INDEFINITE);
				
				// Create a stop button
				Button stopStep = new Button("STOP");
				stopStep.setLayoutX(150);
				stopStep.setLayoutY(60);
				btnPane.getChildren().add(stopStep);
				stopStep.setOnAction(e2->{
					isStepping = false;
					// Remove stop button
					btnPane.getChildren().remove(stopStep);
					timeline.stop();
				});
				
				// Continue stepping
				isStepping = true;
				timeline.play();

			});
			
			
			// "quit" button
			Button quitBtn = new Button("Quit");
			quitBtn.setOnAction(e-> {
				System.exit(0);
			});
			quitBtn.setLayoutX(0);
			quitBtn.setLayoutY(200);
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
							critOptions.add(clsName);		// Add to create options
							CheckBox cb = new CheckBox(clsName);	// Add to stats options
							CustomMenuItem critStats = new CustomMenuItem(cb);
							statsShowing.put(clsName, false);		// Keeps track of which stats are showing
							cb.setOnAction(e->{
								//System.out.println("You clicked " + clsName);
								if (statsShowing.get(clsName)) {	// Stats already showing, so stop showing
									
								} else {							// Stats not showing, so show
									statsStage.show();
									
									statsShowing.put(clsName, true);
								}
							});
							critStats.setHideOnClick(false);
							selectStats.getItems().add(critStats);
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
			selectStats.setLayoutX(90);
			selectStats.setLayoutY(90);
			btnPane.getChildren().add(selectStats);
			
			TextField makeNum = new TextField("1");		// Text field to enter number of critters to make
			makeNum.setMaxWidth(50);
			makeNum.setLayoutX(165);
			makeNum.setLayoutY(0);
			
			makeBtn.setOnAction(e-> {
				if (isStepping) {
					return;		// Do nothing if already stepping
				}
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
						// Display the new world
						Critter.displayWorld();
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
