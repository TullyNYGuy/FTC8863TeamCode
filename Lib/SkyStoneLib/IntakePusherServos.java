package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;


public class IntakePusherServos {
    enum State {
        OUT, MOVINGIN, MOVINGOUT
    }

    final private double POSITION_TOLERANCE = 0.05;
    final public double DEFAULT_LEFT_POSITION_IN = 0.34;
    final public double DEFAULT_LEFT_POSITION_OUT = 0.05;
    final public double DEFAULT_RIGHT_POSITION_IN = 0.60;
    final public double DEFAULT_RIGHT_POSITION_OUT = 0.95;

    private Servo left;
    private Servo right;
    private boolean pendingPush;
    private Telemetry telemetry;
    private double inRight;
    private double outRight;
    private double inLeft;
    private double outLeft;
    private State servoState;

    /*
     * @param right Right servo
     * @param left Left servo
     * @param telemetry Telemetry object
     * @param inLeft Left servo position IN. IntakePusherServos.DEFAULT_LEFT_POSITION_IN can be used here
     * @param inRight Right servo position IN. IntakePusherServos.DEFAULT_RIGHT_POSITION_IN can be used here
     * @param outLeft Left servo position OUT. IntakePusherServos.DEFAULT_LEFT_POSITION_OUT can be used here
     * @param outRight Right servo position OUT. IntakePusherServos.DEFAULT_RIGHT_POSITION_IN can be used here
     */
    public IntakePusherServos(Servo right, Servo left, Telemetry telemetry, double inLeft, double inRight, double outLeft, double outRight) {
        this.left = left;
        this.right = right;
        this.telemetry = telemetry;
        this.inLeft = inLeft;
        this.inRight = inRight;
        this.outLeft = outLeft;
        this.outRight = outRight;
        servoState = State.OUT;
        pendingPush = false;
    }

    private void setState(State servoState) {
        this.servoState = servoState;

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
                    left.setPosition(inLeft);
                    right.setPosition(inRight);
                    setState(State.MOVINGIN);
                }
                break;
            case MOVINGIN:
                if (Math.abs(left.getPosition() - inLeft) < POSITION_TOLERANCE && Math.abs(right.getPosition() - inRight) < POSITION_TOLERANCE) {
                    setState(State.MOVINGOUT);
                    right.setPosition(outRight);
                    left.setPosition(outLeft);
                }
                break;
            case MOVINGOUT:
                if (Math.abs(left.getPosition() - outLeft) < POSITION_TOLERANCE && Math.abs(right.getPosition() - outRight) < POSITION_TOLERANCE) {
                    setState(State.OUT);
                }
                break;
        }
        telemetry.update();
    }
}
