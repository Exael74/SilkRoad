package silkRoad;

import java.util.ArrayList;
import java.util.Random;

/**
 * Autonomous store that chooses its own position randomly.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public class AutonomousStore extends Store {
    private static final String AUTONOMOUS_COLOR = "pink";
    
    /**
     * Constructor for AutonomousStore.
     * The store will choose its own position from available positions.
     * 
     * @param suggestedPosition suggested position (may be ignored)
     * @param tenges initial amount of tenges
     * @param routeLength total length of the route
     * @param occupiedPositions list of positions already occupied by stores
     */
    public AutonomousStore(int suggestedPosition, int tenges, int routeLength, 
                          ArrayList<Integer> occupiedPositions) {
        super(choosePosition(suggestedPosition, routeLength, occupiedPositions), 
              tenges, AUTONOMOUS_COLOR);
    }
    
    /**
     * Chooses an available position for the store.
     * 
     * @param suggestedPosition suggested position to avoid
     * @param routeLength total route length
     * @param occupiedPositions list of occupied positions
     * @return chosen position
     */
    private static int choosePosition(int suggestedPosition, int routeLength,
                                     ArrayList<Integer> occupiedPositions) {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        
        for (int i = 0; i < routeLength; i++) {
            if (!occupiedPositions.contains(i)) {
                availablePositions.add(i);
            }
        }
        
        if (availablePositions.isEmpty()) {
            return suggestedPosition; // Fallback
        }
        
        // Remove suggested position if possible
        if (suggestedPosition >= 0 && suggestedPosition < routeLength) {
            availablePositions.remove(Integer.valueOf(suggestedPosition));
            if (availablePositions.isEmpty()) {
                return suggestedPosition;
            }
        }
        
        Random random = new Random();
        return availablePositions.get(random.nextInt(availablePositions.size()));
    }
    
    @Override
    public String getType() {
        return "autonomous";
    }
    
    @Override
    public boolean canRobotTakeTenges(Robot robot) {
        return true; // Any robot can take tenges
    }
}
