package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcServoMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class ExtensionArm extends ExtensionRetractionMechanism implements FTCRobotSubsystem {
    private final static String SUBSYSTEM_NAME = "ExtentionArm";
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

    private double movementPerRevolution = 2.75 * Math.PI * 2; // 2 = number of stages

    @Override
    public double getMovementPerRevolution() {
        return movementPerRevolution;
    }

    @Override
    public void setMovementPerRevolution(double movementPerRevolution) {
        this.movementPerRevolution = movementPerRevolution;
    }

    private int encoderCountsPerRevolution = 1140;

    public int getEncoderCountsPerRevolution() {
        return encoderCountsPerRevolution;
    }

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

    public ExtensionArm(HardwareMap hardwareMap, Telemetry telemetry, String mechanismName,
                        String extensionLimitSwitchName, String retractionLimitSwitchName,
                        String motorName, DcMotor8863.MotorType motorType, double movementPerRevolution) {
        super(hardwareMap, telemetry, mechanismName, extensionLimitSwitchName, retractionLimitSwitchName, motorName, motorType, movementPerRevolution);
    }

    /**
     * This method overrides the parent method for creating the motor since the extension arm
     * does not use a real motor. It uses a continuous rotation servo with encoder feedback instead.
     *
     * @param hardwareMap
     * @param telemetry
     * @param motorName
     */
    @Override
    protected void createExtensionRetractionMotor(HardwareMap hardwareMap, Telemetry telemetry, String motorName) {
        // the encoder is plugged into the drive train FrontLeft motor port
        extensionRetractionMotor = new DcServoMotor("ExtensionArmEncoder", "extensionArmServoMotor", 0.5, 0.5, .01, hardwareMap, telemetry);
        extensionRetractionMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void rotateSpool(double degrees, double power) {
        extensionRetractionMotor.setTargetPosition((int) (degrees / 360 * getEncoderCountsPerRevolution()));
        extensionRetractionMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extensionRetractionMotor.rotateToEncoderCount(power, extensionRetractionMotor.getTargetEncoderCount(), DcMotor8863.FinishBehavior.HOLD);
    }

    public void calibrate(double degrees, double power, LinearOpMode opMode) {
        rotateSpool(degrees, power);
        while (opMode.opModeIsActive() && !extensionRetractionMotor.isMotorStateComplete()) {
            opMode.telemetry.addData("encoder count = ", extensionRetractionMotor.getCurrentPosition());
            opMode.telemetry.update();
            opMode.idle();
        }
        double numberOfRevolutions = extensionRetractionMotor.getCurrentPosition() / (double) getEncoderCountsPerRevolution();
        opMode.telemetry.addData("actual number of revolutions = ", numberOfRevolutions);
        opMode.telemetry.addData("Measure the distance moved. Calculate distance / revolution", "!");
        opMode.telemetry.update();
        while (opMode.opModeIsActive()) {
            // wait for user to get values and then kill the opmode
            opMode.idle();
        }
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    protected void stopMechanism() {
        extensionRetractionMotor.setPower(0.0);
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean init(Configuration config) {
        return super.init();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

}
