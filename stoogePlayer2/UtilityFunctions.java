package stoogePlayer2;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class UtilityFunctions {

	UtilityFunctions(){
	}

	public void setInitialMapExtents(MapLocation[] myInitialArchonLocations, MapLocation[] theirInitialArchonLocations,
			RobotController rc) throws GameActionException {
			float minX = -1;
			float minY = -1;
			float maxX = -1;
			float maxY = -1;
			
			for (MapLocation aLoc:myInitialArchonLocations){
				if (minX == -1 || aLoc.x < minX){
					minX = aLoc.x;
				}
				if (maxX == -1 || aLoc.x < maxX){
					maxX = aLoc.x;
				}
				if (minY == -1 || aLoc.y < minY){
					minY = aLoc.y;
				}
				if (maxY == -1 || aLoc.y < maxY){
					maxY = aLoc.y;
				}
			}
			
			for (MapLocation aLoc:theirInitialArchonLocations){
				if (minX == -1 || aLoc.x < minX){
					minX = aLoc.x;
				}
				if (maxX == -1 || aLoc.x < maxX){
					maxX = aLoc.x;
				}
				if (minY == -1 || aLoc.y < minY){
					minY = aLoc.y;
				}
				if (maxY == -1 || aLoc.y < maxY){
					maxY = aLoc.y;
				}
			}
			
			rc.broadcast(baseRobot.CHANNEL_MAP_MINX, (int) minX);
			rc.broadcast(baseRobot.CHANNEL_MAP_MINY, (int) minX);
			rc.broadcast(baseRobot.CHANNEL_MAP_MAXX, (int) maxX);
			rc.broadcast(baseRobot.CHANNEL_MAP_MAXY, (int) maxY);
		
	}

	public float findDistanceBetween(MapLocation[] startLocations, MapLocation[] endLocations,
			String compareFunction) {
		float bestCompareDistance = -1;
		for (MapLocation startLocation: startLocations){
			for (MapLocation endLocation: endLocations){
				float compareDist = startLocation.distanceTo(endLocation);
				switch (compareFunction){
				case ("nearest"):
					if (bestCompareDistance == -1 || bestCompareDistance > compareDist){
						bestCompareDistance = compareDist;
					}
					break;
				case ("furthest"):
					if (bestCompareDistance == -1 || bestCompareDistance < compareDist){
						bestCompareDistance = compareDist;
					}
					break;
				}
			}
		}
		return bestCompareDistance;
	}
	
	int countNearbyRobotsFor(RobotType theType, Team theTeam, RobotInfo[] allRobots) {
		int robotCount = 0;
		for (RobotInfo robot: allRobots){
    		if (robot.getType() == theType && robot.getTeam() == theTeam) {
    			robotCount+=1;
    		}
    	}
		return robotCount;
	}

	public Direction randomDirection() {
		return new Direction((float)Math.random() * 2 * (float)Math.PI);
	}

	public void tradeInBulletsIfAGoodIdea(RobotController rc) throws GameActionException { //check this because that constant is going away, this is now variable.
		float bulletExchangeRate = rc.getVictoryPointCost();
		
		System.out.println("Checking Bullets");
    	if (rc.getTeamBullets() / bulletExchangeRate > GameConstants.VICTORY_POINTS_TO_WIN - rc.getTeamVictoryPoints()){ //if I have enough to win
        	rc.donate(rc.getTeamBullets());
        } else if (rc.getTeamBullets() > baseRobot.DONATE_THRESHOLD) { //If I have some excess bullets
        	rc.donate(bulletExchangeRate*10);
        	System.out.println("Donated to the cause");
        } else if (rc.getRoundNum() >= rc.getRoundLimit()-1){ //If the game is about to end
        	rc.donate(rc.getTeamBullets());
        }
	}

	public Direction canBuildAdjacent(RobotType theType, Direction preferredDireciton, RobotController rc) {
		//Direction initialDirection = preferredDireciton; //seed random direction with my robotid
		
    	for (int i=0;i<baseRobot.possibleBuildDirections.length;i++){
    		Direction testDir = Direction.NORTH.rotateLeftDegrees(baseRobot.possibleBuildDirections[i]);
    		System.out.println("Can I build an "+theType.name()+" at "+testDir.getAngleDegrees());
    		rc.setIndicatorDot(rc.getLocation().add(testDir), 120, 0, 220);
    		if (rc.canBuildRobot(theType, Direction.NORTH.rotateLeftDegrees(baseRobot.possibleBuildDirections[i]))){
    			System.out.println("YES");
    			rc.setIndicatorDot(rc.getLocation().add(testDir), 0, 255, 0);
    			return testDir;
    		}
    	}
    	return null;
	}

	public int convertMapLocationToInt(MapLocation loc) {
		System.out.println("Converting:"+loc+" to integer."); //464.435211  431.255127  
    	int x = (int)(loc.x*10);  //4644300000.5211
    	System.out.println(x);
    	int y = (int)(loc.y*10);  //43125.5127
    	System.out.println(y);
    	//349375829
    	int offsetX = x*10000;
    	System.out.println(offsetX);
    	int convertedInt = offsetX+y;
    	System.out.println("X:"+x+" Y:"+y+" Converted:"+convertedInt);
    	return convertedInt;
	}

	public MapLocation convertIntToMapLocation(int theInt) {
		float y = ((float)(theInt%10000))/10;
    	float x = ((float)((theInt-(theInt%10000))/10000))/10;
    	MapLocation theLoc = new MapLocation(x, y);
    	System.out.println("The Int:"+theInt+" X:"+x+" Y:"+y);
    	return theLoc;
	}
	
	public void singLyrics() {
		System.out.println("Ow!");
		System.out.println("Whoa, yeah!");

		System.out.println("There she goes and knows I'm dying");
		System.out.println("When she says, \"Who is Johnny?\"");
		System.out.println("Games with names that girl is playing.");
		System.out.println("All she says is, \"Who is Johnny?\"");

		System.out.println("I try to understand because I'm people too,");
		System.out.println("And playing games is part of human nature.");
		System.out.println("My heart's in overdrive.");
		System.out.println("It's great to be alive.");

		System.out.println("\"Who's Johnny?\" she said");
		System.out.println("And smiled in her special way.");
		System.out.println("\"Johnny, \" she said");
		System.out.println("\"You know I love you.\"");
		System.out.println("\"Who's Johnny?\" she said");
		System.out.println("And tried to look the other way.");
		System.out.println("Eyes gave her away.");
		System.out.println("All right!");

		System.out.println("Whoa, oh, oh ...");

		System.out.println("She makes sure I see her teasing,");
		System.out.println("Hear her say, \"Who is Johnny?\"");
		System.out.println("There's no way to take this easy,");
		System.out.println("Hear her say, \"Who is Johnny?\"");

		System.out.println("I really couldn't help but fall in love with her.");
		System.out.println("Her being there has made my life worth living.");
		System.out.println("I knew it from the start");
		System.out.println("That I would lose my heart.");

		System.out.println("\"Who's Johnny?\" she said");
		System.out.println("And smiled in her special way.");
		System.out.println("\"Johnny, \" she said");
		System.out.println("\"You know I love you.\"");
		System.out.println("\"Who's Johnny?\" she said");
		System.out.println("And tried to look the other way.");
		System.out.println("Still pretending.");

		System.out.println("\"Who's Johnny? Who's Johnny?\"");
		System.out.println("Whoa, oh, oh, no");
		System.out.println("\"Who's Johnny? Who's Johnny?\"");
		System.out.println("Oh, no, no, no. I don't believe it.");

		System.out.println("\"Who's Johnny?\" she said");
		System.out.println("And tried to look the other way.");
		System.out.println("Eyes gave her away.");

		System.out.println("are very special.");
		System.out.println("Girls (like her) don't rest");
		System.out.println("'Til you too are a believer");
		System.out.println("'Til you too have caught their fever.");

		System.out.println("\"Who is Johnny?\"");

		System.out.println("There she goes and knows I'm dying");
		System.out.println("When she says, \"Who is -- Who -- Who is ...\"");

		System.out.println("\"Who's Johnny?\" she said");
		System.out.println("And smiled in her special way.");
		System.out.println("\"Johnny, \" she said");
		System.out.println("\"You know I love you.\"");
		System.out.println("\"Who's Johnny?\" she said");
		System.out.println("And tried to look the other way.");
		System.out.println("Eyes gave her away.");

		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");
		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");
		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");

		System.out.println("That girl's pretending she can't remember my name.");

		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");
		System.out.println("Oh, I can't believe it.");
		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");
		System.out.println("I hope this girl is only teasing.");
		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");
		System.out.println("She walked away with someone else");
		System.out.println("And left me standing there.");

		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");
		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");
		System.out.println("\"Who's Johnny? Who's Johnny? Who's Johnny?\"");
		
		System.out.println("Oh, Johnny come lately.");

		System.out.println("\"Who's Johnny?\"");
	}

	
}
