package stoogePlayer2;

import battlecode.common.*;

public class robotTank implements baseRobot {

	Team myTeam = null;
    Team enemyTeam = null;
    
    MapLocation[] myInitialArchonLocations = null;
    MapLocation[] theirInitialArchonLocations = null;
    
    int howManyArchonsDoWeHave = -1;

    int creationTurn = -1;
    
    RobotController rc = null;
    
    boolean amIALeader = false;
    
    Direction lastShotDir = null;
	
	public robotTank() {
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
		amIALeader = false;
		lastShotDir = Direction.NORTH;
		
	}

	
	public void RunRoundClassSpecific(RobotController rc) throws GameActionException{
		MapLocation myLocation = rc.getLocation();
		int currentRoundNum = rc.getRoundNum();
        System.out.println(myLocation);
        Direction dirToMove = utilityFunctions.randomDirection();
        Boolean moved = false;
        System.out.println("Still a tank");

        // See if there are any nearby enemy robots
        RobotInfo[] robots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemyTeam);

        int archonLocationToVisit = rc.readBroadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT);
    	if (archonLocationToVisit <= theirInitialArchonLocations.length-1){
    		if (rc.getLocation().distanceTo(theirInitialArchonLocations[archonLocationToVisit]) < 3 && robots.length == 0) {
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
   	
        dirToMove = rc.getLocation().directionTo(rc.getInitialArchonLocations(enemyTeam)[archonLocationToVisit]);
        
        //dodge();

        // Move randomly
        //controllerMovement.wander(placesIveBeen, rc);
        //tryMove(rc.getInitialArchonLocations(enemy)[0].directionTo(rc.getLocation()).opposite());

        String tankRole = "";
		int isThereAScoutLeader = rc.readBroadcast(CHANNEL_SCOUT_LEADER);
		
		RobotInfo[] nearbyEnemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemyTeam);

		float distanceToNearestTank = 999;
		for (RobotInfo aRobot:nearbyEnemyRobots){
			if (aRobot.type == RobotType.TANK && distanceToNearestTank > rc.getLocation().distanceTo(aRobot.getLocation())){
				dirToMove = rc.getLocation().directionTo(aRobot.getLocation());
				if (!moved){
	    			System.out.println("Trying to move towards a nearby tank");
	    			moved = controllerMovement.tryMove(dirToMove,null,rc);
	    		}
			}
		}
		
		int howManyEnemiesCanISee = nearbyEnemyRobots.length;
		System.out.println("Nearby Enemies:"+howManyEnemiesCanISee);

		if (amIALeader && howManyEnemiesCanISee > 0) {
			System.out.println("I'm the scout leader, even though I'm a Tank, where is your god now!?!");
			tankRole = "Leader";
			rc.broadcast(CHANNEL_SCOUT_LEADER, rc.getRoundNum());
		} else if (amIALeader && howManyEnemiesCanISee == 0) {
			System.out.println("Passing on the matrix of leadership, like Ultra Magnus");
			amIALeader = false;
			tankRole = "";
		} else if (isThereAScoutLeader >= rc.getRoundNum()-1) {
			System.out.println("There is a leader of some type");
			amIALeader = false;
			tankRole = "Follower";
		} else if (isThereAScoutLeader < rc.getRoundNum()-1 && nearbyEnemyRobots.length > 0) {
			System.out.println("There was a Leader of some type in the past and I can see enemies");
			amIALeader = true;
			tankRole = "Leader";
			rc.broadcast(CHANNEL_SCOUT_LEADER, rc.getRoundNum());
		}  else if (isThereAScoutLeader < rc.getRoundNum()-1 && nearbyEnemyRobots.length == 0) {
			System.out.println("There was a Leader of some type in the past but I can't see any enemies");
			amIALeader = false;
			tankRole = "";
		} else {
			System.out.println("--There was an unhandled Tank Leader determination.--");
			tankRole = "";
		}

		if (tankRole == "Leader") {
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
    	
    	//if someone called for help go there
    	
    	//if the leader called go there
    	
    	//if I don't know where to go head towards a random archon
    	
    	if (turnToAttackReported >= (currentRoundNum-1)) { //There is a location to attack
    		System.out.println("There is a location reported by the leader:"+turnToAttackReported+" "+(currentRoundNum-1));
    		MapLocation enemyLocation = utilityFunctions.convertIntToMapLocation(locationToAttackInt);
    		dirToMove = rc.getLocation().directionTo(enemyLocation);
    		if (!moved){
    			System.out.println("Trying to move towards leader reported location");
    			moved = controllerMovement.tryMove(dirToMove,null,rc);
    		}
    		System.out.println("EnemyMapLocation:"+enemyLocation);
    		System.out.println("There is an attack reference:"+turnToAttackReported+" locationInt:"+locationToAttackInt +" Direction:"+dirToMove);
    	}

    	

    	if (!rc.hasAttacked() && nearbyEnemyRobots.length > 0){
    		RobotInfo robotToAttack = controllerCombat.getBestRobotToAttack(nearbyEnemyRobots, rc);
    		Direction dirToEnemy = rc.getLocation().directionTo(robotToAttack.location);
        	// If there are some...
            if (robots.length > 0) {
                // And we have enough bullets, and haven't attacked yet this turn...
            	
            	if (rc.canFirePentadShot() && rc.getLocation().distanceTo(robotToAttack.location) < 10){ //added pentad shot to tanks.
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
            	

                if (rc.canFireSingleShot() && controllerCombat.wontHitAllies(dirToEnemy,robotToAttack.location, rc)) {
                    // ...Then fire a bullet in the direction of the enemy.
                	rc.setIndicatorLine(rc.getLocation(), robotToAttack.location, 255, 0, 0);
                    rc.fireSingleShot(dirToEnemy);
                    lastShotDir = dirToEnemy;
                }
            }
            rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(lastShotDir, (float) 0.6), 0, 0, 0);
    	}
    	

        if (!moved){
        	System.out.println("Choosing to move towards the default location");
        	moved = controllerMovement.tryMove(dirToMove,null,rc);	
        }
		
		

		
		/*
		Team enemy = rc.getTeam().opponent();
	    RobotInfo[] robots = rc.senseNearbyRobots(-1,enemy);
	    
	    if(robots.length > 0) {
	        MapLocation myLocation = rc.getLocation();
	        MapLocation enemyLocation = robots[0].getLocation();
	        Direction toEnemy = myLocation.directionTo(enemyLocation);
	        System.out.println("Enemies for the tank");
	        if (toEnemy != null){
	        	controllerMovement.tryMove(toEnemy,null,rc);
	        }
	        Clock.yield();
	    } else {
	        // Move Randomly
	        controllerMovement.tryMove(utilityFunctions.randomDirection(),null,rc);
	    	System.out.println("Tank driving");
	    	Clock.yield();
	    }*/
	}
	
	

}
