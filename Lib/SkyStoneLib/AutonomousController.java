package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;

import java.util.HashMap;
import java.util.Map;


public class AutonomousController {
    final private double BLUE_BUILDSITE_X = 10.0;
    final private double BLUE_BUILDSITE_Y = 10.0;
    final private double RED_BUILDSITE_X = 10.0;
    final private double RED_BUILDSITE_Y = 10.0;
    final private double BLUE_BRIDGE_X = 10.0;
    final private double BLUE_BRIDGE_Y = 10.0;
    final private double RED_BRIDGE_X = 10.0;
    final private double RED_BRIDGE_Y = 10.0;
    final private double BLUE_PLATFORM_X = 10.0;
    final private double BLUE_PLATFORM_Y = 10.0;
    final private double RED_PLATFORM_X = 10.0;
    final private double RED_PLATFORM_Y = 10.0;
    final private double BLUE_HOME_X = 10.0;
    final private double BLUE_HOME_Y = 10.0;
    final private double RED_HOME_X = 10.0;
    final private double RED_HOME_Y = 10.0;

    final private DistanceUnit distanceUnit = DistanceUnit.CM;

    private enum Areas {
        BUILDSITE, BRIDGE, BLOCK, PLATFORM, HOME
    }

    public boolean blockState;
    Areas place;

    private enum Color {
        BLUE, RED
    }

    private Color color;

    private Position currentDestination;

    private Map<Areas, Position> places;

    private SkystoneRobot robot;

    public AutonomousController(SkystoneRobot robot) {
        places = new HashMap<Areas, Position>();
        this.robot = robot;
    }


    public void setBlockState(boolean state) {
        blockState = state;
    }

    public void setAlligance(Color color) {
        this.color = color;
    }

    // these variables are place holders for now. in the furture they will be only the coordinates for the first
    // block in the line
    public void moveTo(double x, double y) {
        //move robot to specified coordinates
    }

    public void initPlaces() {
        if (color == Color.BLUE) {
            places.put(Areas.BUILDSITE, new Position(distanceUnit, BLUE_BUILDSITE_X, BLUE_BUILDSITE_Y, 0, 0));
            places.put(Areas.BRIDGE, new Position(distanceUnit, BLUE_BRIDGE_X, BLUE_BRIDGE_Y, 0, 0));
            places.put(Areas.PLATFORM, new Position(distanceUnit, BLUE_PLATFORM_X, BLUE_PLATFORM_Y, 0, 0));
            places.put(Areas.HOME, new Position(distanceUnit, BLUE_HOME_X, BLUE_HOME_Y, 0, 0));
        } else {
            places.put(Areas.BUILDSITE, new Position(distanceUnit, RED_BUILDSITE_X, RED_BUILDSITE_Y, 0, 0));
            places.put(Areas.BRIDGE, new Position(distanceUnit, RED_BRIDGE_X, RED_BRIDGE_Y, 0, 0));
            places.put(Areas.PLATFORM, new Position(distanceUnit, RED_PLATFORM_X, RED_PLATFORM_Y, 0, 0));
            places.put(Areas.HOME, new Position(distanceUnit, RED_HOME_X, RED_HOME_Y, 0, 0));
        }
    }

    public void goTo(Areas place) {
        this.place = place;
        Position p = places.get(place);
        if (p != null) {
            moveTo(p.x, p.y);
        }
    }

    public void pickUpBlock() {
        goTo(Areas.BLOCK);
        findBlock();
        intakeBlock();
        goTo(Areas.BUILDSITE);
        dropBlock();
    }

    private void findBlock() {
    }

    private void intakeBlock() {

    }

    private void dropBlock() {

    }

    public void park() {
        //go to (bridge coordinates)
    }

    public void parkWithArm() {
        //go to (coordinates near bridge)
        //rotate to face bridge
        //extend arm
    }

    public void moveBase() {
        //go to base
        //grab base
        //move so base is in build area
        //let go of base
    }

    public void moveBaseRotate() {
        //go to base
        //grab base
        //rotate 90 degrees
        //move so base is in build area
        //let go of base
    }

    public void putBlockOnBase() {
        pickUpBlock();
        //pick up block with arm
        //move to base
        //arm up
        //extend arm
        //lower arm
        //align qrm
        //drop arm
    }

}
