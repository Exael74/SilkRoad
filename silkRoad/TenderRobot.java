package silkRoad;

/**
 * Tender robot that only takes half of the tenges from stores.
 * Can move in any direction.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public class TenderRobot extends Robot {
    private static final String TENDER_COLOR = "green";
    
    /**
     * Constructor for TenderRobot.
     * 
     * @param position initial position of the robot
     */
    public TenderRobot(int position) {
        super(position, TENDER_COLOR);
    }
    
    @Override
    public String getType() {
        return "tender";
    }
    
    @Override
    public boolean canMove(int moveDistance) {
        return true; // Can move in any direction
    }
    
    @Override
    public int calculateProfit(int storeTenges, int distance) {
        return (storeTenges / 2) - distance; // Half profit minus distance
    }
    
    @Override
    public int getTengesToTake(int storeTenges) {
        return storeTenges / 2; // Takes only half
    }
}