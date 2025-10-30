package silkRoad;

/**
 * Lazy robot that never collects tenges from stores.
 * This robot can move but will never take any tenges, resulting in zero profit.
 * Represented with black color.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public class LazyRobot extends Robot {
    private static final String LAZY_COLOR = "black";
    
    /**
     * Constructor for LazyRobot.
     * 
     * @param position initial position of the robot
     */
    public LazyRobot(int position) {
        super(position, LAZY_COLOR);
    }
    
    @Override
    public String getType() {
        return "lazy";
    }
    
    @Override
    public boolean canMove(int moveDistance) {
        return true; // Can move in any direction, just won't collect
    }
    
    @Override
    public int calculateProfit(int storeTenges, int distance) {
        return 0; // Never gets profit
    }
    
    @Override
    public int getTengesToTake(int storeTenges) {
        return 0; // Never takes any tenges
    }
}
