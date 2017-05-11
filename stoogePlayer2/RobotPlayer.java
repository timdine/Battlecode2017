package stoogePlayer2;
//import java.awt.Toolkit;
import battlecode.common.*;




/*
	    	   _//)_//)
	    	  / /)=/ /)
    		 ((O)=(O))
    			\||/
	    ________====____[o]_ 
	)/)|___._==      ==_.___|(\(
	(( \ || '-.________.-' || / ))
	\=/ ||     ..''..     || \=/
     \\_//    / [||] \    \\_//
	  \V/    / ...... \    \V/
			 \::::::::/
	   _____.---'  '---._____
	  |_-_-_|__------__|_-_-_|
	  |_-_-_|=        =|_-_-_|
	  |_-_-_|          |_-_-_|
	  
	  Remember Number 5 (Johnny 5)

*/

public strictfp class RobotPlayer{
    static RobotController rc;

    //@SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
    	 System.out.println(rc.getType());
    	 baseRobot robot;
    	 
    	  switch (rc.getType()) { 
    	  case ARCHON: 
    		  System.out.println("Prearchon");
    		  robot = new robotArchon();
    		  System.out.println("PostArchon");
    	   break; 
    	  case GARDENER: 
    		  robot = new robotGardener(); 
    	   break; 
    	  case SOLDIER: 
    		  robot = new robotSoldier(); 
    	   break; 
    	  // case HQ: 
    	  case SCOUT: 
    		  robot = new robotScout(); 
       	   break; 
    	  case TANK: 
    		  robot = new robotTank(); 
       	   break; 
    	  case LUMBERJACK: 
    		  robot = new robotLumberjack();
       	   break; 
       	   default:
       		robot = new robotArchon();//make a generic robot and put it here so it doesn't blow up
       		   break;
    	  } 
    	  robot.run(rc);
    } 
        
}
