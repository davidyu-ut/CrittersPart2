Link to GitHub repo: https://github.com/davidyu-ut/CrittersPart2

Main.java 
This is the driver for this assignment. It contains the stages for the displayed world and GUI. 
List of GUI nodes:
Make - Allows the user to select the type and number of Critters to make. Assumes all possible Critters are in "./src/assignment5" directory. If
       this is different, change the line 244 to the correct directory.
Manual step - Allows the user to perform discrete, individual steps of the world by entering a valid positive integer.
Auto step - Automatically performs timesteps at the specified speed. The world is actually refreshed every 2 seconds, but the speed indicates how
			many timesteps are taken before refreshing the world. While auto-stepping, all other controls except Stop and Quit button are disabled.
Stop - Stops auto-stepping.
Seed - Allows the user to provide a seed for the random number generator. Must be a valid positive long.
Stats - Displays the stats of all available critters. This is refreshed every time the view is refreshed.
Quit - Terminates the program.

Critter.java 
The adult critter population is held in an ArrayList while the newborn Critters are held in a different ArrayList. Since Critter is an abstract class, 
each class that implements in must have its own doTimeStep() and fight() methods.

Critter1.java 
Critter1 is classified as an aggressive/predatory Critter. It runs to search for prey if it has high energy (energy > (3*running_energy_cost)), walks 
if it has low energy, and remains dormant if it is about to run out of energy. Critter1 never backtracks. Critter1 always returns true if given an 
opportunity to fight, but will reproduce during a fight if it has less than 50% of starting energy. It is represented by a black and red diamond.

Critter2.java 
Critter2 is classified as a passive/reactive Critter. It only moves when low on energy to look for Algae or to run during a fight. 
Critter2 reproduces if it gained energy in the last time step (ate Algae). It is represented by a coral circle.