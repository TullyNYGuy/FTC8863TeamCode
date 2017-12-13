package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class ExtensionArm {

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

    // this servo controls the position of the wrist on the arm
    private Servo8863 wristServo;
    // setup positions for the wrist servo
    private double wristPositionInit = 0;
    private double wristPositionHome = 0;
    private double wristPositionPickup = .3;
    private double wristPositionCarry = .9;

    // this servo controls the claw at the end of the wrist. The claw clamps onto the relic
    private Servo8863 clawServo;
    // setup positions for the claw servo
    private double clawPositionInit =  0;
    private double clawPositionHome = 0;
    private double clawPositionOpen = .8;
    private  double clawPositionClose = .4;

    // The motor the extends and retracts the arm
    private DcMotor8863 armMotor;
    // for each revolution of the motor, how much does the arm extend?
    private double inchPerRotation = 3.0;

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

   public ExtensionArm(HardwareMap hardwareMap, Telemetry telemetry) {
       // create and setup the wrist servo
       wristServo = new Servo8863("wristServo", hardwareMap, telemetry);
       wristServo.setInitPosition(wristPositionInit);
       wristServo.setHomePosition(wristPositionHome);
       wristServo.setPositionOne(wristPositionPickup);
       wristServo.setPositionTwo(wristPositionCarry);
       wristServo.setDirection(Servo.Direction.FORWARD);

       // create and setup the claw servo
       clawServo = new Servo8863 ( "clawServo", hardwareMap, telemetry);
       clawServo.setInitPosition(clawPositionInit);
       clawServo.setHomePosition(clawPositionHome);
       clawServo.setPositionOne(clawPositionOpen);
       clawServo.setPositionTwo(clawPositionClose);
       clawServo.setDirection(Servo.Direction.FORWARD);

       // create and setup the arm motor
       armMotor = new DcMotor8863("armMotor", hardwareMap);
       armMotor.setDirection(DcMotor.Direction.FORWARD);
       armMotor.setMaxMotorPower(1);
       armMotor.setMinMotorPower(-1);
       armMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
       // the extension of the arm is to a set point, like on a ruler
       // it is not moving by an amount each time (relative)
       armMotor.setMotorMoveType(DcMotor8863.MotorMoveType.ABSOLUTE);
       // how close to the target encoder value do we have to get before we call it good enough
       // to stop the motor
       armMotor.setTargetEncoderTolerance(10);
       armMotor.setMovementPerRev(inchPerRotation);
       // the motor will apply power to hold its position after it arrives at the position
       armMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
       // the motor will move to the target position and then narrow in on it - the motor controller
       // will control this for us
       armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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

    public void carryRelic() {
       wristServo.goPositionTwo();
    }

    public void goToPickup() {
       wristServo.goPositionOne();
    }

    public void openClaw() {
       clawServo.goPositionOne();
    }

    public void closeClaw() {
       clawServo.goPositionTwo();
    }


    public void init() {
        wristServo.goInitPosition();
        clawServo.goInitPosition();
        // the motor power is initially 0
        armMotor.setPower(0);
    }

    public void shutdown(){
       // put the shutdown commands here
    }
}
