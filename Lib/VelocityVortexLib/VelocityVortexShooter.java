package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

public class VelocityVortexShooter {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Eventually we will have a state machine to "soft" stop and "soft" reverse the direction
     */
    
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    public DcMotor8863 shooterMotor;
    public DcMotor8863 shooterLeadScrewMotor;
    public Servo8863 ballGateServo;

    private Telemetry telemetry;

    private double shooterPower = 0;


    private double openPosition = 0.50;
    private double closedPosition = 1.00;
    private double initPosition = closedPosition;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getshooterPower() {
        return shooterPower;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public VelocityVortexShooter(HardwareMap hardwareMap, Telemetry telemetry) {
        // setup the motor
        shooterMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getShooterMotorName(), hardwareMap);
        shooterMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        shooterMotor.setMovementPerRev(360);
        shooterMotor.setTargetEncoderTolerance(10);
        shooterMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        shooterMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        shooterMotor.setMinMotorPower(-1);
        shooterMotor.setMaxMotorPower(1);

        // setup the motor
        shooterLeadScrewMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getShooterLeadscrewMotorName(), hardwareMap);
        shooterLeadScrewMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        shooterLeadScrewMotor.setMovementPerRev(360);
        shooterLeadScrewMotor.setTargetEncoderTolerance(10);
        shooterLeadScrewMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        shooterLeadScrewMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        shooterLeadScrewMotor.setMinMotorPower(-1);
        shooterLeadScrewMotor.setMaxMotorPower(1);

        // setup the servo
        this.telemetry = telemetry;
        ballGateServo = new Servo8863(RobotConfigMappingForGenericTest.getBallGateServoName(), hardwareMap, telemetry);
        ballGateServo.setHomePosition(closedPosition);
        ballGateServo.setPositionOne(openPosition);
        ballGateServo.setInitPosition(closedPosition);
        ballGateServo.goInitPosition();
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
    public void init(){
        // set its direction
        shooterMotor.setDirection(DcMotor.Direction.REVERSE);
        // set the mode for the motor and lock it in place
        shooterMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // set its direction
        shooterLeadScrewMotor.setDirection(DcMotor.Direction.REVERSE);
        // set the mode for the motor and lock it in place
        shooterLeadScrewMotor.runAtConstantSpeed(0);
    }

    public void stop(){
        shooterMotor.interrupt();
        shooterPower = 0;
    }

    public void shoot(){
        shooterPower = 0.5;
        shooterMotor.moveByAmount(shooterPower, 360, DcMotor8863.FinishBehavior.HOLD);
    }

    public void aimShooter(double leadScrewPower) {
        shooterLeadScrewMotor.setPower(leadScrewPower);
    }

    public void update(){
        shooterLeadScrewMotor.update();
        shooterMotor.update();
    }

    public void shutdown() {
        shooterMotor.setPower(0);
        shooterMotor.shutDown();
    }
    public void setPower(double power){
        shooterMotor.setPower(power);
    }

    public void openBallGate() {
        ballGateServo.goPositionOne();
    }

    public void closeBallGate() {
        ballGateServo.goHome();
    }
}