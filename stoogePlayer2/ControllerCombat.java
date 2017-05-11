package stoogePlayer2;

import java.util.List;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class ControllerCombat {
	ControllerCombat(){
		
	}

	public RobotInfo getBestRobotToAttack(RobotInfo[] nearbyRobots, RobotController rc) {
		MapLocation nearestGardenerLocation = null;
    	MapLocation nearestScoutLocation = null;
    	MapLocation nearestSoldierLocation = null;
    	MapLocation nearestLumberjackLocation = null;
    	MapLocation nearestTankLocation = null;
    	MapLocation nearestArchonLocation = null;
    	MapLocation weakestGardenerLocation = null;
    	MapLocation weakestScoutLocation = null;
    	MapLocation weakestSoldierLocation = null;
    	MapLocation weakestLumberjackLocation = null;
    	MapLocation weakestTankLocation = null;
    	MapLocation weakestArchonLocation = null;
    	
    	float nearestGardenerDistance = -1;
    	float nearestScoutDistance = -1;
    	float nearestSoldierDistance = -1;
    	float nearestLumberjackDistance = -1;
    	float nearestTankDistance = -1;
    	float nearestArchonDistance = -1;
    	float weakestGardenerDistance = -1;
    	float weakestScoutDistance = -1;
    	float weakestSoldierDistance = -1;
    	float weakestLumberjackDistance = -1;
    	float weakestTankDistance = -1;
    	float weakestArchonDistance = -1;
    	
    	RobotInfo nearestGardener = null;
    	RobotInfo weakestGardener = null;
    	RobotInfo nearestScout = null;
    	RobotInfo weakestScout = null;
    	RobotInfo nearestLumberjack = null;
    	RobotInfo weakestLumberjack = null;
    	RobotInfo nearestTank = null;
    	RobotInfo weakestTank = null;
    	RobotInfo nearestArchon = null;
    	RobotInfo weakestArchon = null;
    	RobotInfo nearestSoldier = null;
    	RobotInfo weakestSoldier = null;
    	
    	MapLocation myLocation = rc.getLocation();
    	
    	for(RobotInfo robot:nearbyRobots){
    		MapLocation testRobotLocation = robot.getLocation();
    		switch(robot.type){
    		case GARDENER:
    			if (nearestGardenerDistance == -1 || nearestGardenerLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				nearestGardenerDistance = testRobotLocation.distanceTo(myLocation);
    				nearestGardenerLocation = testRobotLocation;
    				nearestGardener = robot;
    			}
    			if (weakestGardenerDistance == -1 || weakestGardenerLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				weakestGardenerDistance = testRobotLocation.distanceTo(myLocation);
    				weakestGardenerLocation = testRobotLocation;
    				weakestGardener = robot;
    			}
    			break;
    		case SCOUT:
				if (nearestScoutDistance == -1 || nearestScoutLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
					nearestScoutDistance = testRobotLocation.distanceTo(myLocation);
					nearestScoutLocation = testRobotLocation;
					nearestScout = robot;
				}
				if (weakestScoutDistance == -1 || weakestScoutLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
					weakestScoutDistance = testRobotLocation.distanceTo(myLocation);
					weakestScoutLocation = testRobotLocation;
					weakestScout = robot;
				}
    			break;
    		case SOLDIER:
    			if (nearestSoldierDistance == -1 || nearestSoldierLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				nearestSoldierDistance = testRobotLocation.distanceTo(myLocation);
    				nearestSoldierLocation = testRobotLocation;
    				nearestSoldier = robot;
    			}
    			if (weakestSoldierDistance == -1 || weakestSoldierLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				weakestSoldierDistance = testRobotLocation.distanceTo(myLocation);
    				weakestSoldierLocation = testRobotLocation;
    				weakestSoldier = robot;
    			}
    			break;
    		case LUMBERJACK:
    			if (nearestLumberjackDistance == -1 || nearestLumberjackLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				nearestLumberjackDistance = testRobotLocation.distanceTo(myLocation);
    				nearestLumberjackLocation = testRobotLocation;
    				nearestLumberjack = robot;
    			}
    			if (weakestLumberjackDistance == -1 || weakestLumberjackLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				weakestLumberjackDistance = testRobotLocation.distanceTo(myLocation);
    				weakestLumberjackLocation = testRobotLocation;
    				weakestLumberjack = robot;
    			}
    			break;
    		case TANK:
    			if (nearestTankDistance == -1 || nearestTankLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				nearestTankDistance = testRobotLocation.distanceTo(myLocation);
    				nearestTankLocation = testRobotLocation;
    				nearestTank = robot;
    			}
    			if (weakestTankDistance == -1 || weakestTankLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				weakestTankDistance = testRobotLocation.distanceTo(myLocation);
    				weakestTankLocation = testRobotLocation;
    				weakestTank = robot;
    			}
    			break;
    		case ARCHON:
    			if (nearestArchonDistance == -1 || nearestArchonLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				nearestArchonDistance = testRobotLocation.distanceTo(myLocation);
    				nearestArchonLocation = testRobotLocation;
    				nearestArchon = robot;
    			}
    			if (weakestArchonDistance == -1 || weakestArchonLocation.distanceTo(myLocation) > testRobotLocation.distanceTo(myLocation)){
    				weakestArchonDistance = testRobotLocation.distanceTo(myLocation);
    				weakestArchonLocation = testRobotLocation;
    				weakestArchon = robot;
    			}
    			break;    		
    		}
    	}
    	
    	if (weakestGardenerLocation != null){
    		return weakestGardener;
    	} else if (nearestGardenerLocation != null){
    		return nearestGardener;
    	}else if (weakestTankLocation != null){
    	    return weakestTank;
    	} else if (nearestTankLocation != null){
    	    return nearestTank;
    	} else if (weakestScoutLocation != null){
    		return weakestScout;
    	} else if (nearestScoutLocation != null){
    		return nearestScout;
    	} else if (weakestLumberjackLocation != null){
    		return weakestLumberjack;
    	} else if (nearestLumberjackLocation != null){
    		return nearestLumberjack;
    	} else if (weakestSoldierLocation != null){
    		return weakestSoldier;
    	} else if (nearestSoldierLocation != null){
    		return nearestSoldier;
    	} else if (weakestArchonLocation != null){
    		return weakestArchon;
    	} else if (nearestArchonLocation != null){
    		return nearestArchon;
    	}
    	
    	RobotInfo theMapLocationToAttack = getNearestRobot(nearbyRobots, rc);
		return theMapLocationToAttack;
	}

	private RobotInfo getNearestRobot(RobotInfo[] nearbyRobots, RobotController rc) {
		float shortestDistance = -1;
    	MapLocation theNearestLocation = null;
    	RobotInfo theNearestRobot = null;
    	for (int i = 0;i<nearbyRobots.length;i++){
    		MapLocation thisRobotLocation = nearbyRobots[i].getLocation();
    		float distanceToThisRobot = rc.getLocation().distanceTo(thisRobotLocation);
    		if (distanceToThisRobot < shortestDistance || shortestDistance == -1) {
    			shortestDistance = distanceToThisRobot;
    			theNearestLocation = thisRobotLocation;
    			theNearestRobot = nearbyRobots[i];
    		}
    	}
    	System.out.println("Nearest Robot Location:"+theNearestLocation.toString());
		return theNearestRobot;
	}

	public boolean wontHitAllies(Direction dirToEnemy, MapLocation enemyLocation, RobotController rc) {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getType().sensorRadius,rc.getTeam());
		float distanceToEnemy = enemyLocation.distanceTo(rc.getLocation());
		for (RobotInfo ally: nearbyAllies){
			Direction directionToAlly = rc.getLocation().directionTo(ally.getLocation());
			float distanceToAlly = rc.getLocation().distanceTo(enemyLocation);
			if (directionToAlly.degreesBetween(directionToAlly) < 45 && distanceToAlly < distanceToEnemy)
			{
				return false;
			}
		}
		return true;
	}

	public boolean bulletsWontHitMeThere(MapLocation theMapLocation, List<MapLocation> placesToAvoidBullets, RobotController rc) {
		for (MapLocation place:placesToAvoidBullets){
			if (theMapLocation.distanceTo(place)<rc.getType().bodyRadius){
				System.out.println("I'd be hit by a bullet at "+theMapLocation.toString());
				rc.setIndicatorDot(theMapLocation, 150, 0, 150);
				return false;
			}
		}
		System.out.println("It is safe to move to "+theMapLocation.toString());
		return true;
	}
}
