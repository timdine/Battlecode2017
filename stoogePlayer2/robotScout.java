package stoogePlayer2;

import java.util.ArrayList;
import java.util.List;
//import java.util.Random;

import battlecode.common.*;



public class robotScout implements baseRobot {

	static boolean amIALeader = false;
    static boolean amIGathering = false;
    static Direction myPatrolDirection = null;
    
    String myRole = "";
    int currentRoundNum = -1;
    
    List<TreeInfo> unshakenTrees = new ArrayList<TreeInfo>();
	
    Team myTeam = null;
    Team enemyTeam = null;
    
    MapLocation[] myInitialArchonLocations = null;
    MapLocation[] theirInitialArchonLocations = null;
    
    int howManyArchonsDoWeHave = -1;

    int creationTurn = -1;
    
    TreeInfo[] nearbyTrees = null;
    
    RobotController rc = null;
    
    RobotInfo[] nearbyRobots = null;
    RobotInfo[] nearbyEnemyRobots = null;
    List<Integer> theBadTrees = null;
    List<MapLocation> placesIveBeen = null;
    
    int turnsWithoutShaking = 0;
    
    Direction anExploreDirection = null;
    
    public robotScout() {

	}
    
    public void InitializeClassSpecific(RobotController rc2) throws GameActionException{
		System.out.println("In the scout initialize");
		rc = rc2;
		myTeam = rc.getTeam();
		enemyTeam = rc.getTeam().opponent();
		myInitialArchonLocations = rc.getInitialArchonLocations(myTeam);
		theirInitialArchonLocations = rc.getInitialArchonLocations(enemyTeam);
		howManyArchonsDoWeHave = myInitialArchonLocations.length;
		creationTurn = rc.getRoundNum();
		nearbyTrees = rc.senseNearbyTrees();
		nearbyRobots = rc.senseNearbyRobots();
		nearbyEnemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemyTeam);
		theBadTrees = new ArrayList<Integer>();
		placesIveBeen = new ArrayList<MapLocation>();
		anExploreDirection = utilityFunctions.randomDirection();
	}

	public void RunRoundClassSpecific(RobotController rc) throws GameActionException{
				
		int currentRoundNum = rc.getRoundNum();
    	
		Direction dirToMove = null;
		
		boolean shook = false;
		//Float distancetoMove = (float) -1.00;
		
		boolean someoneNeedsHelp = false;
		//code to check if someone needs help
		//int helpDemand = rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_DEMAND);
		int helpTurn = rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_DEMAND_TURN);
		
		if (helpTurn >= currentRoundNum-1){
			someoneNeedsHelp = true;
		} else {
			someoneNeedsHelp = false;
		}
		
		boolean moved = false;
		
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


		} else if (myRole == "Leader") {
    		System.out.println("Leader Block");
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
    	} else if (myRole == "Gatherer"){
    		System.out.println("Gatherer Block");

    		TreeInfo bestUnharvestedTree = getBestUnharvestedTree(unshakenTrees);
    		if (bestUnharvestedTree != null){
    			System.out.println("Moving towards an unshaken tree");
    			dirToMove = rc.getLocation().directionTo(bestUnharvestedTree.location);
    		}
    	} else if (myRole == "Follower"){
    		int turnToAttackReported = rc.readBroadcast(CHANNEL_TO_ATTACK_TURN); // this will always be recent because there is a leader?
        	int locationToAttackInt = rc.readBroadcast(CHANNEL_TO_ATTACK_LOCATION);
        	MapLocation enemyLocation = utilityFunctions.convertIntToMapLocation(locationToAttackInt);
        	
        	//if (turnToAttackReported >= (currentRoundNum-1)) { //There is a location to attack
			System.out.println("There is a location reported by the leader:"+turnToAttackReported+" "+(currentRoundNum-1));

			dirToMove = rc.getLocation().directionTo(enemyLocation);
			System.out.println("EnemyMapLocation:"+enemyLocation);
			System.out.println("There is an attack reference:"+turnToAttackReported+" locationInt:"+locationToAttackInt +" Direction:"+dirToMove);
			/*
			for(TreeInfo tree:nearbyTrees){
				MapLocation treeLoc = tree.location;
				System.out.println("Tree Dist:"+tree.location.distanceTo(enemyLocation));
				if (treeLoc.distanceTo(enemyLocation) == 1){
    				System.out.println("Found a tree next to an enemy");
                	if (rc.getLocation().distanceTo(treeLoc)<rc.getType().strideRadius){
                		if (!rc.hasMoved() && rc.canMove(treeLoc)){
            				rc.setIndicatorDot(treeLoc, 200, 200, 200);
            				rc.move(treeLoc);
                		}
                	}
    			}
			}*/
			
			/*
			if (rc.getLocation().distanceTo(enemyLocation)>RobotType.SCOUT.sensorRadius-2){
				controllerMovement.tryMove(dirToEnemy,DEFAULT_TURN_ANGLE_DEGREES,DEFAULT_TURN_ATTEMPTS-2,rc); //Move towards the reported target
			}
			if (rc.canFireSingleShot() && rc.getLocation().distanceTo(enemyLocation)<DEFAULT_ATTACK_DISTANCE_SCOUT && controllerCombat.wontHitAllies(dirToEnemy,enemyLocation, rc)){
				rc.setIndicatorLine(rc.getLocation(), enemyLocation, 255, 0, 0);
    			rc.fireSingleShot(dirToEnemy);
			}*/
    		//}
    		
    	}
		
		System.out.println("After Type Specific = "+Clock.getBytecodesLeft());
		
		System.out.println("Sensing Robots");
    	nearbyRobots = rc.senseNearbyRobots();
    	nearbyEnemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemyTeam);
    	
		nearbyTrees = rc.senseNearbyTrees();
    	shook = findMoreTreesWithBullets();
    	
    	for (TreeInfo unshakenTree:unshakenTrees){
    		rc.setIndicatorDot(unshakenTree.location, 30, 30, 30);
    	}
    	
    	//what should i be now?
    	System.out.println("Checking Scout Role");
    	myRole = determineScoutRole(nearbyEnemyRobots, unshakenTrees.size());
    	
    	/*
    	if (Clock.getBytecodesLeft() > 1500){
    		System.out.println("Too close robots = "+Clock.getBytecodesLeft());
        	if (nearbyEnemyRobots.length > 0) { //&& !rc.hasAttacked()
    			System.out.println("I can sense enemy robots");
    			List<MapLocation> tooCloseRobotLocations = new ArrayList<MapLocation>();
    			for (RobotInfo enemyRobot:nearbyEnemyRobots){
    				
    				if (rc.getLocation().distanceTo(enemyRobot.location) < RobotType.SCOUT.sensorRadius-2 && enemyRobot.type != RobotType.GARDENER){
    					if (enemyRobot.type == RobotType.LUMBERJACK && enemyRobot.location.distanceTo(rc.getLocation()) < 3){
    						tooCloseRobotLocations.add(enemyRobot.location);
    					} else if (enemyRobot.type == RobotType.SOLDIER && enemyRobot.location.distanceTo(rc.getLocation()) < 5){
    						tooCloseRobotLocations.add(enemyRobot.location);
    					} else if (enemyRobot.type == RobotType.SCOUT && enemyRobot.location.distanceTo(rc.getLocation()) < 2){
    						tooCloseRobotLocations.add(enemyRobot.location);
    					}
    				}
    			}
    			//if (tooCloseRobotLocations.size() > 0){
    			//	controllerMovement.tryMoveTowardsSafety(tooCloseRobotLocations,rc);
    			//}
    		}
    	}*/
    	
    	if (Clock.getBytecodesLeft() > 0){
	    	System.out.println("Trying to move = "+Clock.getBytecodesLeft());
	    	if (rc.getMoveCount() == 0){
				System.out.println("I haven't moved so I'll try going somewhere");
				
				if (dirToMove != null){
					moved = controllerMovement.tryMove(dirToMove,DEFAULT_TURN_ANGLE_DEGREES,1,placesIveBeen,rc);
				}
	        	if (moved==false){
	        		System.out.println("Moving towards an archon initial location");
	    			//Random rnd = new Random();
	    			//int enemyArchonCount = rc.getInitialArchonLocations(rc.getTeam().opponent()).length;
	    			//int randomNumber = rnd.nextInt(enemyArchonCount);
	    			myPatrolDirection = rc.getLocation().directionTo(rc.getInitialArchonLocations(rc.getTeam().opponent())[rc.readBroadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT)]);//utilFunc.randomDirection();
	    			boolean hasMovedTowardsArchon = false;
	    			if (myPatrolDirection != null){
	    				moved = controllerMovement.tryMove(myPatrolDirection,DEFAULT_TURN_ANGLE_DEGREES,2,placesIveBeen,rc);
	    			}
	    			if (!hasMovedTowardsArchon){
	    				moved = controllerMovement.tryMove(utilityFunctions.randomDirection(),DEFAULT_TURN_ANGLE_DEGREES,2,placesIveBeen,rc);
	    			}
	        	}
	        	if (moved){
	        		//System.out.println("Adding location to placesIveBeen");
	        		//MapLocation imHere = new MapLocation((float) (Math.round(rc.getLocation().x*100.0)/100.0),(float) (Math.round(rc.getLocation().y*100.0)/100.0));
	        		//placesIveBeen.add(imHere);
	        	}
			} 
    	}
    	
    	
    	if (Clock.getBytecodesLeft() > 1500){
    		System.out.println("Trying to shoot = "+Clock.getBytecodesLeft());
        	if (nearbyEnemyRobots.length > 0 && !rc.hasAttacked()){
    			System.out.println("There are enemies near me and I haven't attacked");
    			RobotInfo robotToAttack = controllerCombat.getBestRobotToAttack(nearbyEnemyRobots, rc);
    			Direction dirToEnemy = rc.getLocation().directionTo(robotToAttack.location);
    			if (rc.canFireSingleShot() && controllerCombat.wontHitAllies(dirToEnemy,robotToAttack.location, rc)){ //assuming 30 will miss myself  // && thePatrolDirection.degreesBetween(dirToEnemy) > 30
    				rc.setIndicatorLine(rc.getLocation(), robotToAttack.location, 255, 0, 0);
        			rc.fireSingleShot(dirToEnemy);
        		}// else if (dirToEnemy != null && !rc.hasMoved()){
        		//	controllerMovement.tryMove(dirToEnemy,DEFAULT_TURN_ANGLE_DEGREES,DEFAULT_TURN_ATTEMPTS-2,rc);
        		//}
    		}
    	}
    	
    	if (!shook){
    		turnsWithoutShaking++;
    		if (turnsWithoutShaking > 15 && myRole == "Gatherer"){
    			unshakenTrees = new ArrayList<TreeInfo>();
    			myRole = "";
    		}
    	}
    	
    	int archonLocationToVisit = rc.readBroadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT);
    	if (archonLocationToVisit <= theirInitialArchonLocations.length-1){
    		if (rc.getLocation().distanceTo(theirInitialArchonLocations[archonLocationToVisit]) < 3 && nearbyEnemyRobots.length == 0) {
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
    	
    	if (!moved){
        	moved = controllerMovement.tryMove(anExploreDirection,placesIveBeen,rc);
        }
        if (!moved){
        	anExploreDirection = utilityFunctions.randomDirection();
        }
    	
    		
    	   	
    }

	private boolean findMoreTreesWithBullets() throws GameActionException {
		boolean shook = false;
		System.out.println("Checking nearby trees");
		System.out.println("StartByteCodes1 = "+Clock.getBytecodesLeft());
		int i=nearbyTrees.length;
		if (i>6){
			i=6;
		}
    	for (int j=0;j<i;j++){//TreeInfo aTree:nearbyTrees){
    		TreeInfo aTree = nearbyTrees[j];
    		if (rc.canShake(aTree.location)){// && aTree.containedBullets > 0){
    			System.out.println("Shaking");
    			rc.setIndicatorDot(aTree.location, 255, 255, 255);
    			if (rc.canSenseTree(aTree.ID) && rc.senseTree(aTree.ID).containedBullets > 0){
    				rc.shake(aTree.location);
        			shook = true;
    			}
    			
    			unshakenTrees.remove(aTree);
    		} else if (aTree.containedBullets == 0) {
    			unshakenTrees.remove(aTree);
    		}else if (aTree.containedBullets > 0 && !unshakenTrees.contains(aTree)) {
    			unshakenTrees.add(aTree);
    		}/*else if (aTree.containedBullets > 0 && unshakenTrees.contains(aTree)) {
    			System.out.println("Already have this tree but I can't shake it yet");
    		}else {
    			System.out.println("Unhandled tree shaking");
    			System.out.println("Tree Loc:"+aTree.location.x+" "+aTree.location.y);
    			System.out.println("Tree bullets:"+aTree.getContainedBullets());
    			unshakenTrees.remove(aTree);
    			//unshakenTrees.clear();
    		}*/
    	}
    	System.out.println("EndByteCodes1 = "+Clock.getBytecodesLeft());
		return shook;
	}

	private TreeInfo getBestUnharvestedTree(List<TreeInfo> unshakenTrees) throws GameActionException {
		System.out.println("StartByteCodes2 = "+Clock.getBytecodesLeft());
		TreeInfo nearestTree = null;
		float distanceToNearestTree = -1;
		MapLocation myLocation = rc.getLocation();
		if (unshakenTrees.size() > 0){
			distanceToNearestTree = myLocation.distanceTo(unshakenTrees.get(0).location);
			nearestTree = unshakenTrees.get(0);
		}
    	for (TreeInfo tree:unshakenTrees){
    		
    		System.out.println("Tree:"+tree.getLocation());
    		float testDistance = myLocation.distanceTo(tree.location);
			if (testDistance < distanceToNearestTree && !theBadTrees.contains(tree.ID)){
				System.out.println("Tree is nearest:"+testDistance);
				distanceToNearestTree = testDistance;
				nearestTree = tree;
				if (distanceToNearestTree < 1.5){
					if (rc.canSenseTree(tree.getID())){
						TreeInfo theTree = rc.senseTree(tree.getID());
						if (theTree.getContainedBullets() > 0){
							System.out.println("EndBytecodes2 = "+Clock.getBytecodesLeft());
							return nearestTree;
						} else if (theTree.getContainedBullets() == 0){
							System.out.println("Trying to work with a bad tree");
							theBadTrees.add(theTree.ID);
						}
					}
					
				}
			}
		}
    	System.out.println("EndBytecodes2 = "+Clock.getBytecodesLeft());
		return nearestTree;
	}

	private String determineScoutRole(RobotInfo[] nearbyEnemyRobots, int unshakenTreeCount) throws GameActionException {
		String scoutRole = "";
		int isThereAScoutLeader = rc.readBroadcast(CHANNEL_SCOUT_LEADER);
		
		int howManyEnemiesCanISee = nearbyEnemyRobots.length;
		System.out.println("Nearby Enemies:"+howManyEnemiesCanISee);
		System.out.println("Unshaken Trees:"+unshakenTreeCount);
		
		if (unshakenTreeCount > 0){
			System.out.println("I'm Gathering");
			scoutRole = "Gatherer";
			amIALeader = false;
		} else if (amIALeader && howManyEnemiesCanISee > 0) {
			System.out.println("I'm the scout leader");
			scoutRole = "Leader";
			rc.broadcast(CHANNEL_SCOUT_LEADER, rc.getRoundNum());
		} else if (amIALeader && howManyEnemiesCanISee == 0) {
			System.out.println("Passing on the matrix of leadership");
			amIALeader = false;
			scoutRole = "";
		} else if (isThereAScoutLeader >= rc.getRoundNum()-1) {
			System.out.println("There is a scout leader");
			amIALeader = false;
			scoutRole = "Follower";
		} else if (isThereAScoutLeader < rc.getRoundNum()-1 && nearbyEnemyRobots.length > 0) {
			System.out.println("There was a Scout Leader in the past and I can see enemies");
			amIALeader = true;
			scoutRole = "Leader";
			rc.broadcast(CHANNEL_SCOUT_LEADER, rc.getRoundNum());
		}  else if (isThereAScoutLeader < rc.getRoundNum()-1 && nearbyEnemyRobots.length == 0) {
			System.out.println("There was a Scout Leader in the past but I can't see any enemies");
			amIALeader = false;
			scoutRole = "";
		} else {
			System.out.println("--There was an unhandled Scout Leader determination.--");
			scoutRole = "";
		}
		return scoutRole;
	}
}
