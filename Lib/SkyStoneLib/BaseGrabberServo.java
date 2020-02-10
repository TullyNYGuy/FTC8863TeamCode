package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;


public class BaseGrabberServo implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "BaseGrabberServos";

    enum State {
        UP, GOINGTOUP, GOINGTOGRAB, GRABBED
    }

    final public double leftGrabbingPosition = .05;
    final public double leftUpPosition = .95;
    final public double rightGrabbingPosition = .95;
    final public double rightUpPosition = .05;
    private boolean pendingRelease;
    private Servo8863 leftGrabber;
    private Servo8863 rightGrabber;
    private boolean pendingGrab;
    private Telemetry telemetry;
    private double upRight;
    private double grabRight;
    private double upLeft;
    private double grabLeft;
    private State servoState;
    private ElapsedTime timer;

    /*
     * @param right Right servo
     * @param left Left servo
     * @param telemetry Telemetry object
     * @param inLeft Left servo position IN. IntakePusherServos.leftPushingPosition can be used here
     * @param inRight Right servo position IN. IntakePusherServos.rightPushingPosition can be used here
     * @param outLeft Left servo position OUT. IntakePusherServos.leftIdlePosition can be used here
     * @param outRight Right servo position OUT. IntakePusherServos.rightPushingPosition can be used here
     */
    public BaseGrabberServo(HardwareMap hardwareMap, String rightServoName, String leftServoName, Telemetry telemetry) {
        leftGrabber = new Servo8863(leftServoName, hardwareMap, telemetry, leftUpPosition, leftUpPosition, leftGrabbingPosition, leftUpPosition, Servo.Direction.FORWARD);
        rightGrabber = new Servo8863(rightServoName, hardwareMap, telemetry, rightUpPosition, rightUpPosition, rightGrabbingPosition, rightUpPosition, Servo.Direction.FORWARD);
        //this.left = left;
        //this.right = right;
        this.telemetry = telemetry;
        servoState = State.UP;
        pendingGrab = false;
        pendingRelease = false;
        timer = new ElapsedTime();
    }

    private void setState(State servoState) {
        this.servoState = servoState;

    }

    public boolean isInitComplete() {
        if (servoState == State.UP) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean init(Configuration config) {
        return false;
    }

    public void shutdown() {
        setState(State.UP);
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    public void grabBase() {
        pendingGrab = true;
    }
    public void releaseBase(){pendingRelease = true;}

    // UNFORTUNATELY, there is no real position feedback for a servo. The method getPosition() is
    // misleading. All it does is to return the last position command you sent to the servo. It does
    // NOT get the actual position of the servo. So a state machine is not really useful for
    // controlling position.

    public void update() {
        telemetry.addData("servo states: ", servoState);
        switch (servoState) {

            case UP:
                if (pendingGrab == true) {
                    pendingGrab = false;
                    leftGrabber.setPosition(leftGrabbingPosition);
                    rightGrabber.setPosition(rightGrabbingPosition);
                    setState(State.GOINGTOGRAB);
                    timer.reset();
                }
                break;
            case GOINGTOGRAB:
                if (timer.milliseconds() > 1000) {
                    setState(State.GRABBED);
                    timer.reset();
                }
                break;
            case GOINGTOUP:
                if (timer.milliseconds() > 1000) {
                    setState(State.UP);
                    timer.reset();
                }
                break;
            case GRABBED:
                if(pendingRelease == true){
                    pendingRelease = false;
                    leftGrabber.setPosition(leftUpPosition);
                    rightGrabber.setPosition(rightUpPosition);
                    setState(State.GOINGTOUP);
                    timer.reset();
                }
        }
        telemetry.update();
    }

    public boolean IsUpComplete() {
        if (servoState == State.UP) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isGrabComplete() {
        if (servoState == State.GRABBED) {
            return true;
        } else {
            return false;
        }
    }
}

