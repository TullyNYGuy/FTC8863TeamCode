package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class FFArm implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum ArmPart{
        CLAW,
        WRIST,
        SHOULDER
    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    ClawServo clawServo;
    WristServo wristServo;
    ShoulderServo shoulderServo;
    private final String CLAW_NAME = FreightFrenzyRobot.HardwareName.CLAW_SERVO.hwName;
    private final String WRIST_NAME = FreightFrenzyRobot.HardwareName.WRIST_SERVO.hwName;
    private final String SHOULDER_NAME = FreightFrenzyRobot.HardwareName.SHOULDER_SERVO.hwName;
    private DataLogging logFile;
    private Boolean initComplete = false;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFArm(HardwareMap hardwareMap, Telemetry telemetry) {
        clawServo = new ClawServo(hardwareMap, telemetry);
        wristServo = new WristServo(hardwareMap, telemetry);
        shoulderServo = new ShoulderServo(hardwareMap, telemetry);
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
    public void openClaw() {
        clawServo.open();
    }

    public void closeClaw() {
        clawServo.close();
    }

    public void pickup() {
        shoulderServo.down();
        wristServo.pickup();
        clawServo.openPlusDelay();
    }

    public void carry() {
    }

    public void storage() {
        shoulderServo.storage();
        wristServo.storage();
        clawServo.close();
    }

    public void dropoff() {
    }

    public void hold() {
    }

    public boolean isPositionReached() {
        boolean answer = false;
        if (shoulderServo.isPositionReached() && wristServo.isPositionReached() && clawServo.isPositionReached()) {
            answer = true;
        }
        return answer;
    }

    private String requestedName;
    //ArmPart requestedPart
    @Override
    public String getName() {
        /*
        switch(requestedPart){
            case CLAW: requestedName = CLAW_NAME;
            case WRIST: requestedName = WRIST_NAME;
            case SHOULDER: requestedName = SHOULDER_NAME;
        };
        
         */
        return SHOULDER_NAME;
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
    isPositionReached();
    }

    @Override
    public void shutdown() {
    storage();
    }

    @Override
    public void setDataLog(DataLogging logFile) {

    }

    @Override
    public void enableDataLogging() {

    }

    @Override
    public void disableDataLogging() {

    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}


