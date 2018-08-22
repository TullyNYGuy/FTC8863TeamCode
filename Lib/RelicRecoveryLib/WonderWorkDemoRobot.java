package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.AllianceColorSwitch;

public class WonderWorkDemoRobot {

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
    public RobotMode robotMode;
    public DriveTrain driveTrain;
    public SweeperArm sweeperarm;
    private Telemetry telemetry;

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

    private WonderWorkDemoRobot(HardwareMap hardwareMap, RobotMode robotMode, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        this.telemetry = telemetry;
        // create the robot for teleop
        driveTrain = DriveTrain.DriveTrainTeleOpNoIMU(hardwareMap, telemetry);
        sweeperarm = new SweeperArm(hardwareMap,telemetry);
        init(telemetry);
    }

    public static WonderWorkDemoRobot createRobotForAutonomous(HardwareMap hardwareMap, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        WonderWorkDemoRobot robot = new WonderWorkDemoRobot(hardwareMap, RobotMode.AUTONOMOUS, telemetry, teamColor, dataLog);
        return robot;
    }

    public static WonderWorkDemoRobot createRobotForTeleop(HardwareMap hardwareMap, Telemetry telemetry, AllianceColor.TeamColor teamColor, DataLogging dataLog) {
        WonderWorkDemoRobot robot = new WonderWorkDemoRobot(hardwareMap, RobotMode.TELEOP, telemetry, teamColor, dataLog);
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

    public void init(Telemetry telemetry) {
        sweeperarm.init();
    }

    public void setupForRun() {
    }

    public void update() {
    }

    public void shutdown() {
        driveTrain.shutdown();
    }
}
