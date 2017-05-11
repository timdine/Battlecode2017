package stoogePlayer2;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.*;


public class robotLumberjack implements baseRobot {

	
	Team myTeam = null;
    Team enemyTeam = null;
    
    MapLocation[] myInitialArchonLocations = null;
    MapLocation[] theirInitialArchonLocations = null;
    
    int howManyArchonsDoWeHave = -1;

    int creationTurn = -1;
    
    RobotController rc = null;
    
    int currentTurn = -1;
    
    List<TreeInfo> treesIWantToChop = null;
    
    int turnsStuck = 0;
    
    boolean haveIbeenCloseToBadGuys = false;
    
    Direction anExploreDirection = null;
    
	public robotLumberjack() {
	}

	public void InitializeClassSpecific(RobotController rc2) throws GameActionException{
		System.out.println("In the lumberjack initialize");
		rc = rc2;
		myTeam = rc.getTeam();
		enemyTeam = rc.getTeam().opponent();
		myInitialArchonLocations = rc.getInitialArchonLocations(myTeam);
		theirInitialArchonLocations = rc.getInitialArchonLocations(enemyTeam);
		howManyArchonsDoWeHave = myInitialArchonLocations.length;
		creationTurn = rc.getRoundNum();
		currentTurn = rc.getRoundNum();
		treesIWantToChop = new ArrayList<TreeInfo>();
		anExploreDirection = utilityFunctions.randomDirection();
	}

	
	public void RunRoundClassSpecific(RobotController rc) throws GameActionException{
		currentTurn = rc.getRoundNum();
		
		RobotInfo[] enemyRobots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemyTeam);
		
		
	    boolean moved = false;
	    
	    boolean someoneNeedsHelp = false;
		//code to check if someone needs help
		//int helpDemand = rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_DEMAND);
		int helpTurn = rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_DEMAND_TURN);
		
		if (helpTurn >= currentTurn-1){
			someoneNeedsHelp = true;
		} else {
			someoneNeedsHelp = false;
		}
		
		if (someoneNeedsHelp){
			System.out.println("Help Block");
			MapLocation middleSpot = rc.getLocation();
			MapLocation flashingLightDot1 = new MapLocation((float) (middleSpot.x+0.25), middleSpot.y);
			MapLocation flashingLightDot2 = new MapLocation((float) (middleSpot.x-0.25), middleSpot.y);
			if (currentTurn%2==0){
				rc.setIndicatorDot(flashingLightDot1, 225, 0, 0);
				rc.setIndicatorDot(flashingLightDot2, 0, 0, 225);
			} else {
				rc.setIndicatorDot(flashingLightDot2, 225, 0, 0);
				rc.setIndicatorDot(flashingLightDot1, 0, 0, 225);
			}
    		
			MapLocation placeToTryToHelp = utilityFunctions.convertIntToMapLocation(rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_LOCATION));
			
			Direction dirToMove = rc.getLocation().directionTo(placeToTryToHelp);
			moved = controllerMovement.tryMove(dirToMove,null,rc);
		}
	    
	    if (!haveIbeenCloseToBadGuys){
	    	if (rc.readBroadcast(ControllerBroadcast.CHANNEL_SEEN_LAND_BADDIE)<999){ // if > 999 someone else already found a land baddie
	    		if (enemyRobots.length > 0){
		    		boolean isLandBaddieNearby = checkDistanceToEnemyRobotsForLandBaddies(enemyRobots, rc);
		    		if (isLandBaddieNearby){
		    			rc.broadcast(ControllerBroadcast.CHANNEL_SEEN_LAND_BADDIE, 1000);
		    		}
		    	}
	    	} else {
	    		System.out.println("I haven't been close to land baddies, but someone has");
	    	}
	    } else {
	    	System.out.println("I've been close to land baddies");
	    }
	    
	    
	    int distanceToScanForTrees = 1;
	    TreeInfo[] theNearbyTrees = rc.senseNearbyTrees((float) distanceToScanForTrees,rc.getTeam().opponent());
	    	    
	    for (int i=1;i<rc.getType().sensorRadius && (theNearbyTrees.length < 1);i++){
	    	System.out.println("Scanning for trees:"+i);
	    	theNearbyTrees = rc.senseNearbyTrees((float) i,Team.NEUTRAL);
	    	System.out.println(theNearbyTrees.length);
	    	if (theNearbyTrees.length > 0){
	    		System.out.println("Found some enemy trees to chop:"+theNearbyTrees.length);
	    	}
	    }
	    if (theNearbyTrees.length == 0){
	    	theNearbyTrees = rc.senseNearbyTrees((float) distanceToScanForTrees,Team.NEUTRAL);
	    	for (int i=1;i<rc.getType().sensorRadius && (theNearbyTrees.length < 1);i++){
		    	System.out.println("Scanning for trees:"+i);
		    	theNearbyTrees = rc.senseNearbyTrees((float) i,Team.NEUTRAL);
		    	System.out.println(theNearbyTrees.length);
		    	if (theNearbyTrees.length > 0){
		    		System.out.println("Found some trees to chop:"+theNearbyTrees.length);
		    	}
		    }
	    }

	    Direction dirToMove = utilityFunctions.randomDirection();

	    TreeInfo theTreeToChop = findBestTreeToChop(rc, theNearbyTrees);
	    
	    
	    int archonLocationToVisit = rc.readBroadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT);
    	if (archonLocationToVisit <= theirInitialArchonLocations.length-1){
    		if (rc.getLocation().distanceTo(theirInitialArchonLocations[archonLocationToVisit]) < 3 && enemyRobots.length == 0) {
    			if (archonLocationToVisit > theirInitialArchonLocations.length-1){
    				rc.broadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT, archonLocationToVisit);
    			} else {
    				rc.broadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT, archonLocationToVisit+1);
    			}
        		
        	}
    	}
    	
    	if (archonLocationToVisit >= rc.getInitialArchonLocations(rc.getTeam().opponent()).length){
    		archonLocationToVisit = archonLocationToVisit-1;
    	}
	    
	    
	    if (enemyRobots.length > 0){
	    	System.out.println("There are enemies close that need choppin");
	    	for(RobotInfo theEnemy:enemyRobots){
	    		if (rc.canStrike()){
	    			System.out.println("The target type:"+theEnemy.type);
	    			dirToMove = rc.getLocation().directionTo(theEnemy.location);
	    			System.out.println("set dir");
	    			moved = controllerMovement.tryMove(dirToMove, DEFAULT_TURN_ANGLE_DEGREES, DEFAULT_TURN_ATTEMPTS, null, rc);
	    			System.out.println("Chopping a baddie");
	    			rc.strike();
	    			System.out.println("struck");
	    		}
	    	}
	    } else if (theTreeToChop != null){
	    	rc.setIndicatorDot(theTreeToChop.location, 150, 150, 10);
	    	if (rc.canChop(theTreeToChop.location)){
	    		System.out.println("Chopping a good tree");
		    	rc.chop(theTreeToChop.location);
	    	} else if (!moved){
	    		dirToMove = rc.getLocation().directionTo(theTreeToChop.location);
	    		moved = controllerMovement.tryMove(dirToMove, DEFAULT_TURN_ANGLE_DEGREES, DEFAULT_TURN_ATTEMPTS, null, rc);
	    	}
		    	
	    } else {
	    	System.out.println("other lumberjack situation");
	    	int turnToAttackReported = rc.readBroadcast(CHANNEL_TO_ATTACK_TURN); // this will always be recent because there is a leader?
        	int locationToAttackInt = rc.readBroadcast(CHANNEL_TO_ATTACK_LOCATION);
        	MapLocation enemyLocation = utilityFunctions.convertIntToMapLocation(locationToAttackInt);
        	if (turnToAttackReported > currentTurn -1){
        		dirToMove = rc.getLocation().directionTo(enemyLocation);
        	} else {
        		dirToMove = rc.getLocation().directionTo(theirInitialArchonLocations[archonLocationToVisit]);
        	}
        	if (!moved){
        		moved = controllerMovement.tryMove(dirToMove, DEFAULT_TURN_ANGLE_DEGREES, DEFAULT_TURN_ATTEMPTS, null, rc);
        	}
	    }
	    
	    
	    if (rc.hasAttacked() || rc.hasMoved()){
	    	turnsStuck = 0;
	    } else {
	    	turnsStuck++;
	    	if (turnsStuck > 20){
	    		rc.disintegrate();
	    	}
	    }
	    
	    //int lumberjackTarget = rc.readBroadcast(CHANNEL_LUMBERJACK_TARGET);
	    //int lumberjackTargetTurn = rc.readBroadcast(CHANNEL_LUMBERJACK_TARGET_TURN);
	    
	    
	    if (!moved && !rc.hasAttacked()){
        	moved = controllerMovement.tryMove(anExploreDirection,null,rc);
        }
        if (!moved && !rc.hasAttacked()){
        	anExploreDirection = utilityFunctions.randomDirection();
        }
	    
	    
	    
	    
	    
	    /*
	    if (theNearbyTrees.length > 0 && theNearbyTrees[0].getContainedBullets() == 0) {
	    	System.out.println("There are nearby trees");
	    	System.out.println(theNearbyTrees[0].getContainedBullets());
	    	System.out.println(theNearbyTrees[0].getContainedRobot());
	    	if (theNearbyTrees[0].getContainedBullets() == 0 && rc.canChop(theNearbyTrees[0].getLocation())){
	    		System.out.println("Chopping");
	    		rc.chop(theNearbyTrees[0].getLocation());
	    	}
	    } else if(robots.length > 0 && !rc.hasAttacked()) {
	        // Use strike() to hit all nearby robots!
	        rc.strike();
	    } else {
	        // No close robots, so search for robots within sight radius
	        robots = rc.senseNearbyRobots(-1,enemyTeam);

	        // If there is a robot, move towards it
	        if(robots.length > 0) {
	            MapLocation myLocation = rc.getLocation();
	            MapLocation enemyLocation = robots[0].getLocation();
	            Direction toEnemy = myLocation.directionTo(enemyLocation);
	            if (toEnemy != null){
	            	controllerMovement.tryMove(toEnemy,null,rc);
	            }
	        } else {
	            // Move Randomly
	            controllerMovement.tryMove(utilityFunctions.randomDirection(),null,rc);
	        }
	    }*/
	}

	private boolean checkDistanceToEnemyRobotsForLandBaddies(RobotInfo[] enemyRobots, RobotController rc2) {
		for (RobotInfo enemyRobot: enemyRobots){
			if (enemyRobot.getLocation().distanceTo(rc2.getLocation())<=5){
				if (enemyRobot.getType() != RobotType.SCOUT){
					System.out.println("I see land baddies");
					return true;
				}
			}
		}
		return false;
	}

	private TreeInfo findBestTreeToChop(RobotController rc, TreeInfo[] theNearbyTrees) {
		System.out.println("Checking trees");
		float shortestDistance = 999;
		TreeInfo closestTree = null;
		for (TreeInfo theTree: theNearbyTrees){
			if (rc.canChop(theTree.location)){
				System.out.println("Found a tree to chop");
				return theTree;
			} else if (rc.getLocation().distanceTo(theTree.location) < shortestDistance){
				shortestDistance = rc.getLocation().distanceTo(theTree.location);
				closestTree = theTree;
			}
		}
		System.out.println("There weren't any trees to chop, returning null");
		return closestTree;
	}
}
