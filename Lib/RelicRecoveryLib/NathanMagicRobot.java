package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.AllianceColorSwitch;

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
    public AllianceColorSwitch allianceColorSwitch;
    public AllianceColorSwitch.AllianceColor allianceColor;
    public DriveTrain driveTrain;
    public Servo8863 leftBlockGrabberServo;
    public Servo8863 rightBlockGrabberServo;
    public DcMotor8863 liftMotor;
    public DcMotor8863 extendingArmMotor;
    public Servo8863 relicfingers;
    public Servo8863 relicwrist;
    public Servo8863 jewelSmackerServo;
    public Servo8863 clampServo;
    public Servo8863 jewelSmackerServoTwo;

    //**********************************************
    // EXAMPLE LIMIT SWITCH
    public Switch upperLiftLimitSwitch;
    public Switch lowerLiftLimitSwitch;
    //**********************************************

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
        rightBlockGrabberServo.setHomePosition(0);
        rightBlockGrabberServo.setInitPosition(1);
        rightBlockGrabberServo.setPositionOne(.75);

        relicfingers = new Servo8863("relicFingers", hardwareMap, telemetry);
        relicfingers.setDirection(Servo.Direction.FORWARD);
        relicfingers.setHomePosition(.55);
        relicfingers.setInitPosition(.55);
        relicfingers.setPositionOne(.05);

        relicwrist = new Servo8863("relicWrist", hardwareMap, telemetry);
        relicwrist.setDirection(Servo.Direction.FORWARD);
        relicwrist.setHomePosition(.6);
        relicwrist.setInitPosition(.6);
        relicwrist.setPositionOne(0);

        clampServo = new Servo8863("clampServo", hardwareMap, telemetry);
        clampServo.setDirection(Servo.Direction.FORWARD);
        clampServo.setHomePosition(0);
        clampServo.setInitPosition(0);
        clampServo.setPositionOne(.45);

        liftMotor = new DcMotor8863("liftMotor",hardwareMap);
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        liftMotor.setMaxMotorPower(1);
        liftMotor.setMinMotorPower(-1);
        liftMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
       liftMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        liftMotor.setTargetEncoderTolerance(10);
        liftMotor.setMovementPerRev(360);
        liftMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        // switched to SDK provided motor for debug since the above code does not drive the motor in the negative direction
        //liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        //liftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        //liftMotor.setPower(0);

        extendingArmMotor = new DcMotor8863("extendingArmMotor",hardwareMap);
        extendingArmMotor.setDirection(DcMotor.Direction.REVERSE);
        extendingArmMotor.setMaxMotorPower(1);
        extendingArmMotor.setMinMotorPower(-1);
        extendingArmMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20);
        extendingArmMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        extendingArmMotor.setTargetEncoderTolerance(10);
        extendingArmMotor.setMovementPerRev(360);
        extendingArmMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        
        jewelSmackerServo = new Servo8863("jewelSmackerServo", hardwareMap, telemetry);
        jewelSmackerServo.setDirection(Servo.Direction.FORWARD);
        jewelSmackerServo.setHomePosition(0);
        jewelSmackerServo.setInitPosition(1);
        jewelSmackerServo.setPositionOne(.75);

        jewelSmackerServoTwo = new Servo8863("jewelSmackerServoTwo", hardwareMap, telemetry);
        jewelSmackerServoTwo.setDirection(Servo.Direction.FORWARD);
        jewelSmackerServoTwo.setHomePosition(.5);
        jewelSmackerServoTwo.setPositionOne(.35); //position set to front of the robot
        jewelSmackerServoTwo.setPositionTwo(.6);  //position set to the back of the robot


        //**********************************************
        // EXAMPLE LIMIT SWITCH - IF YOU DO NOT HAVE LIMIT SWITCHES CONNECTED ON THE ROBOT COMMENT
        // OUT THIS SECTION
        // you need to configure the robot controller phone
        //    add the core device interface module
        //    within the core device interface module config, select the port the wires for the switch are plugged into and make it a digital device
        //    set the name of the device to the string in green below
        // connecting the switch to the core i/o port -
        //     pick a digital port ( has a D next to it and the number) and plug the connector from the switch into the port
        //         see http://modernroboticsinc.com/core-device-interface-module-2 if you need a picture
        //     plug the cable in so that the dark colored wire is next to the black rectangle right next to the port
        //
        upperLiftLimitSwitch = new Switch(hardwareMap, "upperLiftLimitSwitch", Switch.SwitchType.NORMALLY_OPEN);
        lowerLiftLimitSwitch = new Switch(hardwareMap, "lowerLiftLimitSwitch", Switch.SwitchType.NORMALLY_OPEN);
        //**********************************************

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
        //liftMotor.runAtConstantSpeed(0);
        //liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void update() {
        //**********************************************
        // EXAMPLE LIMIT SWITCH
        upperLiftLimitSwitch.updateSwitch();
        lowerLiftLimitSwitch.updateSwitch();
        //**********************************************
    }

    public void shutdown() {
        driveTrain.shutdown();
        setLiftPower(0);
    }

    public double setLiftPower(double liftPower) {
        //**********************************************
        // EXAMPLE LIMIT SWITCH - IF YOU DO NOT HAVE LIMIT SWITCHES CONNECTED ON THE ROBOT COMMENT
        // OUT THIS SECTION
        // In the example below I'm assuming that
        //    liftPower > 0 means that the lift is going up
        //    liftPower < 0 means that the lift is going down
        // I may not be right! I'm only guessing. If the code does not work, then I got it wrong
        // so you will have to change the greater and lesser than symbols.

        // check if the limit switch is pressed and the direction that the lift is being told
        // to move.

        if (upperLiftLimitSwitch.isPressed() && liftPower > 0) {
            liftPower = 0;
        }

        if (lowerLiftLimitSwitch.isPressed() && liftPower < 0) {
            // the lift is being commanded to move down but the lower limit switch is pressed so
            // it cannot go down any more. Force the motor power to 0 to shut off the motor.
            // Note that if the limit switch is being pressed and the command is to raise the lift
            // (liftPower > 0) then that is ok so don't change the lift power to 0.
            liftPower = 0;
        }

        // you will need to do something similar for the upper limit switch.
        // But think about it. What are you going to check the liftPower for when the
        // upper limit switch is pressed?
        //**********************************************

        // Now send the resulting power to the lift motor
        liftMotor.setPower(liftPower);
        return liftPower;
    }

    // most of the functionality of the robot is reached by calling methods in the objects that make
    // up the robot. For example:
    // rightSideBeaconPusher.pushBeacon()
    // ballShooter.shoot()
}
