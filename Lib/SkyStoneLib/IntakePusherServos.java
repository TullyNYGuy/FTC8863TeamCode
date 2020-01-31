package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

enum State {
    IN, OUT, MOVINGIN, MOVINGOUT
}

public class IntakePusherServos {
    final private double TOLERANCE = 0.05;
    private Servo left;
    private boolean pendingPush;
    private Servo right;
    private Telemetry telemetry;
    private double inRight;
    private double outRight;
    private double inLeft;
    private double outLeft;
    private State servoState;

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
                if (Math.abs(left.getPosition() - inLeft) < TOLERANCE && Math.abs(right.getPosition() - inRight) < TOLERANCE) {
                    setState(State.MOVINGOUT);
                    right.setPosition(outRight);
                    left.setPosition(outLeft);
                }
                break;
            case MOVINGOUT:
                if (Math.abs(left.getPosition() - outLeft) < TOLERANCE && Math.abs(right.getPosition() - outRight) < TOLERANCE) {
                    setState(State.OUT);
                }
                break;
        }
        telemetry.update();
    }
}
