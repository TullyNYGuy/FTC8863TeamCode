package org.firstinspires.ftc.teamcode.opmodes.VelocityVortex;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

public class VelocityVortexRobot {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum RobotMode {
        AUTONOMOUS,
        TELEOP
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    // note that the IMU is an object in the drive train
    public DriveTrain driveTrain;
    public VelocityVortexSweeper sweeper;

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

    private VelocityVortexRobot(HardwareMap hardwareMap, RobotMode robotMode) {
        if(robotMode == RobotMode.AUTONOMOUS) {
            driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap);
        } else {
            driveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap);
        }
        sweeper = new VelocityVortexSweeper(hardwareMap);
        init();
    }

    public static VelocityVortexRobot createRobotForAutonomous(HardwareMap hardwareMap) {
        VelocityVortexRobot robot = new VelocityVortexRobot(hardwareMap, RobotMode.AUTONOMOUS);
        return robot;
    }

    public static VelocityVortexRobot createRobotForTeleop(HardwareMap hardwareMap) {
        VelocityVortexRobot robot = new VelocityVortexRobot(hardwareMap, RobotMode.TELEOP);
        return robot;
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

    public void init() {
        sweeper.init();
        driveTrain.setCmPerRotation(31.1); // cm
    }

    public void update() {
        sweeper.update();
    }

    public void shutdown() {
        sweeper.shudown();
        driveTrain.shutdown();
    }
}
