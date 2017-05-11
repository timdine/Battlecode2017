package stoogePlayer2;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;



public class robotArchon implements baseRobot {
	
	int turnLastBuiltGardener = -1;
    int gardenerExtendedCooldown = 40;
    int nearbyGardenerCount = 0;
    int currentRoundNum = -1;
    
    int wantGardeners = -1;
    int wantLumberjacks = -1;
    
    Team myTeam = null;
    Team enemyTeam = null;
    
    MapLocation[] myInitialArchonLocations = null;
    MapLocation[] theirInitialArchonLocations = null;
    
    int howManyArchonsDoWeHave = -1;

    int creationTurn = -1;
    
    List<MapLocation> placesIveBeen = null;
    
    RobotInfo[] nearbyEnemyRobots = null;
    
    boolean needsHelp = false;
    
    float lastTurnHP = -1;
	
	public robotArchon() { 
	} 

	@Override
	public void InitializeClassSpecific(RobotController rc) throws GameActionException{
		System.out.println("In the archon initialize");
		
		myTeam = rc.getTeam();
		enemyTeam = rc.getTeam().opponent();
		myInitialArchonLocations = rc.getInitialArchonLocations(myTeam);
		theirInitialArchonLocations = rc.getInitialArchonLocations(enemyTeam);
		howManyArchonsDoWeHave = myInitialArchonLocations.length;
		
		rc.broadcast(CHANNEL_ARCHON_LOCATION_TO_VISIT, 0);
		
		creationTurn = rc.getRoundNum();
		
		placesIveBeen = new ArrayList<MapLocation>();
		
		nearbyEnemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemyTeam);
		
		utilityFunctions.setInitialMapExtents(myInitialArchonLocations, theirInitialArchonLocations, rc);
		
		float distanceToNearestArchon = utilityFunctions.findDistanceBetween(myInitialArchonLocations, theirInitialArchonLocations, "nearest");
        float distanceToFurthestArchon = utilityFunctions.findDistanceBetween(myInitialArchonLocations, theirInitialArchonLocations, "furthest");
        System.out.println("Distance to nearest archon:"+distanceToNearestArchon);
        System.out.println("Distance to furthest archon:"+distanceToFurthestArchon);
		
        int nearbyTreeCount = rc.senseNearbyTrees(5, Team.NEUTRAL).length;
    	System.out.println("There are this many trees:"+nearbyTreeCount);
    	//if there are too many trees around me then I need some lumberjacks here
    	
    	//if there are enemies far, take time
    	//if there are enemies close, soldier rush
    	//if there are enemies very close lumberjack rush
    	
    	//if need gardeners and have excess bullets build gardeners
    	
    	
    	if (distanceToNearestArchon < DEFAULT_THRESHOLD_CLOSE_PROXIMITY){
	    	System.out.println("There are archons close together, soldier rush!");
			rc.broadcast(CHANNEL_STRATEGY, 10); //10 = soldier rush
			//rc.broadcast(CHANNEL_STRATEGY, 20); //20 = lumberjack rush
			//must get units early to kill off their gardeners / archon
			//lumberjacks?  Two early lumberjacks wipes me out!
			//lumberjack swarm, check if the map isn't dense?
		} else {
			System.out.println("Archons are far apart");
			//we have time to get a farm established.  
		}
		if (distanceToFurthestArchon > DEFAULT_THRESHOLD_LARGE_MAP){
			System.out.println("The map is pretty big based on archon spacing");
			//send the initial gardeners away from the enemy archons?		} else {
			//start the initial scout on a direction perpendicular to the initial enemy archons?
			//use the scouts to determine if the map is tree dense or not.
			//influence the building of lumberjacks and the distance farmers move from archons

			System.out.println("The map isn't very big based on archon spacing");
			//	lumberjack rush?
		}
    	
    	
		lastTurnHP = rc.getHealth();

	}
	
	@Override
	public void RunRoundClassSpecific(RobotController rc) throws GameActionException{
    	
		//is there space around me to build?
		currentRoundNum = rc.getRoundNum();
		
		boolean moved = false;
		
		Direction initialArchonLocation = rc.getLocation().directionTo(theirInitialArchonLocations[0]);//.opposite(); //trying building towards the enemy instead of away
    	Direction buildDirection = utilityFunctions.canBuildAdjacent(RobotType.GARDENER,initialArchonLocation,rc);
    	System.out.println("Archon Build Direction:"+buildDirection);
    	
    	RobotInfo allRobots[] = rc.senseNearbyRobots();
    	nearbyGardenerCount = utilityFunctions.countNearbyRobotsFor(RobotType.GARDENER,myTeam, allRobots);
    	
    	nearbyEnemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemyTeam);
    	
    	TreeInfo[] nearbyNeutralTrees = rc.senseNearbyTrees(8, Team.NEUTRAL);
    	System.out.println("There are this many nearby neutral trees:"+nearbyNeutralTrees.length);

		if (iWantAGardener(nearbyGardenerCount, rc)){
			if (buildDirection != null){
	    		if (rc.canHireGardener(buildDirection)) {
	            	System.out.println("Hiring a gardener");
	            	rc.hireGardener(buildDirection);
	                turnLastBuiltGardener = currentRoundNum;
	                if (rc.readBroadcast(CHANNEL_HAS_BUILT_INITIAL_GARDENER) != 999){
	                	rc.broadcast(CHANNEL_HAS_BUILT_INITIAL_GARDENER,999);
	                }
	                
	            }
	    	}
		}
		
		if (iWantALumberjack(rc, nearbyNeutralTrees.length) || currentRoundNum < DEFAULT_INITIAL_LUMBERJACK_TURNS){ //just keep asking for lumberjacks until at least round 150
			System.out.println("There are enough neutral trees to want a lumberjack:"+nearbyNeutralTrees.length);
			rc.broadcast(CHANNEL_WANT_LUMBERJACKS, currentRoundNum);
			if (nearbyNeutralTrees.length > 0){
				MapLocation nearestTreeLoc = findNearestTreeLoc(rc, nearbyNeutralTrees);
				rc.broadcast(CHANNEL_LUMBERJACK_TARGET, utilityFunctions.convertMapLocationToInt(nearestTreeLoc));
				rc.broadcast(CHANNEL_LUMBERJACK_TARGET_TURN, currentRoundNum);
			}
		}
		
		nearbyEnemyRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemyTeam);
		if (Clock.getBytecodesLeft() > 1500){
    		System.out.println("Too close robots = "+Clock.getBytecodesLeft());
        	if (nearbyEnemyRobots.length > 0) { //&& !rc.hasAttacked()
    			System.out.println("I can sense enemy robots, I need to run away!!");
    			List<MapLocation> tooCloseRobotLocations = new ArrayList<MapLocation>();
    			for (RobotInfo enemyRobot:nearbyEnemyRobots){
    				
    				if (rc.getLocation().distanceTo(enemyRobot.location) < RobotType.ARCHON.sensorRadius-2 && enemyRobot.type != RobotType.GARDENER){
    					if (enemyRobot.type == RobotType.LUMBERJACK && enemyRobot.location.distanceTo(rc.getLocation()) < 3){
    						tooCloseRobotLocations.add(enemyRobot.location);
    					} else if (enemyRobot.type == RobotType.SOLDIER && enemyRobot.location.distanceTo(rc.getLocation()) < 5){
    						tooCloseRobotLocations.add(enemyRobot.location);
    					}  else if (enemyRobot.type == RobotType.TANK && enemyRobot.location.distanceTo(rc.getLocation()) < 5){
    						tooCloseRobotLocations.add(enemyRobot.location);
    					}else if (enemyRobot.type == RobotType.SCOUT && enemyRobot.location.distanceTo(rc.getLocation()) < 2){
    						tooCloseRobotLocations.add(enemyRobot.location);
    					}
    				}
    			}
    			if (tooCloseRobotLocations.size() > 0){
    				moved = controllerMovement.tryMoveTowardsSafety(tooCloseRobotLocations,null, rc);
    				if (!moved){
    					int intLocation = utilityFunctions.convertMapLocationToInt(rc.getLocation());
    					int helpDemand = 100;
    					controllerBroadcast.callForHelpIfRequired(rc, currentRoundNum, intLocation, helpDemand);
    				}
    			}
    		}
    	}
		
		if (lastTurnHP > rc.getHealth()){
    		lastTurnHP = rc.getHealth();
    		needsHelp = true;
    	}
		
		if (needsHelp){
    		int helpDemand = 999;
    		controllerBroadcast.callForHelpIfRequired(rc, currentRoundNum, utilityFunctions.convertMapLocationToInt(rc.getLocation()), helpDemand);
    		if (rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent()).length==0){
    			needsHelp = false;
    		}
    	}
	}

	private MapLocation findNearestTreeLoc(RobotController rc, TreeInfo[] nearbyNeutralTrees) {
		float shortestDistance = 999;
		TreeInfo theNearestTree = null;
		MapLocation myLocation = rc.getLocation();
		for (TreeInfo theTree: nearbyNeutralTrees){
			if (myLocation.distanceTo(theTree.location) < shortestDistance){
				shortestDistance = myLocation.distanceTo(theTree.location);
				theNearestTree = theTree;
			}
		}
		return theNearestTree.location;
	}

	private boolean iWantALumberjack(RobotController rc, int nearbyTreeCount) {
		if (nearbyTreeCount > 0){
			System.out.println("I want a lumberjack");
			return true;
		}
		return false;
	}

	private boolean iWantAGardener(int nearbyGardenerCount, RobotController rc) throws GameActionException {
		System.out.println("There are this many gardeners near me:"+nearbyGardenerCount);
		boolean areThereFewEnoughGardenersClose = nearbyGardenerCount < DEFAULT_INITIAL_GARDENER_COUNT;
    	int turnToBuildNextGardener = turnLastBuiltGardener + gardenerExtendedCooldown;
    	boolean isThisRoundTimeToBuildAGardener = currentRoundNum > turnToBuildNextGardener;
    	
    	int hasHiriedInitialGardener = rc.readBroadcast(CHANNEL_HAS_BUILT_INITIAL_GARDENER);
    	int hasBuiltInitialSoldier = rc.readBroadcast(CHANNEL_HAS_BUILT_INITIAL_SOLDIER);
    	
    	if (hasHiriedInitialGardener == 999 && hasBuiltInitialSoldier != 999){
    		return false;
    	}
    	
		if (areThereFewEnoughGardenersClose && ((turnLastBuiltGardener == -1) || isThisRoundTimeToBuildAGardener)) {
			System.out.println("I want Gardeners!");
    		return true;
    	} else {
    		System.out.println("I don't want to hire a gardener right now.");
    	}
		return false;
	}	
}
