package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import android.util.FloatProperty;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;

public class FFLandingGear implements FTCRobotSubsystem {

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
    Servo8863New leftServo;
    Servo8863New rightServo;
    Servo8863New backServo;

    private boolean loggingOn;
    private DataLogging logFile;

    private final String SERVO_SYSTEM_NAME = "Landing Gear";

    private final String  LEFT_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.LANDING_GEAR_LEFT_SERVO.hwName;
    private final String  RIGHT_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.LANDING_GEAR_RIGHT_SERVO.hwName;
    private final String  BACK_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.LANDING_GEAR_BACK_SERVO.hwName;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

        private boolean isInitComplete = false;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public FFLandingGear(HardwareMap hardwareMap, Telemetry telemetry) {
        isInitComplete = false;
        leftServo = new Servo8863New(LEFT_SERVO_NAME, hardwareMap, telemetry);
        rightServo = new Servo8863New(RIGHT_SERVO_NAME, hardwareMap, telemetry);
        backServo = new Servo8863New(BACK_SERVO_NAME, hardwareMap, telemetry);

        leftServo.setDirection(Servo.Direction.FORWARD);
        rightServo.setDirection(Servo.Direction.REVERSE);
        backServo.setDirection(Servo.Direction.FORWARD);

        leftServo.addPosition("Up", 1, 500, TimeUnit.MILLISECONDS);
        leftServo.addPosition("Down", 0, 500, TimeUnit.MILLISECONDS);

        rightServo.addPosition("Up", 1, 500, TimeUnit.MILLISECONDS);
        rightServo.addPosition("Down", 0, 500, TimeUnit.MILLISECONDS);

        backServo.addPosition("Up", 1, 500, TimeUnit.MILLISECONDS);
        backServo.addPosition("Down", 0, 500, TimeUnit.MILLISECONDS);

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


    public void up() {
        leftServo.setPosition("Up");
        rightServo.setPosition("Up");
        backServo.setPosition("Up");
    }

    public void down(){
        leftServo.setPosition("Down");
        rightServo.setPosition("Down");
        backServo.setPosition("Down");
    }

    public boolean isDown() {
        return isPositionReached();
    }

    public boolean isUp() {
        return isPositionReached();
    }

    @Override
    public String getName() {
        return SERVO_SYSTEM_NAME;
    }

    @Override
    public boolean init(Configuration config) {
        down();
        return true;
    }

    @Override
    public boolean isInitComplete() {
        return isPositionReached();
    }

    private boolean isPositionReached() {
        if (leftServo.isPositionReached() && rightServo.isPositionReached() && backServo.isPositionReached()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {
        down();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        loggingOn = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}

