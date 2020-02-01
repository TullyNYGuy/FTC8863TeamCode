package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.Gamepad;

public class SmartJoystick extends JoyStick {

    public enum JoystickSide {
        LEFT, RIGHT
    }

    public enum JoystickAxis {
        X, Y
    }

    private JoystickSide joystickSide = JoystickSide.LEFT;

    private JoystickAxis joystickAxis = JoystickAxis.Y;

    private Gamepad gamepad = null;

    public SmartJoystick(Gamepad gamepad, JoystickSide joystickSide, JoystickAxis joystickAxis) {
        super(JoyStickMode.SQUARE, 0.15, (joystickAxis == JoystickAxis.X) ? InvertSign.NO_INVERT_SIGN : InvertSign.INVERT_SIGN, 1);
        this.joystickAxis = joystickAxis;
        this.joystickSide = joystickSide;
        this.gamepad = gamepad;
    }

    public JoystickSide getJoystickSide() {
        return joystickSide;
    }

    public void setJoystickSide(JoystickSide joystickSide) {
        this.joystickSide = joystickSide;
    }

    public JoystickAxis getJoystickAxis() {
        return joystickAxis;
    }

    public void setJoystickAxis(JoystickAxis joystickAxis) {
        this.joystickAxis = joystickAxis;
    }

    public Gamepad getGamepad() {
        return gamepad;
    }

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    public double getValue() {
        double joystickValue = 0;
        if (joystickAxis == JoystickAxis.X) {
            if (joystickSide == JoystickSide.LEFT) {
                joystickValue = gamepad.left_stick_x;
            } else {
                joystickValue = gamepad.right_stick_x;
            }
        } else {
            if (joystickSide == JoystickSide.LEFT) {
                joystickValue = gamepad.left_stick_y;
            } else {
                joystickValue = gamepad.right_stick_y;
            }
        }
        return scaleInput(joystickValue);
    }


}
