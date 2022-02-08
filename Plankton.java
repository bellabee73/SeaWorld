import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a plankton.
 * Plankton age and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Plankton extends Organism
{
    // Characteristics shared by all plankton (class variables).
    
    // The likelihood of a plankton growing.
    private static final double GROWTH_PROBABILITY = 0.02;
    // The maximum age a plankton can reach
    private static final int MAX_AGE = 10;
    // The maximum number of births.
    private static final int MAX_GROWTH_AMOUNT =2;
    // A shared random number generator to control growth.
    private static final Random rand = Randomizer.getRandom();
    private int age;
    /**
     * Create a plankton. A plankton can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the plankton will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plankton(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        
    }
    
    /**
     * This is what the plankton does most of the time: 
     * new plankton may grow
     * or die of old age.
     * @param field The field currently occupied.
     * @param newPlanktons A list to return newly born plankton.
     */
    public void act(List<Organism> newPlanktons)
    {
    incrementAge();
    if(isAlive()){
       growPlanktons(newPlanktons);
       Location location = getField().freeAdjacentLocation(getLocation());
       if(location != null) {
                setLocation(location);
            }
            else {
                // Overcrowding.
                setDead();
            }
    }
}

private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    private void growPlanktons(List<Organism> newPlanktons)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int growths = grow();
        for(int b = 0; b < growths && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plankton young = new Plankton(false, field, loc);
            newPlanktons.add(young); 
        }

   
}

    private int grow()
    {
        int growths = 0;
        if(rand.nextDouble() <= GROWTH_PROBABILITY) {
            growths = rand.nextInt(MAX_GROWTH_AMOUNT) + 1;
        }
        return growths;
    }
    }
        
    

