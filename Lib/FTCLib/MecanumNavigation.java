package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Point;

public class MecanumNavigation {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private OdometrySystem odometrySystem;
    private DistanceUnit unit;
    private Point currentPosition;

    private Pose currentPose;
    private Point targetPosition;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public MecanumNavigation(OdometrySystem odometrySystem, DistanceUnit unit) {
        this.odometrySystem = odometrySystem;
        this.unit = unit;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    // get current x, y, orientation of robot
    // given x, y of target
    // calculate angle to target
    // calculate robot rate of rotation needed to maintain desired orientation using PID
    // setup mecanum commands: robot speed, angle of translation, robot rate of rotation

    public void settargetPosition(double targetX, double targetY, DistanceUnit targetUnits) {
        targetPosition = new Point(targetX, targetY, targetUnits);
    }

    public MecanumCommands getMecanumCommands(MecanumCommands commands, double targetX, double targetY, DistanceUnit targetUnits) {
        currentPose = odometrySystem.getCurrentPose();
        // get the angle of translation
        double angleToTarget = currentPosition.angleTo(targetPosition);
        // calculate the rate of rotation
        double currentOrientation = odometrySystem.getCurrentRotation(AngleUnit.RADIANS);
        commands.
    }


}
