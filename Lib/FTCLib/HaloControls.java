package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class HaloControls {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Mode {
        DRIVER_MODE,
        ROBOT_MODE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private SmartJoystick yJoystick;
    private SmartJoystick xJoystick;
    private SmartJoystick speedOfRotationJoystick;
    protected Gamepad gamepad;
    private double adjustAngle = 0;
    private Mode mode = Mode.DRIVER_MODE;
    private double heading = 0;
    private boolean modeButton = false;
    private FTCRobot robot;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private commands fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public HaloControls(Gamepad gamepad, FTCRobot robot) {
        this.gamepad = gamepad;
        xJoystick = new SmartJoystick(gamepad, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.X);
        yJoystick = new SmartJoystick(gamepad, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);
        speedOfRotationJoystick = new SmartJoystick(gamepad, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        this.robot = robot;

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
    public void calculateMecanumCommands(MecanumCommands commands) {
        if (commands == null)
            return;
        heading = robot.getCurrentRotation(AngleUnit.RADIANS);
        /*
         * b button on the gamepad toggles between driver point of view mode (angles are based
         * on coordinate system relative to field) and robot point of view mode (angles are based
         * on coordinate system relative to the robot)
         */
        if (gamepad.dpad_up && !modeButton)
            toggleMode();
        modeButton = gamepad.dpad_up;
        /*
         * y button resets the coordinate system for the driver point of view to the same as the
         * the robot based coordinate system at the time the y button is pressed. After that
         * the coordinate system is based off the coordinate system in effect when the y button
         * was pressed.
         */
        if (gamepad.dpad_down)
            resetHeading();

        double yValue = yJoystick.getValue();
        double xValue = xJoystick.getValue();
        double rValue = speedOfRotationJoystick.getValue();
        double translationSpeed = java.lang.Math.hypot(xValue, yValue);
        // Divide pi by 2 to shift axis. add pi to get correct range
        double angleOfTranslation = (java.lang.Math.atan2(yValue, xValue));
        if (angleOfTranslation > Math.PI / 2 && angleOfTranslation <= Math.PI) {
            angleOfTranslation = angleOfTranslation - (Math.PI / 2);
        } else {
            angleOfTranslation = angleOfTranslation + 3 * Math.PI / 2;
        }
        if (mode == Mode.DRIVER_MODE)
            angleOfTranslation -= heading - adjustAngle;

        if (translationSpeed > 1) {
            translationSpeed = 1;
        }
        commands.setAngleOfTranslation(AngleUnit.RADIANS, angleOfTranslation);
        commands.setSpeed(translationSpeed);
        commands.setSpeedOfRotation(rValue);
        heading = robot.getCurrentRotation(AngleUnit.RADIANS);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.DRIVER_MODE) {
            adjustAngle = heading - adjustAngle;
        }
    }

    public void toggleMode() {
        if (mode == Mode.DRIVER_MODE) {
            mode = Mode.ROBOT_MODE;
        } else {
            mode = Mode.DRIVER_MODE;
            // get the difference in angle between the robot referenced coordinate system and the
            // driver / field referenced coordinate system
            adjustAngle = heading - adjustAngle;
        }
    }

    public void resetHeading() {
        adjustAngle = heading;
    }

}
