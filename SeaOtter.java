import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a sea otter.
 * Sea otters age, move, eat kelp, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class SeaOtter extends Organism
{
    // Characteristics shared by all sea otters (class variables).
    
    // The age at which a sea otter can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a sea otter can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a sea otter breeding.
    private static final double BREEDING_PROBABILITY = 0.20;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single kelp. In effect, this is the
    // number of steps a sea otter can go before it has to eat again.
    private static final int KELP_FOOD_VALUE = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The sea otter's age.
    private int age;
    // The sea otter's food level, which is increased by eating kelp.
    private int foodLevel;

    /**
     * Create a sea otter. A sea otter can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the sea otter will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public SeaOtter(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(KELP_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = KELP_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the sea otter does most of the time: it hunts for
     * kelp. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSeaOtters A list to return newly born sea otters.
     */
    public void act(List<Organism> newSeaOtters)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSeaOtters);            
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
     * Increase the age. This could result in the sea otter's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this sea otter more hungry. This could result in the sea otter's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for kelp adjacent to the current location.
     * Only the first live kelp is eaten.
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
            if(plant instanceof Kelp) {
                Kelp kelp = (Kelp) plant;
                if(kelp.isAlive()) { 
                    kelp.setDead();
                    foodLevel = KELP_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this sea otter is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeaOtters A list to return newly born sea otters.
     */
    private void giveBirth(List<Organism> newSeaOtters)
    {
        // New sea otters are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            SeaOtter young = new SeaOtter(false, field, loc);
            newSeaOtters.add(young);
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
     * A sea otter can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
