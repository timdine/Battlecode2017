package stoogePlayer2;

import battlecode.common.*;

public interface baseRobot {
	

	static ControllerBroadcast controllerBroadcast = new ControllerBroadcast();
	static ControllerCombat controllerCombat = new ControllerCombat();
	static ControllerMovement controllerMovement = new ControllerMovement();
	static ControllerIndicators controllerIndicators = new ControllerIndicators();
	static UtilityFunctions utilityFunctions = new UtilityFunctions();
	
	//Comunication Channels
    static int CHANNEL_COUNT_GARDENER = 100;
    static int CHANNEL_COUNT_LUMBERJACK = 101;
    static int CHANNEL_COUNT_TANK = 102;
    static int CHANNEL_COUNT_SCOUT = 103;
    static int CHANNEL_COUNT_ARCHON = 104;
    
    static int CHANNEL_COUNT_GARDENER_BUILDER = 150;
    static int CHANNEL_COUNT_GARDENER_TURN = 151;
    
    static int CHANNEL_MAP_MINX = 200;
    static int CHANNEL_MAP_MAXX = 201;
    static int CHANNEL_MAP_MINY = 202;
    static int CHANNEL_MAP_MAXY = 203;
    static int CHANNEL_MAP_MINX_FOUND = 204;
    static int CHANNEL_MAP_MAXX_FOUND = 205;
    static int CHANNEL_MAP_MINY_FOUND = 206;
    static int CHANNEL_MAP_MAXY_FOUND = 207;
    
    //Channels 250-253 allocated to controller broadcast
    
    static int CHANNEL_TO_MASS_LOCATION = 301;
    static int CHANNEL_TO_MASS_TURN = 302;
    static int CHANNEL_TO_ATTACK_LOCATION = 303;
    static int CHANNEL_TO_ATTACK_TURN = 304;
    
    static int CHANNEL_STRATEGY = 900;
    static int CHANNEL_CENSUS_TURNS = 901;
    
    static int CHANNEL_ARCHON_LEAD = 951;
    static int CHANNEL_GARDENER_AIRPORT = 952;
    static int CHANNEL_SCOUT_LEADER = 953;
    
    static int CHANNEL_CLAIM_INITIAL_SOLDIER = 972;
    static int CHANNEL_HAS_BUILT_INITIAL_SOLDIER = 973;
    static int CHANNEL_INITIAL_SOLDIER_REACHED_TARGET = 974;
    static int DEFAULT_INITIAL_SOLDIER_COUNT = 2;
    
    static int CHANNEL_HAS_BUILT_INITIAL_SCOUT = 975;
    static int DEFAULT_INITIAL_SCOUT_COUNT = 2;
    
    static int CHANNEL_HAS_BUILT_INITIAL_TANK = 976;
    static int DEFAULT_INITIAL_TANK_COUNT = 977;
    
    static int CHANNEL_HAS_BUILT_INITIAL_GARDENER = 978;
    static int DEFAULT_INITIAL_GARDENER_COUNT = 5;
    
    
    
    static int CHANNEL_WANT_LUMBERJACKS = 979;
    static int CHANNEL_LUMBERJACK_TARGET = 980;
    static int CHANNEL_LUMBERJACK_TARGET_TURN = 981;
    static int DEFAULT_INITIAL_LUMBERJACK_TURNS = 150;
    
    static int CHANNEL_ARCHON_LOCATION_TO_VISIT = 990;
    
    static int DONATE_THRESHOLD = 1000;
    static int DONATE_BULLETS_TO_DONATE = 100;
    static int BUILDER_TURN_DELAY_MOD = 50;
    static int TREE_ANGLES_TO_PLANT = 6;
    
    static int DEFAULT_TURN_ANGLE_DEGREES = 20;
    static int DEFAULT_TURN_ATTEMPTS = 8;
    
    static int DEFAULT_ATTACK_DISTANCE_SCOUT = 6;
    
    static int DEFAULT_BUILD_ANGLE_DEGREES = 22;
    static int DEFAULT_BUILD_TURN_ATTEMPTS = 9;
    
    static int DEFAULT_THRESHOLD_CLOSE_PROXIMITY = 10;
    static int DEFAULT_THRESHOLD_LARGE_MAP = 75;

    static int DEFAULT_THRESHOLD_GARDENERCOOLDOWN_BULLETS = 500;
    static int DEFAULT_THRESHOLD_GARDENERCOOLDOWN_ROUNDS = 200;

    static float[] possibleDirections = new float[] {0,20,-20,40,-40,60,-60,80,-80,100,-100,120,-120,140,-140,160,-160,180}; //45,-45,90,-90,135,-135,180,-180,225,-225,270,-270,315,-315,360
    static float[] possibleBuildDirections =  new float[] {0,60,-60,120,-120,180};
    //List<MapLocation> moneylessTrees = new ArrayList<MapLocation>();
	
	public default void InitializeRobot(RobotController rc) throws GameActionException {
		
		System.out.println("In the baseRobot initialize");
		System.out.println("I'm an "+rc.getType()+"!");
		InitializeClassSpecific(rc);
	}

	public default void InitializeClassSpecific(RobotController rc) throws GameActionException {
		// generic method to be overwritten by the classes of robot
	}

	public default void RunRound(RobotController rc) throws GameActionException {
		//currentRoundNum = rc.getRoundNum();
		RunRoundClassSpecific(rc);
	}

	public default void RunRoundClassSpecific(RobotController rc) throws GameActionException {
		// generic method to be overwritten by the classes of robot
		
	}

	public default void run(RobotController rc) throws GameActionException{
		System.out.println("Begin Base Robot");
		InitializeRobot(rc);
        //for (;;Clock.yield()) {
		while(true){
            try{
            RunRound(rc);
            // ... maybe do some other stuff
            } catch (Exception e) {
            	e.printStackTrace();
            }
            
            utilityFunctions.tradeInBulletsIfAGoodIdea(rc);
            System.out.println("Remaining bytecodes:"+Clock.getBytecodesLeft());
            Clock.yield();
        }
	}
}
