package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

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
    private JoyStick yJoystick;
    private JoyStick xJoystick;
    private JoyStick speedOfRotationJoystick;
    private Gamepad gamepad;
    private double adjustAngle = 0;
    private Mode mode = Mode.DRIVER_MODE;
    private AdafruitIMU8863 imu;
    private double heading = 0;
    private boolean modeButton = false;

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

    public HaloControls(Gamepad gamepad, AdafruitIMU8863 imu) {
        this.gamepad = gamepad;
        xJoystick = new JoyStick(gamepad, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.X);
        yJoystick = new JoyStick(gamepad, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.Y);
        speedOfRotationJoystick = new JoyStick(gamepad, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.X);
        this.imu = imu;

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
        if (translationSpeed > 1) {
            translationSpeed = 1;
        }
        commands.setAngleOfTranslation(angleOfTranslation);
        commands.setSpeed(translationSpeed);
        commands.setSpeedOfRotation(rValue);
        heading = Math.toRadians(imu.getHeading());
        /*
         * b button on the gamepad toggles between driver point of view mode (angles are based
         * on coordinate system relative to field) and robot point of view mode (angles are based
         * on coordinate system relative to the robot)
         */
        if (gamepad.b && !modeButton)
            toggleMode();
        modeButton = gamepad.b;
        /*
         * y button resets the coordinate system for the driver point of view to the same as the
         * the robot based coordinate system at the time the y button is pressed. After that
         * the coordinate system is based off the coordinate system in effect when the y button
         * was pressed.
         */
        if (gamepad.y)

            resetHeading();
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        if (imu != null) {
            this.mode = mode;
            if (mode == Mode.DRIVER_MODE) {
                adjustAngle = heading - adjustAngle;
            }
        } else {
            this.mode = Mode.ROBOT_MODE;

        }

    }

    public void toggleMode() {
        if (imu != null) {
            if (mode == Mode.DRIVER_MODE) {
                mode = Mode.ROBOT_MODE;
            } else {
                mode = Mode.DRIVER_MODE;
                // get the difference in angle between the robot referenced coordinate system and the
                // driver / field referenced coordinate system
                adjustAngle = heading - adjustAngle;
            }
        }
    }

    public void resetHeading() {
        adjustAngle = heading;
    }

}
