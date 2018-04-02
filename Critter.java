package assignment5;

import java.util.ArrayList;
import java.util.List;

public abstract class Critter {
	/* NEW FOR PROJECT 5 */
	public enum CritterShape {
		CIRCLE,
		SQUARE,
		TRIANGLE,
		DIAMOND,
		STAR
	}
	
	/* the default color is white, which I hope makes critters invisible by default
	 * If you change the background color of your View component, then update the default
	 * color to be the same as you background 
	 * 
	 * critters must override at least one of the following three methods, it is not 
	 * proper for critters to remain invisible in the view
	 * 
	 * If a critter only overrides the outline color, then it will look like a non-filled 
	 * shape, at least, that's the intent. You can edit these default methods however you 
	 * need to, but please preserve that intent as you implement them. 
	 */
	public javafx.scene.paint.Color viewColor() { 
		return javafx.scene.paint.Color.WHITE; 
	}
	
	public javafx.scene.paint.Color viewOutlineColor() { return viewColor(); }
	public javafx.scene.paint.Color viewFillColor() { return viewColor(); }
	
	public abstract CritterShape viewShape(); 
	
	private static String myPackage;
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	/**
	 * This method examines the location identified by the critter’s current coordinates 
	 * and moving one or two positions (for steps = false or true respectively) in the 
	 * indicated direction
	 * 
	 * @param direction indicates where to look
	 * @param steps false means 1 step ahead, true means 2 steps ahead
	 * @return null if that location is unoccupied, or the toString() of Critter in that location
	 */
	protected final String look(int direction, boolean steps) {
		ArrayList<Integer> xyPair;
		// Look 1 position ahead
		if (!steps) {
			xyPair = calculatePos(this, direction, "walk");
		} 
		// Look 2 positions ahead
		else {
			xyPair = calculatePos(this, direction, "run");
		}
		
		// Check if there is a Critter at that location
		for (Critter currCritter : population) {
			if ((currCritter.x_coord == xyPair.get(0)) && (currCritter.y_coord == xyPair.get(1))) {
				return currCritter.toString();
			}
		}
		
		return null;
	}
	
	/* rest is unchanged from Project 4 */
	
	
	private static java.util.Random rand = new java.util.Random();
	
	/**
	 * This method returns a random number
	 * @param max is the largest you want the random number to be
	 * @return a random number between 0 and max
	 */
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	/**
	 * This method sets the seed of the random number generator
	 * @param new_seed is the seed
	 */
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;
	
	/**
	 * This method calculates the expected position of a Critter after it either
	 * walks or runs.
	 * 
	 * Note: This method does not directly alter the Critter's position, it just calculates it
	 * 
	 * @param crit is the specific Critter that is moving
	 * @param direction is from 0-7 starting from the right and going CCW
	 * @param move specifies whether the Critter is walking or running
	 * @return an ArrayList containing the Critter's new X and Y coordinates
	 */
	private ArrayList<Integer> calculatePos (Critter crit, int direction, String move) {
		ArrayList<Integer> xyPair = new ArrayList<Integer>();
		int x0 = crit.x_coord;
		int y0 = crit.y_coord;
		int xf = 0, yf = 0;
		int increment = 1;
		
		// Increment changes depending on walk/move
		if (move.equals("walk")) {
			increment = 1;
		} else if (move.equals("run")) {
			increment = 2;
		}
		
		switch (direction) {
			case 0:		// Move east
				xf = this.x_coord + increment;
				yf = this.y_coord;
				break;
			case 1: 	// Move north-east
				xf = this.x_coord + increment;
				yf = this.y_coord - increment;
				break;
			case 2:		// Move north
				xf = this.x_coord;
				yf = this.y_coord - increment;
				break;
			case 3:		// Move north-west
				xf = this.x_coord - increment;
				yf = this.y_coord - increment;
				break;
			case 4:		// Move west
				xf = this.x_coord - increment;
				yf = this.y_coord;
				break;
			case 5:		// Move south-west
				xf = this.x_coord - increment;
				yf = this.y_coord - increment;
				break;
			case 6:		// Move south
				xf = this.x_coord;
				yf = this.y_coord + increment;
				break;
			case 7:		// south-east
				xf = this.x_coord + increment;
				yf = this.y_coord + increment;
				break;
			default:
				break;
		}
		
		// Readjust positions if out of bounds
		// Right wraps around to left
		if (xf > (Params.world_width - 1)) {
			xf = xf - Params.world_width;
		}
		// Left wraps around to right
		if (xf < 0) {
			xf = Params.world_width + xf;
		}
		// Up wraps around to down
		if (yf < 0) {
			yf = Params.world_height + yf;
		}
		// Down wraps around to up
		if (yf > (Params.world_height - 1)) {
			yf = yf - Params.world_height;
		}
		
		xyPair.add(xf);
		xyPair.add(yf);
		return xyPair;
	}
	
	/**
	 * This method alters the location of the Critter by 1 unit if it has not moved yet
	 * in this current time step
	 * 
	 * @param direction is the direction the Critter intends to move
	 */
	protected final void walk(int direction) {
		ArrayList<Integer> xyPair;
		
		// If this is called from fight(), make sure we don't move to position that already has Critter on it
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		String callerMethodName = elements[1].getMethodName();
		
		this.energy -= Params.walk_energy_cost;
		if (callerMethodName.equals("fight")) {
			xyPair = calculatePos(this, direction, "walk");	// Determine attempted final position
			int attemptedX = xyPair.get(0);		
			int attemptedY = xyPair.get(1);
			// Check that attempted position does not have another Critter on it
			for (Critter currCritter : population) {
				if (((currCritter.x_coord == attemptedX) && (currCritter.y_coord == attemptedY)) && (currCritter != this)) {
					return;		// Do not walk to new position if occupied by another Critter
				}
			}
			this.x_coord = attemptedX;
			this.y_coord = attemptedY;
			
			return;
		}
		
		// Critter kills himself trying to walk (not enough energy)
		if ((this.energy - Params.walk_energy_cost) <= 0) {
			this.energy = 0;
			return;
		}
		// Critter successfully walks
		else {
			xyPair = calculatePos(this, direction, "walk");	// Determine final position
			this.x_coord = xyPair.get(0);	// Update x-coordinate
			this.y_coord = xyPair.get(1);	// Update y-coordinate

		}
	}
	
	/**
	 * This method alters the location of the Critter by 2 unit if it has not moved yet
	 * in this current time step
	 * 
	 * @param direction
	 */
	protected final void run(int direction) {
		ArrayList<Integer> xyPair;
		
		// If this is called from fight(), make sure we don't move to position that already has Critter on it
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		String callerMethodName = elements[1].getMethodName();
		
		this.energy -= Params.run_energy_cost;
		if (callerMethodName.equals("fight")) {
			xyPair = calculatePos(this, direction, "run");	// Determine attempted final position
			int attemptedX = xyPair.get(0);
			int attemptedY = xyPair.get(1);
			// Check that attempted position does not have another Critter on it
			for (Critter currCritter : population) {
				if (((currCritter.x_coord == attemptedX) && (currCritter.y_coord == attemptedY)) && (currCritter != this)) {
					return;		// Do not walk to new position if occupied by another Critter
				}
			}
			this.x_coord = attemptedX;
			this.y_coord = attemptedY;

			return;
		}
		
		// Critter kills himself trying to run (not enough energy)
		if ((this.energy - Params.run_energy_cost) <= 0) {
			this.energy = 0;
			return;
		}
		// Critter successfully runs
		else {
			xyPair = calculatePos(this, direction, "run");	// Determine final position
			this.x_coord = xyPair.get(0);	// Update x-coordinate
			this.y_coord = xyPair.get(1);	// Update y-coordinate
			
		}
	}
	
	/**
	 * This method places the Critter's offspring onto the grid adjacent to its parent
	 * 
	 * @param offspring is the Critter to place
	 * @param direction indicates where to place the Critter adjacent to parent
	 */
	protected final void reproduce(Critter offspring, int direction) {
		ArrayList<Integer> xyPair;
		
		// Check if parent has enough energy to reproduce
		if (this.energy < Params.min_reproduce_energy) {
			return;
		}
		
		// Offspring gets 1/2 of parent's energy
		offspring.energy = (int) (Math.floor(this.energy / 2));
		this.energy = (int) (Math.ceil(this.energy / 2));
		
		// Assign offspring's position
		xyPair = calculatePos(this, direction, "walk");	// Determine attempted final position
		offspring.x_coord = xyPair.get(0);
		offspring.y_coord = xyPair.get(1);
		
		// Add to babies array list
		babies.add(offspring);
	}

	public abstract void doTimeStep();
	public abstract boolean fight(String oponent);
	
	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name is the name of the Critter class
	 * @throws InvalidCritterException if specified an unknown Critter class
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		try {
			Class critter = Class.forName("assignment4." + critter_class_name);
			Critter newCritter = (Critter)critter.newInstance();
			population.add(newCritter);	// Add newly created critter to population
		
			newCritter.x_coord = getRandomInt(Params.world_width);	// Random x-coor
			newCritter.y_coord = getRandomInt(Params.world_height);	// Random y-coor
			newCritter.energy = Params.start_energy;	// Initial energy
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new InvalidCritterException(critter_class_name);
		}
	}
	
	/**
	 * Gets a list of critters of a specific type.
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException if cannot get instance
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new java.util.ArrayList<Critter>();
	
		for (Critter currCritter : population) {
			Class cls = currCritter.getClass();
			if (critter_class_name.equals(cls.getSimpleName())) {
				result.add(currCritter);
			}
		}
		
		return result;
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static String runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();		
		
		return null;
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}
		
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}
		
		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * This method determines if two Critters occupy same location
	 * @param crit1 is Critter 1
	 * @param crit2 is Critter 2
	 * @return true if they share the same location, false otherwise
	 */
	private static boolean samePosition(Critter crit1, Critter crit2) {
		if ((crit1.x_coord == crit2.x_coord) && (crit1.y_coord == crit2.y_coord)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		// Complete this method.
		population.clear();
		babies.clear();
	}
	
	/**
	 * This method is called every time the user orders the "step" command. It does the following:
	 * 1. Increment the timestep
	 * 2. doTimeStep() for each Critter
	 * 3. Resolve the fights
	 * 4. updateRestEnergy()
	 * 5. Generate Algae
	 * 6. Move babies to general population
	 */
	public static void worldTimeStep() {
		
		// Allow each Critter to do timestep (move, reproduce, etc.)
		List<Critter> notAlive = new ArrayList<Critter>();
		for (Critter currCritter : population) {
			if (currCritter.energy > 0) {
				currCritter.doTimeStep();
			} else {
				notAlive.add(currCritter);
			}
		}
		
		population.removeAll(notAlive);
		
		// Add Critters with same position into one array list
		ArrayList<Critter> conflict = new ArrayList<Critter>();
		for (int slow = 0; slow < population.size(); slow++) {
			Critter crit1 = population.get(slow);
			for (int fast = slow + 1; fast < population.size(); fast++) {
				Critter crit2 = population.get(fast);
				
				if (samePosition(crit1, crit2)) {
					if (!conflict.contains(crit1)) {
						conflict.add(crit1);
					}
					if (!conflict.contains(crit2)) {
						conflict.add(crit2);
					}
				}
				
			}
		}
		
		// Resolve conflict
		for (int ind = 0; ind < conflict.size(); ind++) {
			int crit1Roll, crit2Roll;								// What each creature rolls
			boolean crit1Removed = false, crit2Removed = false;		// Facilitates indexing
			List<Critter> toRemove = new ArrayList<Critter>();		// Which creatures have died
			int slow = 0, fast = 0;									// Pointers into population
			while (slow < conflict.size()) {
				Critter crit1 = conflict.get(slow);
				fast = slow + 1;
				while (fast < conflict.size()) {
					crit1Removed = false;
					crit2Removed = false;
					Critter crit2 = conflict.get(fast);
					
					// If two creatures are in the same position
					if (samePosition(crit1, crit2)) {
						// Invoke both fights to determine Critters' intentions
						boolean crit1Fight = crit1.fight(crit2.toString());
						boolean crit2Fight = crit2.fight(crit1.toString());
					
						// After fight() invoked, both Critters are still alive and in same position
						if ((crit1.energy > 0) && (crit2.energy > 0) && samePosition(crit1, crit2)) {						
							if (!crit1Fight) {	// crit1 did not intend to fight
								crit1Roll = 0;
							} else {			// crit1 did intend to fight
								crit1Roll = getRandomInt(crit1.energy);	// Random # between 0 and crit1.energy
							}
							
							if (!crit2Fight) {	// crit2 did not intend to fight
								crit2Roll = 0;
							} else {			// crit2 did intend to fight
								crit2Roll = getRandomInt(crit2.energy);	// Random # between 0 and crit2.energy
							}
							
							// crit1 won the fight
							// If rolls are equal, crit1 arbitrarily wins
							if (crit1Roll >= crit2Roll) {
								crit1.energy += (Math.floor(crit2.energy / 2));	// Half of loser's energy goes to winner
								crit2.energy = 0;	// crit2 has no energy
								crit2Removed = true;
							}
							
							// crit2 won the fight
							if (crit2Roll > crit1Roll) {
								crit2.energy += (Math.floor(crit1.energy / 2));	//Half of loser's energy goes to winner
								crit1.energy = 0;	// crit1 has no energy
								crit1Removed = true;
							}
						}
	
						// crit1 energy depleted
						if (crit1.energy <= 0) {
							toRemove.add(crit1);
							conflict.remove(crit1);		// Remove from population
						}
							
						// crit2 energy depleted
						if (crit2.energy <= 0) {
							toRemove.add(crit2);
							conflict.remove(crit2);		// Remove from population
						}
						
						// Take care of indices after removing elements
						if (crit1Removed || crit2Removed) {
							slow = 0;
							fast = slow;
						}
						
					}
					fast++;
				}	
				slow++;
			}		
			population.removeAll(toRemove);		// Remove Critters who have died
		}
		
		// Update rest energy
		List<Critter> toRemove = new ArrayList<Critter>();
		for (Critter currCritter : population) {
			currCritter.energy -= Params.rest_energy_cost;
			if (currCritter.energy <= 0) {
				toRemove.add(currCritter);
			}
		}
		population.removeAll(toRemove);
		
		// Generate Algae
		try {
			for (int i = 0; i < Params.refresh_algae_count; i++) {
				Critter.makeCritter("Algae");
			}
		} catch (InvalidCritterException e) {
        	System.out.println("Oops, something went wrong");
        }
		
		// Move babies to general population
		population.addAll(babies);
		babies.clear();
	}
	
	/**
	 * This method prints to standard output the grid and all alive Critters 
	 */
	public static void displayWorld() {
		
		// Initialize 2D grid with single space " "
		String[][] grid = new String[Params.world_height][Params.world_width]; 
		for (int row = 0; row < Params.world_height; row++) {
			for (int col = 0; col < Params.world_width; col++) {
				grid[row][col] = " ";
			}
		}
		
		// Place Critters on grid
		for (Critter currCritter : population) {
			grid[currCritter.y_coord][currCritter.x_coord] = currCritter.toString();
		}
		
		// Print top border
		System.out.print("+");
		for (int col = 0; col < Params.world_width; col++) {
			System.out.print("-");
		}
		System.out.println("+");
		
		// Print grid
		for (int row = 0; row < Params.world_height; row++) {
			System.out.print("|");
			for (int col = 0; col < Params.world_width; col++) {
				System.out.print(grid[row][col]);
			}
			System.out.println("|");
		}
		
		// Print bottom border
		System.out.print("+");
		for (int col = 0; col < Params.world_width; col++) {
			System.out.print("-");
		}
		System.out.println("+");
	}

}
