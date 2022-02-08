import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Killer Whales, Sea Lions, Dolphins, Sea Otters, Sardines, Kelp and Plankton
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a killer whale will be created in any given grid position.
    private static final double KILLERWHALE_CREATION_PROBABILITY = 0.02;
    // The probability that a sea lion will be created in any given grid position.
    private static final double SEALION_CREATION_PROBABILITY = 0.02; 
    // The probability that a dolphin will be created in any gi3en grid position.
    private static final double DOLPHIN_CREATION_PROBABILITY = 0.04;
    // The probability that a sea otter will be created in any given grid position.
    private static final double SEAOTTER_CREATION_PROBABILITY = 0.04;
    // The probability that a sardine will be created in any given grid position.
    private static final double SARDINE_CREATION_PROBABILITY = 0.09;
    // The probability that a kelp will be created in any given grid position.
    private static final double KELP_CREATION_PROBABILITY = 0.05;
    // The probability that a plankton will be created in any given grid position.
    private static final double PLANKTON_CREATION_PROBABILITY = 0.05;

    // List of organisms in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        organisms = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(KillerWhale.class, Color.BLUE);
        view.setColor(SeaLion.class, Color.PINK);
        view.setColor(Dolphin.class, Color.BLACK);
        view.setColor(SeaOtter.class, Color.GREEN);
        view.setColor(Sardine.class, Color.GRAY);
        view.setColor(Kelp.class, Color.ORANGE);
        view.setColor(Kelp.class, Color.RED);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (1000 steps).
     */
    public void runLongSimulation()
    {
        simulate(1000);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(60);   // uncomment this to run more slowly
        }
    }
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * organism.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();  
        // Let all organisms act.

        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism animal = it.next();
            animal.act(newOrganisms);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
          
             
        // Add the newly born organisms to the main lists.
        organisms.addAll(newOrganisms);
        

        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with organisms.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= KILLERWHALE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    KillerWhale killerWhale = new KillerWhale(true, field, location);
                    organisms.add(killerWhale);
                }
                else if(rand.nextDouble() <= SEALION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    SeaLion seaLion = new SeaLion(true, field, location);
                    organisms.add(seaLion);
                }
                else if(rand.nextDouble() <= DOLPHIN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Dolphin dolphin = new Dolphin(true, field, location);
                    organisms.add(dolphin);
                }
                else if(rand.nextDouble() <= SEAOTTER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    SeaOtter seaOtter = new SeaOtter(true, field, location);
                    organisms.add(seaOtter);
                }
                else if(rand.nextDouble() <= SARDINE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Sardine sardine = new Sardine(true, field, location);
                    organisms.add(sardine);
                }
                else if(rand.nextDouble() <= KELP_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Kelp kelp = new Kelp(true, field, location);
                    organisms.add(kelp);
                }
                else if(rand.nextDouble() <= PLANKTON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plankton plankton = new Plankton(true, field, location);
                    organisms.add(plankton);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
