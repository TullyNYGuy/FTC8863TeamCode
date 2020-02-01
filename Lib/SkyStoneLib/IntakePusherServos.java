package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;


public class IntakePusherServos implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "IntakePusherServos";

    enum State {
        OUT, MOVINGIN, MOVINGOUT
    }

    final public double leftPushingPosition = 0.34;
    final public double leftIdlePosition = 0.05;
    final public double rightPushingPosition = 0.60;
    final public double rightIdlePosition = 0.95;

    private Servo8863 leftPusher;
    private Servo8863 rightPusher;
    private boolean pendingPush;
    private Telemetry telemetry;
    private double inRight;
    private double outRight;
    private double inLeft;
    private double outLeft;
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
    public IntakePusherServos(HardwareMap hardwareMap, String rightServoName, String leftServoName, Telemetry telemetry) {
        leftPusher = new Servo8863(leftServoName, hardwareMap, telemetry, leftIdlePosition, leftPushingPosition, leftIdlePosition, leftIdlePosition, Servo.Direction.FORWARD);
        rightPusher = new Servo8863(rightServoName, hardwareMap, telemetry, rightIdlePosition, rightPushingPosition, rightIdlePosition, rightIdlePosition, Servo.Direction.FORWARD);
        //this.left = left;
        //this.right = right;
        this.telemetry = telemetry;
        servoState = State.OUT;
        pendingPush = false;
        timer = new ElapsedTime();
    }

    private void setState(State servoState) {
        this.servoState = servoState;

    }

    public boolean isInitComplete() {
        if (servoState == State.OUT) {
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
        setState(State.OUT);
    }

    public void pushIn() {
        pendingPush = true;
    }

    // UNFORTUNATELY, there is no real position feedback for a servo. The method getPosition() is
    // misleading. All it does is to return the last position command you sent to the servo. It does
    // NOT get the actual position of the servo. So a state machine is not really useful for
    // controlling position.

    public void update() {
        telemetry.addData("servo states: ", servoState);
        switch (servoState) {

            case OUT:
                if (pendingPush == true) {
                    pendingPush = false;
                    //left.setPosition(inLeft);
                    //right.setPosition(inRight);
                    setState(State.MOVINGIN);
                    timer.reset();
                }
                break;
            case MOVINGIN:
                if (timer.milliseconds() > 1000) {
                    setState(State.MOVINGOUT);
                    //right.setPosition(outRight);
                    //left.setPosition(outLeft);
                    timer.reset();
                }
                break;
            case MOVINGOUT:
                if (timer.milliseconds() > 1000) {
                    setState(State.OUT);
                    timer.reset();
                }
                break;
        }
        telemetry.update();
    }

    public boolean IsMoveOutComplete() {
        if (servoState == State.OUT) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPushComplete() {
        if (servoState == State.MOVINGIN) {
            return true;
        } else {
            return false;
        }
    }
}

