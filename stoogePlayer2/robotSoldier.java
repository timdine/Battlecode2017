package stoogePlayer2;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.*;

public class robotSoldier implements baseRobot {
	
	Team myTeam = null;
    Team enemyTeam = null;
    
    MapLocation[] myInitialArchonLocations = null;
    MapLocation[] theirInitialArchonLocations = null;
    
    int howManyArchonsDoWeHave = -1;

    int creationTurn = -1;
    
    RobotController rc = null;
    
    List<MapLocation> placesIveBeen = null;
    boolean amIALeader = false;
    
    Direction lastShotDir = null;
    
    int turnsStuck = 0;
    
    Direction anExploreDirection = null;

	public robotSoldier() {

	}
	public void InitializeClassSpecific(RobotController rc2) throws GameActionException{
		System.out.println("In the soldier initialize");
		rc = rc2;
		myTeam = rc.getTeam();
		enemyTeam = rc.getTeam().opponent();
		myInitialArchonLocations = rc.getInitialArchonLocations(myTeam);
		theirInitialArchonLocations = rc.getInitialArchonLocations(enemyTeam);
		howManyArchonsDoWeHave = myInitialArchonLocations.length;
		creationTurn = rc.getRoundNum();
		placesIveBeen = new ArrayList<MapLocation>();
		amIALeader = false;
		lastShotDir = Direction.NORTH;
		anExploreDirection = utilityFunctions.randomDirection();
	}

	
	public void RunRoundClassSpecific(RobotController rc) throws GameActionException{
		MapLocation myLocation = rc.getLocation();
		int currentRoundNum = rc.getRoundNum();
        System.out.println(myLocation);
        Direction dirToMove = utilityFunctions.randomDirection();
        Boolean moved = false;

        // See if there are any nearby enemy robots
        RobotInfo[] robots = rc.senseNearbyRobots(-1, enemyTeam);

        dirToMove = rc.getLocation().directionTo(rc.getInitialArchonLocations(enemyTeam)[0]);
        
        //boolean amIoriginalSoldier = false;
        if (rc.readBroadcast(CHANNEL_CLAIM_INITIAL_SOLDIER) <= 1 || rc.readBroadcast(CHANNEL_CLAIM_INITIAL_SOLDIER) == rc.getID()){
        	//amIoriginalSoldier = true;
        	rc.broadcast(CHANNEL_CLAIM_INITIAL_SOLDIER, rc.getID());
        	if (!moved) {
        		moved = controllerMovement.tryMove(rc.getInitialArchonLocations(enemyTeam)[0].directionTo(rc.getLocation()).opposite(),null,rc);
        	}
        }
        
        //dodge();

        // Move randomly
        //controllerMovement.wander(placesIveBeen, rc);
        //tryMove(rc.getInitialArchonLocations(enemy)[0].directionTo(rc.getLocation()).opposite());

        String soldierRole = "";
		int isThereAScoutLeader = rc.readBroadcast(CHANNEL_SCOUT_LEADER);
		
		RobotInfo[] nearbyEnemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemyTeam);
		
		int howManyEnemiesCanISee = nearbyEnemyRobots.length;
		System.out.println("Nearby Enemies:"+howManyEnemiesCanISee);
		boolean someoneNeedsHelp = false;
		//code to check if someone needs help
		//int helpDemand = rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_DEMAND);
		int helpTurn = rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_DEMAND_TURN);
		
		if (helpTurn >= currentRoundNum-1){
			someoneNeedsHelp = true;
		} else {
			someoneNeedsHelp = false;
		}
		
		if (someoneNeedsHelp){
			System.out.println("Help Block");
			MapLocation middleSpot = rc.getLocation();
			MapLocation flashingLightDot1 = new MapLocation((float) (middleSpot.x+0.25), middleSpot.y);
			MapLocation flashingLightDot2 = new MapLocation((float) (middleSpot.x-0.25), middleSpot.y);
			if (currentRoundNum%2==0){
				rc.setIndicatorDot(flashingLightDot1, 225, 0, 0);
				rc.setIndicatorDot(flashingLightDot2, 0, 0, 225);
			} else {
				rc.setIndicatorDot(flashingLightDot2, 225, 0, 0);
				rc.setIndicatorDot(flashingLightDot1, 0, 0, 225);
			}
    		
			MapLocation placeToTryToHelp = utilityFunctions.convertIntToMapLocation(rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_LOCATION));
			
			dirToMove = rc.getLocation().directionTo(placeToTryToHelp);
			moved = controllerMovement.tryMove(dirToMove,null,rc);
		} else if (amIALeader && howManyEnemiesCanISee > 0) {
			System.out.println("I'm the scout leader, even though I'm a soldier");
			soldierRole = "Leader";
			rc.broadcast(CHANNEL_SCOUT_LEADER, rc.getRoundNum());
		} else if (amIALeader && howManyEnemiesCanISee == 0) {
			System.out.println("Passing on the matrix of leadership, like Roddimus Prime");
			amIALeader = false;
			soldierRole = "";
		} else if (isThereAScoutLeader >= rc.getRoundNum()-1) {
			System.out.println("There is a leader of some type");
			amIALeader = false;
			soldierRole = "Follower";
		} else if (isThereAScoutLeader < rc.getRoundNum()-1 && nearbyEnemyRobots.length > 0) {
			System.out.println("There was a Leader of some type in the past and I can see enemies");
			amIALeader = true;
			soldierRole = "Leader";
			rc.broadcast(CHANNEL_SCOUT_LEADER, rc.getRoundNum());
		}  else if (isThereAScoutLeader < rc.getRoundNum()-1 && nearbyEnemyRobots.length == 0) {
			System.out.println("There was a Leader of some type in the past but I can't see any enemies");
			amIALeader = false;
			soldierRole = "";
		} else {
			System.out.println("--There was an unhandled Scout Leader determination.--");
			soldierRole = "";
		}

		if (soldierRole == "Leader") {
    		System.out.println("Soldier Leader Block");
    		rc.setIndicatorDot(rc.getLocation(), 50, 200, 200);
        	if (nearbyEnemyRobots.length > 0){
        		RobotInfo targetEnemyLocation = controllerCombat.getBestRobotToAttack(nearbyEnemyRobots, rc); //getNearestRobot(nearbyRobots);
        		int theEncodedMapLocation = utilityFunctions.convertMapLocationToInt(targetEnemyLocation.location);
        		
        		System.out.println("The Leader Speaks:"+targetEnemyLocation+","+theEncodedMapLocation+","+currentRoundNum);
        		if (targetEnemyLocation != null){
        			System.out.println("Reporting location to attack");
        			rc.broadcast(CHANNEL_TO_ATTACK_LOCATION, theEncodedMapLocation);
            		rc.broadcast(CHANNEL_TO_ATTACK_TURN, currentRoundNum);
            		rc.setIndicatorDot(targetEnemyLocation.location, 0, 250, 250);
        		}
        	}
    	}
		
        int turnToAttackReported = rc.readBroadcast(CHANNEL_TO_ATTACK_TURN); // this will always be recent because there is a leader?
    	int locationToAttackInt = rc.readBroadcast(CHANNEL_TO_ATTACK_LOCATION);
    	
    	if (turnToAttackReported >= (currentRoundNum-1)) { //There is a location to attack
    		System.out.println("There is a location reported by the leader:"+turnToAttackReported+" "+(currentRoundNum-1));
    		MapLocation enemyLocation = utilityFunctions.convertIntToMapLocation(locationToAttackInt);
    		dirToMove = rc.getLocation().directionTo(enemyLocation);
    		if (!moved){
    			moved = controllerMovement.tryMove(dirToMove,null,rc);
    		}
    		System.out.println("EnemyMapLocation:"+enemyLocation);
    		System.out.println("There is an attack reference:"+turnToAttackReported+" locationInt:"+locationToAttackInt +" Direction:"+dirToMove);
    	}

    	if (!rc.hasAttacked() && nearbyEnemyRobots.length > 0){
    		System.out.println("Attacking because I haven't");
    		RobotInfo robotToAttack = controllerCombat.getBestRobotToAttack(nearbyEnemyRobots, rc);
    		Direction dirToEnemy = rc.getLocation().directionTo(robotToAttack.location);
        	// If there are some...
            if (robots.length > 0) {
                // And we have enough bullets, and haven't attacked yet this turn...
                if (rc.canFireSingleShot() && controllerCombat.wontHitAllies(dirToEnemy,robotToAttack.location, rc)) {
                    // ...Then fire a bullet in the direction of the enemy.
                	rc.setIndicatorLine(rc.getLocation(), robotToAttack.location, 255, 0, 0);
                    rc.fireSingleShot(dirToEnemy);
                    lastShotDir = dirToEnemy;
                }
            } else if (robots.length > 1){
            	if (rc.canFirePentadShot() && rc.getLocation().distanceTo(robotToAttack.location) < 2){
            		rc.setIndicatorLine(rc.getLocation(), robotToAttack.location, 255, 255, 255);
            		rc.firePentadShot(dirToEnemy);
            	} else if (rc.canFireTriadShot() && controllerCombat.wontHitAllies(dirToEnemy,robotToAttack.location, rc)){
            		rc.setIndicatorLine(rc.getLocation(), robotToAttack.location, 255, 255, 255);
            		rc.fireTriadShot(dirToEnemy);
            		lastShotDir = dirToEnemy;
            	} else if (rc.canFireSingleShot() && controllerCombat.wontHitAllies(dirToEnemy,robotToAttack.location, rc)){
            		rc.setIndicatorLine(rc.getLocation(), robotToAttack.location, 255, 0, 0);
            		rc.fireSingleShot(dirToEnemy);
            		lastShotDir = dirToEnemy;
            	}
            }
            rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(lastShotDir, (float) 0.3), 0, 0, 0);
    	}
    	
    	
    	int archonLocationToVisit = rc.readBroadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT);
    	System.out.println("Which archon should I visit:" + archonLocationToVisit);
    	if (archonLocationToVisit <= theirInitialArchonLocations.length-1){
    		if (rc.getLocation().distanceTo(theirInitialArchonLocations[archonLocationToVisit]) < 3 && nearbyEnemyRobots.length == 0) {
    			if (archonLocationToVisit > theirInitialArchonLocations.length-1){
    				System.out.println("Which archon should I visit 1 :" + archonLocationToVisit);
    				rc.broadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT, archonLocationToVisit);
    			} else {
    				System.out.println("Which archon should I visit 2 :" + archonLocationToVisit);
    				rc.broadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT, archonLocationToVisit+1);
    			}
        		
        	}
    	}

    	if (archonLocationToVisit >= rc.getInitialArchonLocations(rc.getTeam().opponent()).length){
    		archonLocationToVisit = archonLocationToVisit-1;
    	}
    	
        if (!moved){
        		System.out.println("Moving because I haven't");
        		moved = controllerMovement.tryMove(rc.getInitialArchonLocations(rc.getTeam().opponent())[archonLocationToVisit].directionTo(rc.getLocation()).opposite(),placesIveBeen,rc);	
        }
        
        System.out.println("Stuck Check");
        if (rc.hasAttacked() || rc.hasMoved()){
	    	turnsStuck = 0;
	    } else {
	    	turnsStuck++;
	    	if (turnsStuck > 10){
	    		moved = controllerMovement.tryMove(dirToMove.opposite(),null,rc);
	    	}
	    }
        
        System.out.println("Move Checks");
        if (!moved){
        	moved = controllerMovement.tryMove(anExploreDirection,placesIveBeen,rc);
        }
        if (!moved){
        	anExploreDirection = utilityFunctions.randomDirection();
        }
	}

}
