package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;


public class FoundationGrabberServo implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "BaseGrabberServos";

    final public double leftGrabbingPosition = .05;
    final public double leftInitPosition = .95;
    final public double leftReleasePosition = .95;
    final public double rightGrabbingPosition = .05;
    final public double rightInitPosition = .95;
    final public double rightReleasePosition = .95;

    final private String initPositionName = "init";
    final private String grabPositionName = "grab";
    final private String releasePositionName = "release";

    private Servo8863New leftGrabber;
    private Servo8863New rightGrabber;

    private DataLogging logFile = null;
    private boolean loggingOn = false;

    /**
     * The foundation grabber has 2 servos in it. One on the left and one on the right
     *
     * @param hardwareMap
     * @param rightServoName
     * @param leftServoName
     * @param telemetry
     */
    public FoundationGrabberServo(HardwareMap hardwareMap, String rightServoName, String leftServoName, Telemetry telemetry) {
        leftGrabber = new Servo8863New(leftServoName, hardwareMap, telemetry);
        leftGrabber.addPosition(initPositionName, leftInitPosition, 1000, TimeUnit.MILLISECONDS);
        leftGrabber.addPosition(grabPositionName, leftGrabbingPosition, 1000, TimeUnit.MILLISECONDS);
        leftGrabber.addPosition(releasePositionName, leftReleasePosition, 1000, TimeUnit.MILLISECONDS);

        rightGrabber = new Servo8863New(rightServoName, hardwareMap, telemetry);
        rightGrabber.addPosition(initPositionName, rightInitPosition, 1000, TimeUnit.MILLISECONDS);
        rightGrabber.addPosition(grabPositionName, rightGrabbingPosition, 1000, TimeUnit.MILLISECONDS);
        rightGrabber.addPosition(releasePositionName, rightReleasePosition, 1000, TimeUnit.MILLISECONDS);
        rightGrabber.setDirection(Servo.Direction.REVERSE);
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

    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(stringToLog);

        }
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean init(Configuration config) {
        log("foundation grabber commanded to init");
        leftGrabber.setPosition(initPositionName);
        rightGrabber.setPosition(initPositionName);
        return false;
    }

    @Override
    public boolean isInitComplete() {
        if (leftGrabber.isPositionReached() && rightGrabber.isPositionReached()) {
            log("foundation grabber init complete");
            return true;
        } else {
            return false;
        }
    }

    public void shutdown() {
        log("foundation grabber commanded to shutdown");
        leftGrabber.setPosition(initPositionName);
        rightGrabber.setPosition(initPositionName);
    }

    @Override
    public void update() {
        // no state machine in this ojbect
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    public void grabBase() {
        log("foundation grabber commanded to grab");
        rightGrabber.setPosition(grabPositionName);
        leftGrabber.setPosition(grabPositionName);
    }

    public boolean isGrabBaseComplete() {
        if (rightGrabber.isPositionReached() && leftGrabber.isPositionReached()) {
            return true;
        } else {
            return false;
        }
    }

    public void releaseBase() {
        log("foundation grabber commanded to release");
        rightGrabber.setPosition(releasePositionName);
        leftGrabber.setPosition(releasePositionName);
    }

    public boolean isReleaseBaseComplete() {
        if (rightGrabber.isPositionReached() && leftGrabber.isPositionReached()) {
            return true;
        } else {
            return false;
        }
    }
}


