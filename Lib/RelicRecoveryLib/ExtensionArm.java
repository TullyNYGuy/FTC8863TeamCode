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
    private double wristPositionInit = .95;
    private double wristPositionHome = .95;
    private double wristPositionPickup = .7;
    private double wristPositionCarry = .05;

    // this servo controls the claw at the end of the wrist. The claw clamps onto the relic
    private Servo8863 clawServo;
    // setup positions for the claw servo
    private double clawPositionInit =  0.8;
    private double clawPositionHome = 0.8;
    private double clawPositionOpen = 0;
    private  double clawPositionClose = .55;

//    private double clawPositionInit =  0.2;
//    private double clawPositionHome = 0.2;
//    private double clawPositionOpen = 1.0;
//    private  double clawPositionClose = .45;

    // The motor the extends and retracts the arm
    private DcMotor8863 armMotor;
    // for each revolution of the motor, how much does the arm extend?
    private double inchPerRotation = 10.0;
    private double zone1Position = 9.0; //This is really 7.75, plus 1 to get us there
    private double zone2Position = 24.0;
    private double zone3Position = 37.0; //This is the max we can go - actual is 39.25"
    private double retractedPosition = 1.0;
    private DcMotor8863.MotorState armMotorState;

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
       armMotor.setDirection(DcMotor.Direction.REVERSE);
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

       this.telemetry = telemetry;
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
        wristServo.goInitPosition();
        clawServo.goInitPosition();
        // the motor power is initially 0
        armMotor.setPower(0);
        telemetry.addData("ExtensionArm Initialized", "!");
    }

    public void update() {
       // put update commands here for anything that needs to update every few milliseconds
        armMotorState = armMotor.update();
        telemetry.addData("encoder = ", "%d", armMotor.getCurrentPosition());
        telemetry.addData("Arm motor state = ", armMotorState.toString());
}

    public void shutdown(){
       // put the shutdown commands here
        armMotor.shutDown();
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
    public void goToPosition(double distanceInInches, double power) {
        armMotor.moveToPosition(power, distanceInInches, DcMotor8863.FinishBehavior.HOLD);
    }

    /**
     * Extend the arm to the middle of relic recovery zone 1
     */
    public void goToZone1() {
        goToPosition(zone1Position, .6);
    }

    /**
     * Extend the arm to the middle of relic recovery zone 2
     */
    public void goToZone2() {
        goToPosition(zone2Position, .6);
    }

    /**
     * Extend the arm to the middle of relic recovery zone 3
     */
    public void goToZone3() {
        goToPosition(zone3Position, .6);
    }

    /**
     * Retract the extension arm almost to its starting point.
     */
    public void retractArm() {
        goToPosition(retractedPosition, .3);
    }

    /**
     * Use this to find out how many inches the arm moves for each revolution
     */
    public void getInchPerRotation() {
        double numberOfRotations = 2;
        armMotor.rotateNumberOfDegrees(.2, numberOfRotations * 360, DcMotor8863.FinishBehavior.HOLD);
    }

    /**
     * Rotate the motor a tiny bit in the forward direction so the operator can see which direction
     * is forward.
     */
    public void testMotorDirection() {
        armMotor.rotateNumberOfDegrees(.2, 5, DcMotor8863.FinishBehavior.HOLD);
    }
}
