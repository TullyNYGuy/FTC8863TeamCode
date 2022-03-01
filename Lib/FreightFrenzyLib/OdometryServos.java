package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;

public class OdometryServos implements FTCRobotSubsystem {

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
    private final String SERVO_SYSTEM_NAME = "Odometry Raising Servos";
    private final String  LEFT_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.LEFT_SERVO.hwName;
    private final String  RIGHT_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.RIGHT_SERVO.hwName;
    private final String  BACK_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.BACK_SERVO.hwName;
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

    public OdometryServos(HardwareMap hardwareMap, Telemetry telemetry) {
        isInitComplete = false;
        /*clawServo = new Servo8863New(CLAW_SERVO_NAME, hardwareMap, telemetry);
        clawServo.addPosition("open", .0, 1000, TimeUnit.MILLISECONDS);
        clawServo.addPosition("open plus delay", .0, 500, 1000, TimeUnit.MILLISECONDS);
        clawServo.addPosition("close", .58,1000, TimeUnit.MILLISECONDS);
        clawServo.addPosition("close plus delay", .58, 500, 1000, TimeUnit.MILLISECONDS);
        close();*/
        leftServo = new Servo8863New(LEFT_SERVO_NAME, hardwareMap, telemetry);
        rightServo = new Servo8863New(RIGHT_SERVO_NAME, hardwareMap, telemetry);
        backServo = new Servo8863New(BACK_SERVO_NAME, hardwareMap, telemetry);

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

   /* public void open() {
        clawServo.setPosition("open");
    }

    public void openPlusDelay() {
        clawServo.setPosition("open plus delay");
    }

    public void close() {
        clawServo.setPosition("close");
    }

    public void closePlusDelay(){clawServo.setPosition("close plus delay");}

    public boolean isPositionReached() {
        return clawServo.isPositionReached();
    }
*/
    public void raiseOdometry(){}
    public void lowerOdometry(){}

    @Override
    public String getName() {
        return SERVO_SYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return isInitComplete;
    }

    @Override
    public boolean init(Configuration config) {
        isInitComplete = true;
        return isInitComplete;
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {

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

