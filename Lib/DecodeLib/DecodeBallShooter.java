package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

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
    public enum HoodPositions{
        SHORT,
        LONG
    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DecodeShooterMotor shooterMotor;
    private DecodeHoodServo hoodServo;
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

    public void setRPM (int aRPM){
        this.RPM = aRPM;
        shooterMotor.setRPM(this.RPM);
    }
    public void shootLong(){
        setRPM(3100);
        setHoodPosition(HoodPositions.LONG);
    }
    public void shootShort(){
        setRPM(3100);
        setHoodPosition(HoodPositions.SHORT);
    }
    public void off(){
        setRPM(0);
    }

    public void setHoodPosition(HoodPositions hoodPosition){
        if (hoodPosition==HoodPositions.SHORT){
            hoodServo.shortPosition();
        }
        if (hoodPosition==HoodPositions.LONG){
            hoodServo.longPosition();
        }
    }

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;
    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * @param shooterMotorName  The name of the left motor
     * @param hardwareMap    Hardware map from the FTC robot
     * @param telemetry      The telemetry from the FTC robot
     */
    public DecodeBallShooter(String shooterMotorName, HardwareMap hardwareMap, Telemetry telemetry) {
        shooterMotor=new DecodeShooterMotor("shooterMotor",hardwareMap,telemetry);
        hoodServo=new DecodeHoodServo(hardwareMap,telemetry);
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
    @Override
    public void update() {
    }

    /**
     * Stops the gearbox
     */
    public void stop() {
        // interrupt sets the motors to coast to a stop, not stop suddenly
        shooterMotor.stop();
        hoodServo.initPosition();
    }

    @Override
    public String getName() {
        return "shooter";
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
        hoodServo.init(null);
        shooterMotor.init(null);
        return true;
    }
}
