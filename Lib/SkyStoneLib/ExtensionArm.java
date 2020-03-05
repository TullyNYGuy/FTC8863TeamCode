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
    private final static String SUBSYSTEM_NAME = "ExtensionArm";
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

    private double movementPerRevolution = ExtensionArmConstants.movementPerRevolution;

    @Override
    public double getMovementPerRevolution() {
        return movementPerRevolution;
    }

    @Override
    public void setMovementPerRevolution(double movementPerRevolution) {
        this.movementPerRevolution = movementPerRevolution;
    }

//    private int encoderCountsPerRevolution = 1140;
//
//    public int getEncoderCountsPerRevolution() {
//        return DcServoMotor.;
//    }

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
                        String motorNameForEncoderPort, DcMotor8863.MotorType motorType, double movementPerRevolution) {
        super(hardwareMap, telemetry, mechanismName, extensionLimitSwitchName, retractionLimitSwitchName, motorNameForEncoderPort, motorType, movementPerRevolution);
        configureForSkystone();
    }

    /**
     * This method overrides the parent method for creating the motor since the extension arm
     * does not use a real motor. It uses a continuous rotation servo with encoder feedback instead.
     *
     * @param hardwareMap
     * @param telemetry
     * @param motorNameForEncoderPort
     */
    @Override
    protected void createExtensionRetractionMotor(HardwareMap hardwareMap, Telemetry telemetry, String motorNameForEncoderPort) {
        // This hardwired servoName is not ideal. I'd like to be able to pass it in as a parameter in the constructor. But
        // the first thing that has to run in the constructor is super (DCMotor8863) and that then calls
        // this method. So I can't set a property to the servoName yet the statements would have to be
        // after the super. Super is now in the middle of running. So those statements would not have
        // run yet.
        String servoName = SkystoneRobot.HardwareName.EXT_ARM_SERVO.hwName;

        extensionRetractionMotor = new DcServoMotor(motorNameForEncoderPort, servoName, 0.5, 0.5, .01, hardwareMap, telemetry);
        // This call does not change the direction of the motor. It changes the direction of the servo!
        extensionRetractionMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /**
     * Specific configuration for skystone extension arm
     */
    protected void configureForSkystone() {
        //determined experimentally to be 1900 but gave some margin, limited by the drag chain
        setExtensionPositionInEncoderCounts(ExtensionArmConstants.maximumExtensionInEncoderCounts);
        setResetPower(ExtensionArmConstants.resetPower);
        extensionRetractionMotor.encoder.setDirection(DcMotorSimple.Direction.REVERSE);
        extensionRetractionMotor.setTargetEncoderTolerance(20);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    @Override
    protected void stopMechanism() {
        extensionRetractionMotor.setPower(0.0);
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    /**
     * The init of the ExtensionRetractionMechanism resets the motor using
     * setMode(STOP_AND_RESET_ENCODER). setMode is Overridden and calls resetEncoder() so everything
     * is relative to the reset encoder count.
     *
     * @param config
     * @return
     */
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
