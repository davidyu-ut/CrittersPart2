package assignment5;
/* CRITTERS Critter2.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * David Yu
 * dy3834
 * 15460
 * Slip days used: <0>
 * Spring 2018
 */

/**
 * Critter2 is classified as a passive/reactive Critter. It only moves when
 * low on energy to look for Algae or to run during a fight. Critter2 reproduces
 * if it gained energy in the last time step (ate Algae). It is represented by
 * an "S" on the grid.
 * 
 * @author David Yu
 *
 */
public class Critter2 extends Critter {
	private int prevEnergy;				// Energy during last time step
	private boolean movedThisStep;		// Indicates if it moved already during this time step
	private boolean walked;				// Indicates if it walked in the previous time step
	private boolean ran;				// Indicates if it ran in the previous time step
	private static int numChildren;		// Number of times reproduced
	
	/**
	 * Basic constructor initializes movement indicators to false
	 */
	public Critter2() {
		prevEnergy = Params.start_energy;
		movedThisStep = false;
		walked = false;
		ran = false;
	}
	
	@Override
	/**
	 * In Critter2's timestep, it reproduces if it gained energy in the previous
	 * time step (accounting for rest/movement energy cost). If on low energy
	 * (less than 50% of starting), it moves in search of Algae.
	 */
	public void doTimeStep() {
		movedThisStep = false;
		// If we acquired energy in our previous time step, reproduce
		int compensation = 0;		// need to add this to current energy to compensate for movement + rest energy
		if (walked) {
			compensation = Params.walk_energy_cost + Params.rest_energy_cost;
		} else if (ran) {
			compensation = Params.run_energy_cost + Params.rest_energy_cost;
		} else if (this.getEnergy() != Params.start_energy){
			compensation = Params.rest_energy_cost;
		}
		if (((this.getEnergy() + compensation) > prevEnergy) && (this.getEnergy() >= Params.min_reproduce_energy)) {
			try {
				Class critter = Class.forName("assignment5.Critter2");
				Critter childCritter = (Critter)critter.newInstance();
				reproduce(childCritter, getRandomInt(8));
				numChildren++;
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				System.out.println("Cannot reproduce for Critter2");
			}
		}
		
		// Only move when low on energy (<50%) to look for Algae
		if (this.getEnergy() < (Params.start_energy / 2)) {
			walk(getRandomInt(8));	// Move in random direction looking for Algae
			movedThisStep = true;
			walked = true;
		} else {
			walked = false;
		}
		ran = false;
		
		// Update previous energy
		prevEnergy = this.getEnergy();
	}

	@Override
	/**
	 * Critter2 only attempts to fight if its opponent is an Algae.
	 * Critter2 will try to run from a fight if it has enough energy or
	 * walk away from a fight if it has not yet moved in this time step.
	 */
	public boolean fight(String opponent) {
		// Fight if opponent is Algae, or cannot move again (already moved this step)
		if (opponent.equals("@") || movedThisStep) {
			return true;
		}
		
		int dir = getRandomInt(8);
		// Try to run away
		if (this.getEnergy() > Params.run_energy_cost) {
			// TODO: check if this works
			// Intended position is unoccupied
			if (this.look(dir, true) == null) {
				run(dir);
			}
			ran = true;
			walked = false;
			return false;
		} 
		// Try to walk away
		else if (this.getEnergy() > Params.walk_energy_cost) {
			// TODO: check if this works
			// Intended position is unoccupied
			if (this.look(dir, false) == null) {
				walk(getRandomInt(8));
			}
			walked = true;
			ran = false;
			return false;
		}
		// Not enough energy to move away, so fight
		else {
			walked = false;
			ran = false;
			return true;
		}
	}
	
	/**
	 * Critter2 represented by an "S" on the grid.
	 */
	public String toString() {
		return "S";
	}
	
	/**
	 * runStats keeps track of how many times all instances of Critter2s
	 * have reproduced in total.
	 * @param critter2s contains all alive Critter2s
	 */
	public static String runStats(java.util.List<Critter> critter2s) {
		int totNumChildren = 0;
		for (Object obj : critter2s) {
			Critter2 currCritter2 = (Critter2) obj;
			totNumChildren += currCritter2.numChildren;
		}
		System.out.println(critter2s.size() + " total Critter2s");
		System.out.println("Number of naturally reproduced Critter2s: " + totNumChildren);
		
		return critter2s.size() + " total Critter2s --- " + "Number of naturally reproduced Critter2s: " + totNumChildren;
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.CIRCLE;
	}
	
	public javafx.scene.paint.Color viewOutlineColor() { return javafx.scene.paint.Color.CORAL; }
	public javafx.scene.paint.Color viewFillColor() { return javafx.scene.paint.Color.CORNFLOWERBLUE; }
}
