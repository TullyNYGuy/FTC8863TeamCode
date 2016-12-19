package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * This was written under pressure at the Rochester qualifier to eliminate the use of DcMotor8863
 * and instead use DcMotor. The problem was that the motors were not being initialized properly and
 * the thinking was that using the SDK provided motor might elminate the problem. It lessened it
 * but did not eliminate it.
 */
public class VelocityVortexRobotDirect {

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
    //public DriveTrain driveTrain;
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

    private VelocityVortexRobotDirect(HardwareMap hardwareMap, RobotMode robotMode) {
//        if(robotMode == RobotMode.AUTONOMOUS) {
//            driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap);
//        } else {
//            driveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap);
//        }
        sweeper = new VelocityVortexSweeper(hardwareMap);
        init();
    }

    public static VelocityVortexRobotDirect createRobotForAutonomous(HardwareMap hardwareMap) {
        VelocityVortexRobotDirect robot = new VelocityVortexRobotDirect(hardwareMap, RobotMode.AUTONOMOUS);
        return robot;
    }

    public static VelocityVortexRobotDirect createRobotForTeleop(HardwareMap hardwareMap) {
        VelocityVortexRobotDirect robot = new VelocityVortexRobotDirect(hardwareMap, RobotMode.TELEOP);
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
//        driveTrain.setCmPerRotation(31.1); // cm
    }

    public void update() {
        sweeper.update();
    }

    public void shutdown() {
        sweeper.shudown();
        //driveTrain.shutdown();
    }
}
