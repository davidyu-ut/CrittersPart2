package assignment5;
/* CRITTERS Critter1.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * David Yu
 * dy3834
 * 15460
 * Slip days used: <0>
 * Spring 2018
 */

/**
 * Critter1 is classified as an aggressive/predatory Critter. It runs to search for prey if it has
 * high energy (energy greater than (3*running_energy_cost)), walks if it has low energy, and remains dormant
 * if it is about to run out of energy. Critter1 never backtracks.
 * 
 * Critter1 always returns true if given an opportunity to fight, but will reproduce during a fight 
 * if it has less than 50% of starting energy. It is represented by a "P" on the grid.
 * 
 * @author David Yu
 *
 */
public class Critter1 extends Critter {
	private int origin;											// Indicates originating direction to prevent backtracking
	private static int highestEnergy = Params.start_energy;		// Holds record for highest energy
	
	/**
	 * Basic constructor initializes origin
	 */
	public Critter1() {
		// Pick random direction we don't go towards upon first creation
		origin = Critter.getRandomInt(8);
	}
	
	
	@Override
	/**
	 * In a timestep, Critter1 determines a random direction to move in order to
	 * search for prey. However, it never goes back to the position it was just 
	 * at (never backtracks). 
	 */
	public void doTimeStep() {
		int currEnergy = this.getEnergy();
		int moveDir = Critter.getRandomInt(8);	
		
		// Check if highest energy record has been broken
		if (this.getEnergy() > highestEnergy) {
			highestEnergy = this.getEnergy();
		}
		
		// Don't attempt to move in direction we came from (don't backtrack)
		while (moveDir == origin) {
			moveDir = Critter.getRandomInt(8);
		}
		
		// We are on low energy (less than 10% of starting), so do nothing
		if (currEnergy <= (Params.start_energy * 0.10)) {
			return;
		}
		
		// If we have enough energy to run at least 3 more times, run
		else if (currEnergy >= (Params.run_energy_cost * 3)) {
			run(moveDir);
		}
		
		// We have medium energy, so walk
		else {
			walk(moveDir);
		}

	}

	@Override
	/**
	 * In its fight method, Critter1 reproduces before a fight if it has less than
	 * 50% of its starting energy as a safety mechanism incase it loses the fight.
	 */
	public boolean fight(String opponent) {
		// Opponent is Algae
		if (opponent.equals("@")) {
			return true;
		}
		
		// We have less than 50% of starting energy going into fight
		if ((this.getEnergy() < (Params.start_energy / 2)) && (this.getEnergy() >= Params.min_reproduce_energy)) {
			// Reproduce before fighting as backup
			try {
				Class critter = Class.forName("assignment4.Critter1");
				Critter childCritter = (Critter)critter.newInstance();
				reproduce(childCritter, getRandomInt(8));
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				System.out.println("Cannot reproduce for Critter1");
			}
		}
		return true;
	}
	
	/**
	 * Represented as a "P" on the grid.
	 */
	public String toString() {
		return "P";
	}
	
	/**
	 * runStats keeps track of the average energies of all alive Critter1s
	 * and the record for the highest energy count.
	 * 
	 * @param critter1s contains all alive Critter1s
	 */
	public static String runStats(java.util.List<Critter> critter1s) {
		//String statStr = new String();
		if(critter1s.size() < 1) {
			return "total Critter1s: 0";
		}
		int avgEnergy = 0;
		for (Object obj : critter1s) {
			Critter1 currCritter1 = (Critter1) obj;
			avgEnergy += currCritter1.getEnergy();
		}
		avgEnergy = avgEnergy / critter1s.size();
		
		/*
		System.out.println(critter1s.size() + " total Critter1s");
		System.out.println("Average energy of current Critter1s: " + avgEnergy);
		System.out.println("Highest energy record: " + highestEnergy);
		*/
		
		return critter1s.size() + " total Critter1s --- " + "Average energy of current Critter1s: " + avgEnergy + " --- " + "Highest energy record: " + highestEnergy;
	}

	
	@Override
	public CritterShape viewShape() {
		return CritterShape.DIAMOND;
	}
	
	public javafx.scene.paint.Color viewOutlineColor() { return javafx.scene.paint.Color.BLACK; }
	public javafx.scene.paint.Color viewFillColor() { return javafx.scene.paint.Color.RED; }
}
