package org.firstinspires.ftc.teamcode.Lib.FTCLib;


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

    private Pose currentPose;
    private Pose targetPose;

    public void setTargetPose(Pose targetPose) {
        this.targetPose = targetPose;
    }

    private MecanumOrientationControl mecanumOrientationControl;

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

    public MecanumNavigation(OdometrySystem odometrySystem, Pose targetPose) {
        this.odometrySystem = odometrySystem;
        this.targetPose = targetPose;
        this.mecanumOrientationControl = new MecanumOrientationControl(0.1, 0, 0, targetPose.getOrientation());
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


    public MecanumCommands getMecanumCommands(MecanumCommands commands, double speed) {
        commands.setSpeed(speed);
        currentPose = odometrySystem.getCurrentPose();
        // get the angle of translation
        Angle headingToTarget = currentPose.headingTo(targetPose);
        commands.setAngleOfTranslation(headingToTarget);
        // set the rate of rotation in the mecanum commands
        commands.setSpeedOfRotation(mecanumOrientationControl.getRateOfRotation(currentPose.getOrientation()));
        return commands;
    }


}
