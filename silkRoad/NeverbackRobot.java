package silkRoad;

/**
 * Neverback robot that cannot move backwards.
 * Takes all tenges from stores.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public class NeverbackRobot extends Robot {
    private static final String NEVERBACK_COLOR = "blue";
    
    /**
     * Constructor for NeverbackRobot.
     * 
     * @param position initial position of the robot
     */
    public NeverbackRobot(int position) {
        super(position, NEVERBACK_COLOR);
    }
    
    @Override
    public String getType() {
        return "neverback";
    }
    
    @Override
    public boolean canMove(int moveDistance) {
        return moveDistance >= 0; // Cannot move backwards
    }
    
    @Override
    public int calculateProfit(int storeTenges, int distance) {
        return storeTenges - distance;
    }
    
    @Override
    public int getTengesToTake(int storeTenges) {
        return storeTenges; // Takes all tenges
    }
}