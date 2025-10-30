package silkRoad;

/**
 * Fighter store that only allows robots with higher profit to take tenges.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public class FighterStore extends Store {
    private static final String FIGHTER_COLOR = "orange";
    
    /**
     * Constructor for FighterStore.
     * 
     * @param position store position on the route
     * @param tenges initial amount of tenges
     */
    public FighterStore(int position, int tenges) {
        super(position, tenges, FIGHTER_COLOR);
    }
    
    @Override
    public String getType() {
        return "fighter";
    }
    
    @Override
    public boolean canRobotTakeTenges(Robot robot) {
        // Only robots with more profit than store tenges can take
        return robot.getTotalProfit() > this.tenges;
    }
}
