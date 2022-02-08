import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a kelp.
 * Kelp age and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Kelp extends Organism
{
    // Characteristics shared by all kelp (class variables).
    
    // The likelihood of a kelp breeding.
    private static final double GROWTH_PROBABILITY = 0.04;
    // The age to which a kelp can live.
    private static final int MAX_AGE = 10;
    // The maximum number of births.
    private static final int MAX_GROWTH_AMOUNT =2;
    // A shared random number generator to control growth.
    private static final Random rand = Randomizer.getRandom();
    private int age;
    /**
     * Create a kelp. A kelp can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the kelp will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Kelp(boolean randomAge, Field field, Location location)
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
     * This is what the kelp does most of the time: new kelp may grow 
     * or die of old age.
     * @param field The field currently occupied.
     * @param newKelps A list to return newly born kelp.
     */
    public void act(List<Organism> newKelps)
    {
    incrementAge();
    if(isAlive()){
       growKelps(newKelps);
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
    
    private void growKelps(List<Organism> newKelps)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int growths = grow();
        for(int b = 0; b < growths && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Kelp young = new Kelp(false, field, loc);
            newKelps.add(young); 
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
        
    

