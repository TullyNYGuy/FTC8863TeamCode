package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class DecodeBallShooter implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum HoodPositions {
        SHORT,
        MEDIUM,
        LONG
    }
    private HoodPositions hoodPosition = HoodPositions.SHORT;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DecodeShooterMotor shooterMotor;
    private final String SHOOTER_MOTOR_NAME = DecodeRobot.HardwareName.SHOOTER_MOTOR.hwName;
    private DecodeHoodServo hoodServo;

    private double requestedRPM = 0;
    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************
    private int RPM;

    public int getRPM() {
        return RPM;
    }

    public void setRPM(int aRPM) {
        this.RPM = aRPM;
        shooterMotor.setRPM(this.RPM);
    }

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        // setup logging for the objects that make up this subsystem
        hoodServo.setDataLog(logFile);
        shooterMotor.setDataLog(logFile);
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
        hoodServo.enableDataLogging();
        shooterMotor.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
        hoodServo.disableDataLogging();
        shooterMotor.disableDataLogging();
    }

    private final String SUB_SYSTEM_NAME = DecodeRobot.HardwareName.BALL_SHOOTER.hwName;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * @param hardwareMap      Hardware map from the FTC robot
     * @param telemetry        The telemetry from the FTC robot
     */
    public DecodeBallShooter(HardwareMap hardwareMap, Telemetry telemetry) {
        shooterMotor = new DecodeShooterMotor(hardwareMap, telemetry);
        hoodServo = new DecodeHoodServo(hardwareMap, telemetry);
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

    public void shootLong() {
        shooterMotor.runAtRPMForLongShot();
        setHoodPosition(HoodPositions.LONG);
    }

    public void shootMedium() {
        shooterMotor.runAtRPMForMediumShot();
        setHoodPosition(HoodPositions.SHORT);
    }
    public void shootShort() {
        shooterMotor.runAtRPMForShortShot();
        setHoodPosition(HoodPositions.SHORT);
    }

    /**
     * Set the rpm to 0, but does not change the hood position
     */
    public void off() {
        setRPM(0);
    }

    public void setHoodPosition(HoodPositions hoodPosition) {
        this.hoodPosition = hoodPosition;
        if (hoodPosition == HoodPositions.SHORT) {
            hoodServo.shortPosition();
        }
        if (hoodPosition == HoodPositions.MEDIUM) {
            hoodServo.mediumPosition();
        }
        if (hoodPosition == HoodPositions.LONG) {
            hoodServo.longPosition();
        }
    }

    public double getActualRPM() {
        return shooterMotor.getActualRPM();
    }

    public void displayActualRPM(Telemetry telemetry) {
        telemetry.addData("Shooter actual RPM ", shooterMotor.getActualRPM());
    }

    public void displayHoodPosition(Telemetry telemetry) {
        telemetry.addData("Hood Position ", hoodPosition.toString() );
    }

    public void setMode(DcMotor.RunMode mode) {
        shooterMotor.setMode(mode);
    }

    public void setPower(double power) {
        shooterMotor.setPower(power);
    }

    /**
     * Stops the gearbox, sets the servo hood to init position
     */
    public void stop() {
        // interrupt sets the motors to coast to a stop, not stop suddenly
        shooterMotor.stop();
        hoodServo.initPosition();
    }

    public int getShooterMotorEncoderCount() {
        return shooterMotor.getCurrentPosition();
    }

    @Override
    public void update() {
        shooterMotor.update();
        // The hood servo updates when isPositionReached() is called
    }

    @Override
    public String getName() {
        return SUB_SYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        // init all of the objects that make up this subsystem
        hoodServo.init(null);
        shooterMotor.init(null);
        return true;
    }
}
