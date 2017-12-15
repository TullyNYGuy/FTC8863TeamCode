package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

import static java.lang.Thread.sleep;

public class ExtensionArmDirect {

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
    private double wristPositionInit = 1.0;
    private double wristPositionHome = 1.0;
    private double wristPositionPickup = .8;
    private double wristPositionCarry = .05;

    // this servo controls the claw at the end of the wrist. The claw clamps onto the relic
    private Servo8863 clawServo;
    // setup positions for the claw servo
    private double clawPositionInit =  0.8;
    private double clawPositionHome = 0.8;
    private double clawPositionOpen = 0;
    private  double clawPositionClose = .55;

    // The motor the extends and retracts the arm
    private DcMotor armMotor;
    // for each revolution of the motor, how much does the arm extend?
    private double inchPerRotation = 3.0;
    private double zone1Position = 8.0;
    private double zone2Position = 24.0;
    private double zone3Position = 40.0;
    private double retractedPosition = 1.0;

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

   public ExtensionArmDirect(HardwareMap hardwareMap, Telemetry telemetry) {
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
       armMotor  = hardwareMap.get(DcMotor.class, "armMotor");
       armMotor.setDirection(DcMotor.Direction.FORWARD);
       armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       delay(100);
       armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
       delay(100);
       armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       armMotor.setPower(0);
   }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    /**
     * Implements a delay
     * @param mSec delay in milli Seconds
     */
    private void delay(int mSec) {
        try {
            Thread.sleep((int) (mSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // create a property for the motor encoder counts per revolution
    // create a property for position of the arm in inches

    private int inchesToEncoder(double inches) {
        // put in an equation that converts inches (double) to encoder count (int)
        return 0;
    }

    private double encoderToInches(int encoder){
        // put in an equation that converts encoder (int) to inches (double)
        return 0;
    }

    private void moveToPosition(double power, double positionInInches) {

    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void init() {
        wristServo.goInitPosition();
        clawServo.goInitPosition();
        // the motor power is initially 0
        armMotor.setPower(0);
    }

    public void update() {
       // put update commands here for anything that needs to update every few milliseconds
        if (!armMotor.isBusy()) {
            //movement is complete
            armMotor.setPower(0);
        }
    }

    public void shutdown(){
       // put the shutdown commands here
        armMotor.setPower(0);
        wristServo.goInitPosition();
        clawServo.goInitPosition();
    }

    /**
     * Move the wrist servo so that the relic is held upside down in the air
     */
    public void carryRelic() {
        wristServo.goPositionTwo();
    }

    /**
     * Move the wrist servo so that the claw is in position to pick up the relic
     */
    public void goToPickup() {
        wristServo.goPositionOne();
    }

    /**
     * Open the claw to either prepare to pick up the relic or to release it.
     */
    public void openClaw() {
        clawServo.goPositionOne();
    }

    /**
     * Close the claw around the relic
     */
    public void closeClaw() {
        clawServo.goPositionTwo();
    }

    /**
     * Move the extension arm to a position. 0 inches is fully retracted.
     * @param distanceInInches
     */
    public void goToPosition(double distanceInInches) {
        //armMotor.moveToPosition(.1, distanceInInches, DcMotor8863.FinishBehavior.HOLD);
    }

    /**
     * Extend the arm to the middle of relic recovery zone 1
     */
    public void goToZone1() {
        goToPosition(zone1Position);
    }

    /**
     * Extend the arm to the middle of relic recovery zone 2
     */
    public void goToZone2() {
        goToPosition(zone2Position);
    }

    /**
     * Extend the arm to the middle of relic recovery zone 3
     */
    public void goToZone3() {
        goToPosition(zone3Position);
    }

    /**
     * Retract the extension arm almost to its starting point.
     */
    public void retractArm() {
        goToPosition(retractedPosition);
    }

    /**
     * Use this to find out how many inches the arm moves for each revolution
     */
    public void getInchPerRotation() {
        double numberOfRotations = 2;
        //armMotor.rotateNumberOfDegrees(.2, numberOfRotations * 360, DcMotor8863.FinishBehavior.HOLD);
    }

    /**
     * Rotate the motor a tiny bit in the forward direction so the operator can see which direction
     * is forward.
     */
    public void testMotorDirection() {
        //armMotor.rotateNumberOfDegrees(.2, 5, DcMotor8863.FinishBehavior.HOLD);
    }
}
