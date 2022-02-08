import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a sardine.
 * Sardines age, move, eat plankton, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Sardine extends Organism
{
    // Characteristics shared by all sardines (class variables).
    
    // The age at which a sardine can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a sardine can live.
    private static final int MAX_AGE = 13;
    // The likelihood of a sardine breeding.
    private static final double BREEDING_PROBABILITY = 0.30;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single plankton. In effect, this is the
    // number of steps a sardine can go before it has to eat again.
    private static final int PLANKTON_FOOD_VALUE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The sardine's age.
    private int age;
    // The sardine's food level, which is increased by eating plankton.
    private int foodLevel;

    /**
     * Create a sardine. A sardine can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the sardine will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Sardine(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANKTON_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = PLANKTON_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the sardine does most of the time: it hunts for
     * plankton. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSardines A list to return newly born sardines.
     */
    public void act(List<Organism> newSardines)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSardines);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age. This could result in the sardine's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this sardine more hungry. This could result in the sardine's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for plankton adjacent to the current location.
     * Only the first live plankton is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Plankton) {
                Plankton plankton = (Plankton) plant;
                if(plankton.isAlive()) { 
                    plankton.setDead();
                    foodLevel = PLANKTON_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this sardine is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSardines A list to return newly born sardines.
     */
    private void giveBirth(List<Organism> newSardines)
    {
        // New sardines are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Sardine young = new Sardine(false, field, loc);
            newSardines.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A sardine can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
