package assignment5;
	
import java.io.File;
import java.util.HashMap;
import java.util.List;

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
	static HashMap<String, Integer> statLoc = new HashMap<String, Integer>();
	static Group statGroup = new Group();

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
			Scene btnScene = new Scene(btnPane, 490, 300);
			Label notification = new Label();			// Node to place error messages
			notification.setLayoutX(2);	
			notification.setLayoutY(283);
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
	        		
	        		//Update stats
	        		btnPane.getChildren().removeAll(statGroup);
	        		statGroup.getChildren().clear();
	        		for (String key : statLoc.keySet()) {
	        			specificStats(key, btnPane);
	        		}
					btnPane.getChildren().addAll(statGroup);
					
				} catch (NumberFormatException nfe) {
					notification.setText("Invalid number of steps");
					btnStage.show();
					return;
				}
			});
			
			// "stats" display
			Text critStats = new Text();
			critStats.setText("Stats for Critters:");
			critStats.setLayoutX(2);
			critStats.setLayoutY(130);
			btnPane.getChildren().add(critStats);

			
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
							
			        		//Update stats
			        		btnPane.getChildren().removeAll(statGroup);
			        		statGroup.getChildren().clear();
			        		for (String key : statLoc.keySet()) {
			        			specificStats(key, btnPane);
			        		}
							btnPane.getChildren().addAll(statGroup);
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
			quitBtn.setLayoutY(253);
			btnPane.getChildren().add(quitBtn);
			
			// "seed" button
			Button seedBtn = new Button("Seed");
			seedBtn.setLayoutX(0);
			seedBtn.setLayoutY(90);
			btnPane.getChildren().add(seedBtn);
			TextField seedNum = new TextField("Enter long seed");		// Text field to enter seed
			seedNum.setMaxWidth(100);
			seedNum.setLayoutX(46);
			seedNum.setLayoutY(90);
			btnPane.getChildren().add(seedNum);
			seedBtn.setOnAction(e->{
				if (isStepping) {
					return;		// Do nothing if already stepping
				}
				long seed;
				// Make sure seed is valid
				try {
					seed = Integer.parseInt(seedNum.getText().trim());
					if (seed < 1) {
						notification.setText("Invalid seed");
						btnStage.show();
						return;
					}
					Critter.setSeed(seed);
					notification.setText("Set seed to " + seed);
					btnStage.show();
				} catch (NumberFormatException nfe) {
					notification.setText("Invalid seed");
					btnStage.show();
					return;
				}
			});
			
			// "make" button
			Button makeBtn = new Button("Make");
			ObservableList<String> critOptions = FXCollections.observableArrayList();		
			// TODO: maybe /bin/ instead of src?
			File dir = new File("./src/assignment5"); 	// new file obj
			Class<?> critClass = Critter.class;
			int statY = 130;			// Keeps track of stats
			for (File file : dir.listFiles()) {
				// TODO: better if-statement?
				if ((file.getName().endsWith(".class") || file.getName().endsWith(".java")) && (!file.getName().equals("Header.java"))) {
					try {
						String clsName = file.getName().split("\\.")[0];
						Class exCrit = Class.forName("assignment5." + clsName);
						Critter newCritter = (Critter)exCrit.newInstance();
						if (Critter.class.isAssignableFrom(newCritter.getClass())) {
							critOptions.add(clsName);		// Add to create options
							
							statLoc.put(clsName, statY);
							statY += 15;
							// Put up their stats
							specificStats(clsName, btnPane);
							btnPane.getChildren().addAll(statGroup);
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
	
	public static void specificStats (String className, Pane btnPane) {
		try {
			Text stats = new Text();
			List<Critter> instances;
			java.lang.reflect.Method method;
			instances = Critter.getInstances(className);
			Class critter = Class.forName("assignment5." + className);
			Critter dummyCritter = (Critter)critter.newInstance();
			
			// Use reflection to call custom Critter's runStats method
			Class[] cArg = new Class[1];
			cArg[0] = List.class;
			method = dummyCritter.getClass().getMethod("runStats", cArg);
			stats.setText((String) method.invoke(dummyCritter, instances));
			stats.setLayoutX(2);
			stats.setLayoutY(statLoc.get(className));
			
			statGroup.getChildren().add(stats);
			
		} catch (Exception e) {
			System.out.println("Cannot get stats for " + className);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
