package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
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

    // Here are all of the objects that make up the entire robot
    // note that the IMU is an object in the drive train
    public DriveTrain driveTrain;
    //public FrontBeaconPusher frontBeaconPusher;
    public MuxPlusColorSensors muxPlusColorSensors;
    public VelocityVortexSweeper sweeper;
    public VelocityVortexShooter shooter;
    public SideBeaconPusher rightSideBeaconPusher;
    public SideBeaconPusher leftSideBeaconPusher;
    public FrontBeaconPusher frontBeaconPusher;
    public FrontBeaconPusherControl frontBeaconPusherControl;
    public AllianceColorSwitch allianceColorSwitch;
    // public FrontBeaconPusher frontBeaconPusher;
    // public I2CMux mux;
    // public BallShooter ballShooter;

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

    private VelocityVortexRobot(HardwareMap hardwareMap, RobotMode robotMode, Telemetry telemetry) {
        if(robotMode == RobotMode.AUTONOMOUS) {
            driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        } else {
            driveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap, telemetry);
        }
        sweeper = new VelocityVortexSweeper(hardwareMap);
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        allianceColorSwitch = new AllianceColorSwitch(hardwareMap);
        rightSideBeaconPusher = new SideBeaconPusher(hardwareMap, telemetry, driveTrain, SideBeaconPusher.SideBeaconPusherPosition.RIGHT, muxPlusColorSensors);
        //frontBeaconPusher = new FrontBeaconPusher(hardwareMap, telemetry, muxPlusColorSensors);
        frontBeaconPusherControl = new FrontBeaconPusherControl(hardwareMap, telemetry, muxPlusColorSensors, allianceColorSwitch.getAllianceColor(), driveTrain);
        shooter = new VelocityVortexShooter(hardwareMap, telemetry);
        frontBeaconPusher = new FrontBeaconPusher(hardwareMap, telemetry, muxPlusColorSensors);
        init();
    }


    public static VelocityVortexRobot createRobotForAutonomous(HardwareMap hardwareMap, Telemetry telemetry) {
        VelocityVortexRobot robot = new VelocityVortexRobot(hardwareMap, RobotMode.AUTONOMOUS, telemetry);
        return robot;
    }

    public static VelocityVortexRobot createRobotForTeleop(HardwareMap hardwareMap, Telemetry telemetry) {
        VelocityVortexRobot robot = new VelocityVortexRobot(hardwareMap, RobotMode.TELEOP, telemetry);
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
        shooter.init();
        rightSideBeaconPusher.init();
    }

    public void update() {
        sweeper.update();
        shooter.update();
        frontBeaconPusher.updateState();

    }

    public void shutdown() {
        sweeper.shudown();
        driveTrain.shutdown();
        shooter.shutdown();
        rightSideBeaconPusher.shutdown();
    }

    // most of the functionality of the robot is reached by calling methods in the objects that make
    // up the robot. For example:
    // rightSideBeaconPusher.pushBeacon()
    // ballShooter.shoot()
}
