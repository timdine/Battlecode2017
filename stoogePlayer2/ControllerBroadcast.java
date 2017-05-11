package stoogePlayer2;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class ControllerBroadcast {
	ControllerBroadcast(){
		
	}
	
	static int CHANNEL_HELP_DEMAND = 250;
    static int CHANNEL_HELP_DEMAND_TURN = 251;
	static int CHANNEL_HELP_LOCATION = 252;
	static int CHANNEL_SEEN_LAND_BADDIE = 253;

	public void callForHelpIfRequired(RobotController rc, int currentTurn, int intLocation, int helpDemand) throws GameActionException {
		 if (currentTurn >= rc.readBroadcast(CHANNEL_HELP_DEMAND_TURN)) {
    		rc.broadcast(CHANNEL_HELP_DEMAND, helpDemand);
    		rc.broadcast(CHANNEL_HELP_DEMAND_TURN, currentTurn);
    		rc.broadcast(CHANNEL_HELP_LOCATION, intLocation);
    	}
		 if (rc.getRoundNum() % 2 == 0){
			 rc.setIndicatorDot(rc.getLocation(), 250, 250, 0);
		 } else {
			 rc.setIndicatorDot(rc.getLocation(), 250, 0, 0);
		 }
		
	}
}
