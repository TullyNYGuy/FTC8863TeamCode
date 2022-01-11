package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.arcrobotics.ftclib.command.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;

public class ClawServo implements FTCRobotSubsystem {

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
    Servo8863New clawServo;
    private DataLogging logFile;
    private boolean loggingOn = false;
    private boolean initComplete = false;
    private final String  CLAW_NAME = FreightFrenzyRobot.HardwareName.CLAW_SERVO.hwName;
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

    public ClawServo(HardwareMap hardwareMap, Telemetry telemetry) {
        clawServo = new Servo8863New("clawServo", hardwareMap, telemetry);
        clawServo.addPosition("open", .0, 1000, TimeUnit.MILLISECONDS);
        clawServo.addPosition("close", .58,1000, TimeUnit.MILLISECONDS);
        close();
        initComplete = true;
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

    public void open() {
        clawServo.setPosition("open");
    }

    public void close() {
        clawServo.setPosition("close");
    }

    public boolean isPositionReached() {
        return clawServo.isPositionReached();
    }

    @Override
    public String getName() {
        return CLAW_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return initComplete;
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {
    close();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}

