import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a sea lion.
 * Sea lions age, move, eat sardines, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class SeaLion extends Organism
{
    // Characteristics shared by all sea lions (class variables).
    
    // The age at which a sea lion can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a sea lion can live.
    private static final int MAX_AGE = 25;
    // The likelihood of a sea lion breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single sardine. In effect, this is the
    // number of steps a sea lion can go before it has to eat again.
    private static final int SARDINE_FOOD_VALUE = 6;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The sea lion's age.
    private int age;
    // The sea lion's food level, which is increased by eating sardines.
    private int foodLevel;

    /**
     * Create a sea lion. A sea lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the sea lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public SeaLion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SARDINE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = SARDINE_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the sea lion does most of the time: it hunts for
     * sardines. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSeaLions A list to return newly born sea lions.
     */
    public void act(List<Organism> newSeaLions)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSeaLions);            
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
     * Increase the age. This could result in the sea lion's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this sea lion more hungry. This could result in the sea lion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for sardines adjacent to the current location.
     * Only the first live sardine is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Sardine) {
                Sardine sardine = (Sardine) animal;
                if(sardine.isAlive()) { 
                    sardine.setDead();
                    foodLevel = SARDINE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this sea lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeaLions A list to return newly born sea lion.
     */
    private void giveBirth(List<Organism> newSeaLions)
    {
        // New sea lions are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            SeaLion young = new SeaLion(false, field, loc);
            newSeaLions.add(young);
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
     * A sea lion can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
