package stoogePlayer2;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;


public class robotGardener implements baseRobot {

	static boolean amIAnAirport = false;
	static boolean hasBuiltInitialScout = false;
	boolean initialMoving = true;
	
	Random rnd = new Random();
	int initialDistance = rnd.nextInt(5)+5;
	int turnsWandering = 0;
	Direction travelDirection = null;
	
	int howManyTreesWillIBuild = TREE_ANGLES_TO_PLANT;
	
	String myRole = "";
	   
    Team myTeam = null;
    Team enemyTeam = null;
    
    MapLocation[] myInitialArchonLocations = null;
    MapLocation[] theirInitialArchonLocations = null;
    
    int howManyArchonsDoWeHave = -1;

    int creationTurn = -1;
    
    RobotController rc = null;
    
    boolean needsHelp = false;
    
    float lastTurnHP = -1;

	public robotGardener() {
	}
	
	public void InitializeClassSpecific(RobotController rc2) throws GameActionException{
		System.out.println("In the gardener initialize");
		rc = rc2;
		myTeam = rc.getTeam();
		enemyTeam = rc.getTeam().opponent();
		myInitialArchonLocations = rc.getInitialArchonLocations(myTeam);
		theirInitialArchonLocations = rc.getInitialArchonLocations(enemyTeam);
		howManyArchonsDoWeHave = myInitialArchonLocations.length;
		creationTurn = rc.getRoundNum();
		travelDirection = utilityFunctions.randomDirection();
		lastTurnHP = rc.getHealth();
	}

	
	public void RunRoundClassSpecific(RobotController rc) throws GameActionException{
		
		int currentTurn = rc.getRoundNum();

		checkForAndBuildInitialUnit(rc);
		checkForAndBuildInitialSoldier(rc);
		checkIfINeedAndCanBuildLumberjack(rc);
		checkIfWeNeedSoldiers(rc); 
		
		//preferred initial direction perpendicular to direction to enemy archons, alternate directions.
		
		System.out.println("Checking health:"+lastTurnHP+" "+rc.getHealth());
    	if (lastTurnHP > rc.getHealth()){
    		lastTurnHP = rc.getHealth();
    		needsHelp = true;
    	}
    	System.out.println("Do I need help:"+needsHelp);
    	if (needsHelp){
    		int helpDemand = 999;
    		controllerBroadcast.callForHelpIfRequired(rc, currentTurn, utilityFunctions.convertMapLocationToInt(rc.getLocation()), helpDemand);
    		buildAdjacent(rc, RobotType.SOLDIER);	
    		if (rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent()).length==0){
    			needsHelp = false;
    		}
    	}
		
		if (initialMoving){
    		System.out.println("Initial Moving:"+initialDistance);
    		boolean hasMovedFarEnough = false;
        	boolean farEnoughFromOtherRobots = true;
        	boolean farEnoughFromMyTrees = false;
        	turnsWandering++;
        	if (turnsWandering % 30 == 0){ //if you wander too long settle for a spot where you can build less trees.
        		howManyTreesWillIBuild--;
        		if (howManyTreesWillIBuild < 3){
        			howManyTreesWillIBuild = 3;
        		}
        		travelDirection = utilityFunctions.randomDirection();
        	}
        	
        	if (currentTurn > (creationTurn + initialDistance)){
        		hasMovedFarEnough = true;
        	}
        	
        	if (rc.senseNearbyRobots((float) (rc.getType().bodyRadius*4.5)).length > 0){ //5 gives nice spacing, trying lower
        		farEnoughFromOtherRobots = false;
        		//if (turnsWandering % 20 == 0) {
        		//	travelDirection = travelDirection;//utilityFunctions.randomDirection();
        		//}
        	}
        	
        	if (rc.senseNearbyTrees(rc.getType().bodyRadius*3,rc.getTeam()).length > 2){
        		farEnoughFromMyTrees = false;
        		//if (turnsWandering % 20 == 0) {
        		//	travelDirection = travelDirection;//utilityFunctions.randomDirection();
        		//}
        	} else {
        		farEnoughFromMyTrees = true;
        	}
        	
        	boolean canBuildEnoughTrees = canBuildTreesAroundMe(howManyTreesWillIBuild); //tried this with -1
        	
        	System.out.println("Has moved Enough:"+hasMovedFarEnough);
        	System.out.println("Can build Enough:"+canBuildEnoughTrees);
        	System.out.println("Is far Enough from robots:"+farEnoughFromOtherRobots);
        	System.out.println("Is far Enough from trees:"+farEnoughFromMyTrees);
        	
        	if (!hasMovedFarEnough || !farEnoughFromOtherRobots || !farEnoughFromMyTrees) //|| !canBuildEnoughTrees 
        	{ //add a check to see if too close to other gardeners
        		System.out.println("Trying to move in block 1");
        		if (travelDirection != null){
        			controllerMovement.tryMove(travelDirection,DEFAULT_TURN_ANGLE_DEGREES,DEFAULT_TURN_ATTEMPTS,null,rc);
        		}
        		Clock.yield();
        		//continue;
        	} else {
        		initialMoving = false;
        	}
    	}  else {
    		System.out.println("Main Block");
    		myRole = determineGardenerRole();
    		//myRole = "Farmer";
    		           	
        	switch(myRole) {
            case "shakeNearbyTrees" :
            	//reallocating this role to scouts.
            	/*
            	TreeInfo theNearbyTrees[];
            	boolean shookATree = false;
                theNearbyTrees = rc.senseNearbyTrees(rc.getType().bodyRadius,rc.getTeam());
                
                if (theNearbyTrees.length > 0 && theNearbyTrees[0].getContainedBullets() == 0) {
                	System.out.println("There are nearby trees");
                	System.out.println(theNearbyTrees[0].getContainedBullets());
                	System.out.println(theNearbyTrees[0].getContainedRobot());
                	for (TreeInfo tree: theNearbyTrees){
                		if (!(moneylessTrees.contains(tree.getLocation()))){
                			if (tree.getContainedBullets() == 0) {
                				moneylessTrees.add(tree.getLocation());
                			} else if (theNearbyTrees[0].getContainedBullets() > 0 && rc.canShake(theNearbyTrees[0].getLocation())){
                        		System.out.println("Shaking");
                        		rc.shake(theNearbyTrees[0].getLocation());
                        		shookATree = true;
                        	}
                		}
                	}
                } else {
                	controllerMovement.tryMove(utilityFunctions.randomDirection(),DEFAULT_TURN_ANGLE_DEGREES,DEFAULT_TURN_ATTEMPTS, rc);
                	Clock.yield();
                }
                
                if (!shookATree){
                	controllerMovement.tryMove(utilityFunctions.randomDirection(),DEFAULT_TURN_ANGLE_DEGREES,DEFAULT_TURN_ATTEMPTS, rc);
                	Clock.yield();
                }*/
               break; // optional 
            case "buildTreesAroundArchon" : 
                // Statements
            	//gardener check message queue
            	//if archon is not surrounded by 5 trees build a pattern of trees around that location
                break; // optional 
            case "buildLineOfTrees" :
                // Statements
            	//get location we want to build from
            	//get the direction we want to build from
            	//can we find an existing line of trees 
            	//data structure that draws connecting lines between the trees establishing walls
                break; // optional 
            case "Farmer" :
            	tendLot(); //tending lot I can't build enough trees in magicwood
                // Statements
                break; // optional
            case "Airport" :
            	System.out.println("Building Scouts");
            	beAnAirport(rc);
                // Statements
                break; // optional 
            case "recruitingOfficer" :
            	buildAdjacent(rc, RobotType.SOLDIER);
            	evaluateAndActOnSafety();
                // Statements
                break; // optional 
            case "lumberJacksAreOK" :
            	buildAdjacent(rc, RobotType.LUMBERJACK);
                // Statements
                break; // optional 
            case "buildTank" :
            	buildAdjacent(rc, RobotType.TANK);
                // Statements
                break; // optional
            case "buildRandomHarvestTrees" :
                // Statements
            	//Not implemented
                break; // optional 
            }
            //Clock.yield();
        	/*
        	boolean needsHelp = false;
        	if (currentTurn > 1000){ //This is here for testing on calling for help
        		needsHelp = true;
        	}*/
        	
    	}
	}

	private void evaluateAndActOnSafety() throws GameActionException {
		RobotInfo[] nearbyRobots = rc.senseNearbyRobots(999, rc.getTeam().opponent());
    	if (nearbyRobots.length > 0 && !rc.hasAttacked()) {
			System.out.println("I can sense enemy robots");
			//am I too close?
			List<MapLocation> tooCloseRobotLocations = new ArrayList<MapLocation>();
			for (RobotInfo enemyRobot:nearbyRobots){
    
				if (rc.getLocation().distanceTo(enemyRobot.location) < RobotType.SCOUT.sensorRadius-2){
					tooCloseRobotLocations.add(enemyRobot.location);
					//this robot is too close to me.
					//add its location into an array
					//move away from the center of mass
					//if there is trees between me and a soldier or lumberjack, don't shoot
					//if there is trees between me and a gardener or archon SHOOT
						//try to find an angle I can hit them.
				}
			}
			if (tooCloseRobotLocations.size() > 0){
				controllerMovement.tryMoveTowardsSafety(tooCloseRobotLocations,null,rc);
			}
		}
	}

	private boolean buildAdjacent(RobotController rc, RobotType theType) throws GameActionException {
		Direction initialDirection = utilityFunctions.randomDirection(); //seed random direction with my robotid
    	for (int i=0;i<baseRobot.possibleDirections.length;i++){
    		Direction attemptBuildDirection = initialDirection.rotateLeftDegrees(baseRobot.possibleDirections[i]);
    		if (rc.canBuildRobot(theType, attemptBuildDirection)){
    			rc.setIndicatorDot(rc.getLocation().add(attemptBuildDirection), 35, 35, 35);
    			rc.buildRobot(theType, attemptBuildDirection);
    			return true;
    		}
    		if (theType == RobotType.TANK){
				if (rc.canBuildRobot(RobotType.TANK, attemptBuildDirection)){
					rc.buildRobot(RobotType.TANK, attemptBuildDirection);
					return true;
				} else if (rc.canBuildRobot(RobotType.SOLDIER, attemptBuildDirection)){
					rc.buildRobot(RobotType.SOLDIER, attemptBuildDirection);
					return true;
				}
			}
    	}
    	return false;
	}

	private void beAnAirport(RobotController rc) throws GameActionException {
		rc.setIndicatorDot(rc.getLocation(), 0, 0, 0);
		int nearbyTreeCount = rc.senseNearbyTrees(1).length;
		if (nearbyTreeCount < TREE_ANGLES_TO_PLANT) {
			tendLot();
		}
		buildAdjacent(rc, RobotType.SCOUT);
	}

	private void tendLot() throws GameActionException {
		System.out.println("Tending Lot");
		TreeInfo[] nearbyTrees = rc.senseNearbyTrees(2);
		//if there are still places around you to plant trees, do it.
		//should the places be a hex or a grid.
		if (!canBuildTreesAroundMe(TREE_ANGLES_TO_PLANT-2)){
			buildAdjacent(rc, RobotType.LUMBERJACK); //added so that I try to build lumberjacks to help clear space around me even if the archon doesn't need then anymore.
		}
		if (nearbyTrees.length < TREE_ANGLES_TO_PLANT-1){
			System.out.println("There are " + nearbyTrees.length + " trees nearby");
			Direction desiredDirectonToPlant = null;
			boolean plantedTree = false;
			for (int i=0;i<TREE_ANGLES_TO_PLANT;i++){
				desiredDirectonToPlant = Direction.getNorth().rotateLeftDegrees(-180+(360/TREE_ANGLES_TO_PLANT*i));
				rc.setIndicatorDot(rc.getLocation().add(desiredDirectonToPlant), 0, 150, 0);
				//System.out.println("I want to plant a tree at:" + desiredDirectonToPlant.toString());
				if (rc.canPlantTree(desiredDirectonToPlant)){
					//System.out.println("Trying to plant a tree");
					rc.setIndicatorDot(rc.getLocation().add(desiredDirectonToPlant), 0, 255, 0);
					rc.plantTree(desiredDirectonToPlant);
					plantedTree = true;
					System.out.println("Planted a tree  }=<@");
				}
			}
			if (plantedTree){
				Clock.yield();
			}
		}
		//check if there are any trees around you that need watering
		for (int i=0;i<nearbyTrees.length;i++){
			//do self built trees contain robots?
			//System.out.println("Tree of team:" + nearbyTrees[i].team.toString() + " Contains:" + nearbyTrees[i].getContainedRobot().toString() );
			if (nearbyTrees[i].health < nearbyTrees[i].getMaxHealth() * 0.9 && rc.canWater(nearbyTrees[i].location)){
				rc.setIndicatorDot(nearbyTrees[i].location, 0, 0, 255);
				rc.water(nearbyTrees[i].location);
				System.out.println("Watered a tree");
			} else if (rc.canShake(nearbyTrees[i].location) && nearbyTrees[i].containedBullets > 0){
				System.out.println("Shook a tree");
				rc.setIndicatorDot(nearbyTrees[i].location, 255, 255, 255);
				rc.shake(nearbyTrees[i].location);
			}
		}
	}

	private String determineGardenerRole() throws GameActionException {
		String gardenerRole = "";
		int isThereAnAirport = rc.readBroadcast(CHANNEL_GARDENER_AIRPORT);
    	System.out.println("Is there an airport?"+isThereAnAirport+" "+(rc.getRoundNum()-1));
    	System.out.println("Am I an airport?"+amIAnAirport);
    	Direction initialArchonLocation = rc.getLocation().directionTo(theirInitialArchonLocations[0]).opposite();
    	System.out.println("Can I build an adjacent Scout?"+utilityFunctions.canBuildAdjacent(RobotType.SCOUT,initialArchonLocation, rc));

    	int strategy = rc.readBroadcast(CHANNEL_STRATEGY);
    	System.out.println("We have a Strategy:"+strategy);
    	
    	/*
    	if (strategy == 10) {
    		gardenerRole = "recruitingOfficer";
    		return gardenerRole;
    	}*/
    	
  	
    	if (amIAnAirport && utilityFunctions.canBuildAdjacent(RobotType.SCOUT, rc.getLocation().directionTo(theirInitialArchonLocations[0]).opposite(), rc)!=null) {
			System.out.println("I'm already an airport");
			gardenerRole = "Airport";
			rc.broadcast(CHANNEL_GARDENER_AIRPORT, rc.getRoundNum());
		} else if (amIAnAirport && !(utilityFunctions.canBuildAdjacent(RobotType.SCOUT, rc.getLocation().directionTo(theirInitialArchonLocations[0]).opposite(), rc)!=null)) {
			amIAnAirport = false;
		} else if (isThereAnAirport >= rc.getRoundNum()-1) {
			System.out.println("There is already an airport, so I don't need to be");
			amIAnAirport = false;
			gardenerRole = "Farmer";
		} else if (isThereAnAirport < rc.getRoundNum()-1 &&  utilityFunctions.canBuildAdjacent(RobotType.SCOUT, rc.getLocation().directionTo(theirInitialArchonLocations[0]).opposite(), rc)!=null) {
			System.out.println("There was an Airport in the past. and I can build so I'm an airport now");
			amIAnAirport = true;
			gardenerRole = "Airport";
			rc.broadcast(CHANNEL_GARDENER_AIRPORT, rc.getRoundNum());
		} else {
			amIAnAirport = false;
			gardenerRole = "Farmer";
			//There might be an airport or not, or I can't build anything anyway so I'll stay a farmer.
			System.out.println("--There was an unhandled Gardener Airport determination.--");
		}
		return gardenerRole;
	}

	private boolean canBuildTreesAroundMe(int howManyTreesWillIBuild) {
		System.out.println("I want to build some trees around me:"+howManyTreesWillIBuild);
		Direction desiredDirectonToPlant = null;
		int treesYouCouldPlant = 0;
		for (int i=TREE_ANGLES_TO_PLANT;i>0;i--){
			desiredDirectonToPlant = Direction.getNorth().rotateLeftDegrees(-180+(360/(TREE_ANGLES_TO_PLANT)*i));
			//System.out.println("I want to plant a tree at:" + desiredDirectonToPlant.toString());
			if (rc.canPlantTree(desiredDirectonToPlant)){
				treesYouCouldPlant += 1;
			}
		}
		System.out.println("I can plant:"+treesYouCouldPlant);
		if (treesYouCouldPlant >=howManyTreesWillIBuild){
			return true;
		}
		return false;
	}

	private void checkForAndBuildInitialSoldier(RobotController rc2) throws GameActionException {
		
		// TODO Auto-generated method stub
		int hasBuiltInitialSoldier = rc2.readBroadcast(CHANNEL_HAS_BUILT_INITIAL_SOLDIER);
		System.out.print("Checking for Inital Soldier:"+hasBuiltInitialSoldier);
		if (hasBuiltInitialSoldier != 999){ //it's plus one because the initial broadcast array is full of 1s, adding one here prevents someone broadcasting to initialize it to 0
			System.out.println("There is no initial soldier, trying to build one.");
			boolean built = buildAdjacent(rc2, RobotType.SOLDIER);
			if (built){
				rc2.broadcast(CHANNEL_HAS_BUILT_INITIAL_SOLDIER, 999);
			}
		} else {
			System.out.println("Not enough bullets to build a Soldier");
		}
	}
	
	private void checkForAndBuildInitialUnit(RobotController rc) throws GameActionException {
		RobotType theTypeToBuild = null;
		
		int strategy = rc.readBroadcast(CHANNEL_STRATEGY);
		if (strategy == 10){
			System.out.println("Strategy 10");
			theTypeToBuild = RobotType.SOLDIER;
		} else {
			System.out.println("Default Strategy");
			theTypeToBuild = RobotType.SCOUT;
		}
		
		int hasBuiltInitialUnit = rc.readBroadcast(CHANNEL_HAS_BUILT_INITIAL_SCOUT);
		System.out.println("Has built initial unit? "+hasBuiltInitialUnit);
    	
    	if (hasBuiltInitialUnit+1 < DEFAULT_INITIAL_SCOUT_COUNT){ //it's plus one because the initial broadcast array is full of 1s, adding one here prevents someone broadcasting to initialize it to 0
			buildAdjacent(rc, theTypeToBuild);
			rc.broadcast(CHANNEL_HAS_BUILT_INITIAL_SCOUT, hasBuiltInitialUnit+1);
		} else {
			System.out.println("Not enough bullets to build a "+theTypeToBuild.name());
		}
    	 //else if (rc.getTeamBullets() > 250){
 			//buildAdjacent(RobotType.SCOUT);
 		//}
	}
	
	private void tryToBuildSomeLumberjacks(RobotController rc) throws GameActionException {
		System.out.println("I want to build lumberjacks");
		buildAdjacent(rc, RobotType.LUMBERJACK);		
	}

	private boolean doesArchonWantLumberjacks(RobotController rc) throws GameActionException {
		int lumberjackCheck = rc.readBroadcast(CHANNEL_WANT_LUMBERJACKS);
		if (lumberjackCheck > rc.getRoundNum()-1){
			return true;
		}
		return false;
	}
	
	private void checkIfINeedAndCanBuildLumberjack(RobotController rc) throws GameActionException {
		if (doesArchonWantLumberjacks(rc)){ //just keep asking for lumberjacks until at least round 150
			tryToBuildSomeLumberjacks(rc);
		}		
	}
	
	private void checkIfWeNeedSoldiers(RobotController rc2) throws GameActionException {
		if (rc2.readBroadcast(ControllerBroadcast.CHANNEL_SEEN_LAND_BADDIE) == 1000){
			//buildAdjacent(rc2, RobotType.SOLDIER);	
			if (rc.getRoundNum() % 10 == 0){
				buildAdjacent(rc2, RobotType.TANK);	
			}
		} else if (rc.readBroadcast(ControllerBroadcast.CHANNEL_HELP_DEMAND_TURN) >= rc.getRoundNum()-1){
			if (rc.getRoundNum() % 10 == 0){
				buildAdjacent(rc2, RobotType.SOLDIER);	
			}
		}
	}
}

