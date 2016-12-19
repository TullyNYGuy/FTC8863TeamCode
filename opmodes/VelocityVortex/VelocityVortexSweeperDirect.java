package org.firstinspires.ftc.teamcode.opmodes.VelocityVortex;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This was written under pressure at the Rochester qualifier to eliminate the use of DcMotor8863
 * and instead use DcMotor. The problem was that the motors were not being initialized properly and
 * the thinking was that using the SDK provided motor might elminate the problem. It lessened it
 * but did not eliminate it.
 */
public class VelocityVortexSweeperDirect {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Eventually we will have a state machine to "soft" stop and "soft" reverse the direction
     */
    private enum SweeperState {
        NORMAL, //power ramp is not active, power ramp has not just completed
        POWER_RAMP_RUNNING, // power ramp is currently running
        POWER_RAMP_COMPLETE // power ramp just completed
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private SweeperState currentState = SweeperState.NORMAL;

    private DcMotor sweeperMotor;

    private double sweeperPower = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getSweeperPower() {
        return sweeperPower;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public VelocityVortexSweeperDirect(HardwareMap hardwareMap) {
        // setup the motor
        sweeperMotor = hardwareMap.dcMotor.get(RobotConfigMappingForGenericTest.getthirdMotorName());
        sweeperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sweeperMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sweeperMotor.setPower(0);
//        sweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
//        sweeperMotor.setMovementPerRev(360);
//        sweeperMotor.setTargetEncoderTolerance(5);
//        sweeperMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
//        sweeperMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
//        sweeperMotor.setMinMotorPower(-1);
//        sweeperMotor.setMaxMotorPower(1);
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
        sweeperMotor.setDirection(DcMotor.Direction.FORWARD);
        // set the mode for the motor and lock it in place
        //sweeperMotor.runAtConstantSpeed(0);
    }

    public void stop(){
        //sweeperMotor.interrupt();
        sweeperPower = 0;
    }
    public void shoot(){
        sweeperPower = -1;
        sweeperMotor.setPower(sweeperPower);
    }

    public void collect(){
        sweeperPower = .5;
        sweeperMotor.setPower(sweeperPower);
    }

//    public void update(){
//        sweeperMotor.update();
//    }

    public void shudown() {
        sweeperMotor.setPower(0);
    }
}
