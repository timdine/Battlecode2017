package stoogePlayer2;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;



public class ControllerMovement {
	ControllerMovement(){
		
	}

	public boolean tryMove(Direction dir, int degreeOffset, int checksPerSide, List<MapLocation>placesIveBeen, RobotController rc) throws GameActionException {
		//draw indicator dots on bullets to dodge
		//draw dots on trees with bullets
		//Scout Rush > Passive Farming  
	     //Soldier Defense > Scout Rush 
		
		boolean moved = false;
		
		if (dir == null){
			return false;
		}
		
		System.out.println("In trymove:"+dir+" "+degreeOffset+" "+checksPerSide);
		List<locationToMoveTo> possibleMoveLocations = new ArrayList<locationToMoveTo>();
		
		if (!rc.hasMoved()){
			/*
			if (rc.getType() == RobotType.SCOUT){
				TreeInfo[] theNearbyTrees = rc.senseNearbyTrees();
	    		int turnToAttackReported = rc.readBroadcast(baseRobot.CHANNEL_TO_ATTACK_TURN);
	        	int locationToAttackInt = rc.readBroadcast(baseRobot.CHANNEL_TO_ATTACK_LOCATION);
	        	
	    		if (turnToAttackReported >= (rc.getRoundNum()-1) && myRole != "Gatherer") { //There is a location to attack
	    			System.out.println("There is a location reported by the leader and I should move there.");
	    			//try to move towards the desired location if far away,
	    			//try to shoot the desired location if close enough
	    			MapLocation enemyLocation = baseRobot.utilityFunctions.convertIntToMapLocation(locationToAttackInt);
	    			//Direction dirToEnemy = rc.getLocation().directionTo(enemyLocation);
	    			for (TreeInfo theTree: theNearbyTrees){
	        			if (theTree.location.distanceTo(enemyLocation) == 1){
	        				System.out.println("Found a tree next to an enemy");
	        				locationToMoveTo testLoc = new locationToMoveTo();
	                    	testLoc.theMapLocation = theTree.location;
	                    	testLoc.weightToAvoidThisLocation = -500;
	                    	possibleMoveLocations.add(testLoc);
	                    	if (rc.getLocation().distanceTo(theTree.location)<rc.getType().strideRadius){
	                    		if (!rc.hasMoved() && rc.canMove(theTree.location)){
	                				rc.setIndicatorDot(theTree.location, 200, 200, 200);
	                				rc.move(theTree.location);
	                				moved = true;
	                    		}
	                    	}
	        			}
	        			if (rc.getLocation() == theTree.location && rc.getLocation().distanceTo(enemyLocation) == 1){
	        				return true;
	        			}
	        		}
	    		}
			}*/
	
			/*
			if (!rc.hasMoved() && rc.canMove(dir) && Clock.getBytecodesLeft() < 100){
				//rc.setIndicatorDot(bestLocation.theMapLocation, 200, 50, 50);
				rc.move(dir);
				return true;
			}*/
			
	        int currentCheck = 1;
	        /*
	        BulletInfo[] theCurrentBullets = rc.senseNearbyBullets();
	        List<MapLocation> placesToAvoidBullets = new ArrayList<MapLocation>();
	        for (BulletInfo bullet:theCurrentBullets){//don't move where a bullet is or where it will be.
	    		MapLocation whereTheBulletIs = bullet.getLocation();
	    		MapLocation whereTheBulletWillBe = bullet.getLocation().add(bullet.getDir(), bullet.getSpeed());
	    		placesToAvoidBullets.add(whereTheBulletIs);
	    		placesToAvoidBullets.add(whereTheBulletWillBe);
	    	}*/
	        
	        /*
	        if (!rc.hasMoved() && rc.canMove(dir) && Clock.getBytecodesLeft() < 100){
				//rc.setIndicatorDot(bestLocation.theMapLocation, 200, 50, 50);
				rc.move(dir);
				return true;
			}*/
	    	
	        if(dir != null && rc.canMove(dir)) {
	        	System.out.println("Checking Initial");
	        	locationToMoveTo testLoc = new locationToMoveTo();
	        	testLoc.theMapLocation = rc.getLocation().add(dir,rc.getType().strideRadius);
	        	testLoc.weightToAvoidThisLocation = 0;
	        	testLoc.weightToAvoidThisLocation += -5;
	        	if (baseRobot.controllerCombat.bulletsWontHitMeThere(testLoc.theMapLocation, new ArrayList<MapLocation>(), rc)){//placesToAvoidBullets
	        		testLoc.weightToAvoidThisLocation += 0;
	        	} else {
	        		testLoc.weightToAvoidThisLocation += 365;
	        	}
	        	MapLocation roundedLocation = new MapLocation((float) (Math.round(testLoc.theMapLocation.x*100.0)/100.0),(float) (Math.round(testLoc.theMapLocation.y*100.0)/100.0));
	        	if (placesIveBeen != null && placesIveBeen.contains(roundedLocation.add(dir))){
	        		System.out.println("Ive been here before");
	        		testLoc.weightToAvoidThisLocation += 500;
	        	}
	        	
	        	
	        	possibleMoveLocations.add(testLoc);
	        }
	        
	        /*
	        if (!rc.hasMoved() && rc.canMove(dir) && Clock.getBytecodesLeft() < 100){
				//rc.setIndicatorDot(bestLocation.theMapLocation, 200, 50, 50);
				rc.move(dir);
				return true;
			}*/
	        
			while(currentCheck<=checksPerSide) {
	            // Try the offset of the left side
	            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
	            	System.out.println("Checking Left");
	            	locationToMoveTo testLoc = new locationToMoveTo();
	            	testLoc.theMapLocation = rc.getLocation().add(dir.rotateLeftDegrees(degreeOffset*currentCheck),rc.getType().strideRadius);
	            	testLoc.weightToAvoidThisLocation = 0;
	            	testLoc.weightToAvoidThisLocation += degreeOffset*currentCheck;
	            	if (baseRobot.controllerCombat.bulletsWontHitMeThere(testLoc.theMapLocation, new ArrayList<MapLocation>(), rc)){
	            		testLoc.weightToAvoidThisLocation += 0;
	            	} else {
	            		testLoc.weightToAvoidThisLocation += 365;
	            	}
	            	MapLocation roundedLocation = new MapLocation((float) (Math.round(testLoc.theMapLocation.x*100.0)/100.0),(float) (Math.round(testLoc.theMapLocation.y*100.0)/100.0));
		        	if (placesIveBeen != null && placesIveBeen.contains(roundedLocation.add(dir))){
		        		System.out.println("Ive been here before");
		        		testLoc.weightToAvoidThisLocation += 500;
		        	}
	            	possibleMoveLocations.add(testLoc);
	            }
	            // Try the offset on the right side
	            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
	            	System.out.println("Checking Right");
	            	locationToMoveTo testLoc = new locationToMoveTo();
	            	testLoc.theMapLocation = rc.getLocation().add(dir.rotateLeftDegrees(degreeOffset*currentCheck),rc.getType().strideRadius);
	            	testLoc.weightToAvoidThisLocation = 0;
	            	testLoc.weightToAvoidThisLocation += degreeOffset*currentCheck;
	            	if (baseRobot.controllerCombat.bulletsWontHitMeThere(testLoc.theMapLocation, new ArrayList<MapLocation>(), rc)){
	            		testLoc.weightToAvoidThisLocation += 0;
	            	} else {
	            		testLoc.weightToAvoidThisLocation += 365;
	            	}
	            	MapLocation roundedLocation = new MapLocation((float) (Math.round(testLoc.theMapLocation.x*100.0)/100.0),(float) (Math.round(testLoc.theMapLocation.y*100.0)/100.0));
		        	if (placesIveBeen != null && placesIveBeen.contains(roundedLocation.add(dir))){
		        		System.out.println("Ive been here before");
		        		testLoc.weightToAvoidThisLocation += 500;
		        	}
	            	possibleMoveLocations.add(testLoc);
	            }
	            // No move performed, try slightly further
	            currentCheck++;
	        }
			
			
			while (!moved && possibleMoveLocations.size() > 0){ //if angle to shoot at enemy crosses trees it's worse.
				locationToMoveTo bestLocation = null;
	    		System.out.println("Checking for the best location in the list");
	    		for (locationToMoveTo bestLocationTest:possibleMoveLocations){
	    			if (bestLocation == null || (bestLocation.weightToAvoidThisLocation > bestLocationTest.weightToAvoidThisLocation)){
	    				bestLocation = bestLocationTest;
	    			}
	    		}
	    		
	    		System.out.println("Moving to the best location");
	    		System.out.println(bestLocation);
	    		
	    		if (bestLocation != null){
	    			System.out.println(bestLocation.theMapLocation);
	    			if (!rc.hasMoved() && rc.canMove(bestLocation.theMapLocation)){
	    				rc.setIndicatorDot(bestLocation.theMapLocation, 0, 150, 0);
	    				rc.move(bestLocation.theMapLocation);
	    				possibleMoveLocations.remove(bestLocation);
	    				moved = true;
	    				return true;
	    			} else {
	    				rc.setIndicatorDot(bestLocation.theMapLocation, 150, 0, 0);
	    				possibleMoveLocations.remove(bestLocation);
	    				moved = false;
	    			}
	    		}
			}
		}
		return moved;
	}

	public boolean tryMove(Direction dir, List<MapLocation> placesIveBeen, RobotController rc) throws GameActionException {
    	if (dir != null){
    		return tryMove(dir,20,3,placesIveBeen,rc);
    	} else {
    		return false;
    	}
        
    }
	
	public boolean tryMoveTowardsSafety(List<MapLocation> tooCloseRobotLocations, List<MapLocation> placesIveBeen, RobotController rc) throws GameActionException {
    	MapLocation myLoc = rc.getLocation();
    	boolean moved = false;

    	for (MapLocation theLoc: tooCloseRobotLocations){
    		double vX = myLoc.x - theLoc.x;
        	double vY = myLoc.y - theLoc.y;
        	double magV = Math.sqrt(vX*vX + vY*vY);
        	double aX = theLoc.x + vX / magV * 1.5; //1.5 is the assumed radius of an attacking robot hit radius around their center
        	double aY = theLoc.y + vY / magV * 1.5;
        	MapLocation theClosestPointOnTheCircle = new MapLocation((float)(aX),(float)(aY));
        	boolean isSafeEnough = true;
        	for (MapLocation testLoc: tooCloseRobotLocations){
        		if (testLoc.distanceTo(theClosestPointOnTheCircle)<1.5){
        			isSafeEnough = false;
        		}
        	}
        	if (isSafeEnough){
        		if (myLoc.directionTo(theClosestPointOnTheCircle) != null){
        			moved = tryMove(myLoc.directionTo(theClosestPointOnTheCircle),placesIveBeen,rc);
        		}
        		if (moved){
        			return true;
        		}
        	}
    	}
    	//if you can't move somewhere safe, move towards home
    	moved = tryMove(myLoc.directionTo(rc.getInitialArchonLocations(rc.getTeam())[0]),placesIveBeen,rc);
		return moved;
		
	}

	public void wander(List<MapLocation> placesIveBeen, RobotController rc) throws GameActionException {
		if (rc.getMoveCount() == 0){
			tryMove(baseRobot.utilityFunctions.randomDirection(),baseRobot.DEFAULT_TURN_ANGLE_DEGREES,baseRobot.DEFAULT_TURN_ATTEMPTS,placesIveBeen, rc);
		}
		
	}
}
