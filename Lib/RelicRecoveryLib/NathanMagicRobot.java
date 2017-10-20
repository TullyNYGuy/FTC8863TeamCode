package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class NathanMagicRobot {

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
    public Servo8863 leftBlockGrabberServo;
    public Servo8863 rightBlockGrabberServo;
    public DcMotor8863 liftMotor;
    public Servo8863 relicfingers;
    public Servo8863 relicwrist;
    //public FrontBeaconPusher frontBeaconPusher;
    //public MuxPlusColorSensors muxPlusColorSensors;
    //public AllianceColorSwitch allianceColorSwitch;
    //public AllianceColorSwitch.AllianceColor allianceColor;
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

    private NathanMagicRobot(HardwareMap hardwareMap, RobotMode robotMode, Telemetry telemetry) {
        if(robotMode == RobotMode.AUTONOMOUS) {
            driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
            telemetry.addData("Drive Train Initialized", "!");
            telemetry.update();
        } else {
            driveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap, telemetry);
        }
        telemetry.addData("Drive Train Initialized", "!");
        telemetry.update();

        leftBlockGrabberServo = new Servo8863("leftBlockGrabberServo", hardwareMap, telemetry);
        leftBlockGrabberServo.setDirection(Servo.Direction.FORWARD);
        leftBlockGrabberServo.setHomePosition(0);
        leftBlockGrabberServo.setInitPosition(1);
        leftBlockGrabberServo.setPositionOne(.75);

        rightBlockGrabberServo = new Servo8863("rightBlockGrabberServo", hardwareMap, telemetry);
        rightBlockGrabberServo.setDirection(Servo.Direction.FORWARD);
        leftBlockGrabberServo.setHomePosition(0);
        leftBlockGrabberServo.setInitPosition(1);
        leftBlockGrabberServo.setPositionOne(.75);

        relicfingers = new Servo8863("relicFingers", hardwareMap, telemetry);
        relicfingers.setDirection(Servo.Direction.FORWARD);
        relicfingers.setHomePosition(.55);
        relicfingers.setInitPosition(.55);
        relicfingers.setPositionOne(.1);

        relicwrist = new Servo8863("relicWrist", hardwareMap, telemetry);
        relicwrist.setDirection(Servo.Direction.FORWARD);
        relicwrist.setHomePosition(.6);
        relicwrist.setInitPosition(.6);
        relicwrist.setPositionOne(0);

        liftMotor = new DcMotor8863("liftMotor", hardwareMap);
        liftMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20);
        liftMotor.setMovementPerRev(360);
        liftMotor.setTargetEncoderTolerance(5);
        liftMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        liftMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        liftMotor.setMinMotorPower(-1);
        liftMotor.setMaxMotorPower(1);

        init();
    }


    public static NathanMagicRobot createRobotForAutonomous(HardwareMap hardwareMap, Telemetry telemetry) {
        NathanMagicRobot robot = new NathanMagicRobot(hardwareMap, RobotMode.AUTONOMOUS, telemetry);
        return robot;
    }

    public static NathanMagicRobot createRobotForTeleop(HardwareMap hardwareMap, Telemetry telemetry) {
        NathanMagicRobot robot = new NathanMagicRobot(hardwareMap, RobotMode.TELEOP, telemetry);
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
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        // set the mode for the motor and lock it in place
        liftMotor.runAtConstantSpeed(0);
    }

    public void update() {
    }

    public void shutdown() {
        driveTrain.shutdown();
        setLiftPower(0);
    }

    public void setLiftPower(double liftPower) {
        liftMotor.setPower(liftPower);
    }

    // most of the functionality of the robot is reached by calling methods in the objects that make
    // up the robot. For example:
    // rightSideBeaconPusher.pushBeacon()
    // ballShooter.shoot()
}
