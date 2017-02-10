package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

import java.util.HashMap;
import java.util.Map;

public class VelocityVortexShooter {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum BallGatePosition {
        OPEN,
        CLOSE
    }

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
    private int shotCount = 0;


    private double openPosition = 0.50;
    private double closedPosition = 1.00;
    private double initPosition = closedPosition;

    private BallGatePosition ballGatePosition = BallGatePosition.CLOSE;

    // aiming
    Map<Integer, Double> distanceVsAim;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getshooterPower() {
        return shooterPower;
    }

    public int getShotCount() {
        return shotCount;
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

        // setup a lookup table for the distance to the vortex vs the aiming location
        distanceVsAim = new HashMap<Integer, Double>();
        //distanceVsAim.put(1, 2.6);

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
        // set its direction
        shooterMotor.setDirection(DcMotor.Direction.REVERSE);
        // set the mode for the motor and lock it in place
        shooterMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //delay(100);
        // set its direction
        shooterLeadScrewMotor.setDirection(DcMotor.Direction.REVERSE);
        // set the mode for the motor and lock it in place
        shooterLeadScrewMotor.runAtConstantSpeed(0);
        closeBallGate();
    }

    public void update() {
        shooterLeadScrewMotor.update();
        shooterMotor.update();
    }

    public void shutdown() {
        shooterMotor.setPower(0);
        shooterMotor.shutDown();
        // return the shooter to its loading position then shutdown the motor
        shooterLeadScrewMotor.setPower(0);
        shooterLeadScrewMotor.shutDown();

        closeBallGate();
    }

    //--------------------------------------------
    //  shooter methods
    //--------------------------------------------

    /**
     * For joystick control over shooter motor
     * @param power
     */
    public void setPower(double power) {
        shooterMotor.setPower(power);
    }


    public void shoot() {
        shooterPower = 0.5;
        shotCount++;
        shooterMotor.moveToPosition(shooterPower, 360 * shotCount, DcMotor8863.FinishBehavior.HOLD);
    }


    //--------------------------------------------
    //  aiming methods
    //--------------------------------------------


    // NEED TO add limit switch shut down of motor into this method.
    /**
     * Use the joystick to input a power to the leadscrew to manually aim the shooter.
     *
     * @param leadScrewPower
     */
    public void aimShooter(double leadScrewPower) {
        shooterLeadScrewMotor.setPower(leadScrewPower);
    }

    // need a method to aimShooter that will accept the distance to the vortex and move the
    // leadscrew so that the shooter is aimed there.
    // Use the HashMap defined above as a lookup table.
    // Use this method to automatically aim the shooter or to return it to the loading station.

    //--------------------------------------------
    //  ball gate methods
    //--------------------------------------------

    public void openBallGate() {
        ballGateServo.goPositionOne();
        ballGatePosition = BallGatePosition.OPEN;
    }

    public void closeBallGate() {
        ballGateServo.goHome();
        ballGatePosition = BallGatePosition.CLOSE;
    }

    public void toggleBallGate() {
        if (ballGatePosition == BallGatePosition.CLOSE) {
            openBallGate();
        } else {
            closeBallGate();
        }
    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}