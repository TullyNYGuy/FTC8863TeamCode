package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * Created by ball on 10/7/2017.
 */

@TeleOp(name = "Collection Test+State Machine", group = "Test")
//@Disabled

public class CollectionTestStateMachine extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public enum CollectionStates{
        NO_MINERAL,
        INTAKE_ON,
        MINERAL_DETECTED,
        KEEP,
        REJECT
    }
    public ElapsedTime collectionTimer;
    public CollectionStates collectionState;

    DataLogging dataLog = null;

    // GAMEPAD 1

    // declare the buttons on the gamepad as multi push button objects
    public GamepadButtonMultiPush gamepad1RightBumper;
    public GamepadButtonMultiPush gamepad1LeftBumper;
    public GamepadButtonMultiPush gamepad1a;
    public GamepadButtonMultiPush gamepad1b;
    public GamepadButtonMultiPush gamepad1y;
    public GamepadButtonMultiPush gamepad1x;
    public GamepadButtonMultiPush gamepad1DpadUp;
    public GamepadButtonMultiPush gamepad1DpadDown;
    public GamepadButtonMultiPush gamepad1DpadLeft;
    public GamepadButtonMultiPush gamepad1DpadRight;
    public GamepadButtonMultiPush gamepad1LeftStickButton;
    public GamepadButtonMultiPush gamepad1RightStickButton;

    // joystick and joystick value declarations - game pad 1
    final static double JOYSTICK_DEADBAND_VALUE = .15;
    final static double JOYSTICK_HALF_POWER = .5;
    final static double JOYSTICK_QUARTER_POWER = .25;

    JoyStick gamepad1LeftJoyStickX;
    JoyStick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;

    JoyStick gamepad1RightJoyStickX;
    JoyStick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    // GAMEPAD 2

    // declare the buttons on the gamepad as multi push button objects
    public GamepadButtonMultiPush gamepad2RightBumper;
    public GamepadButtonMultiPush gamepad2LeftBumper;
    public GamepadButtonMultiPush gamepad2a;
    public GamepadButtonMultiPush gamepad2b;
    public GamepadButtonMultiPush gamepad2y;
    public GamepadButtonMultiPush gamepad2x;
    public GamepadButtonMultiPush gamepad2DpadUp;
    public GamepadButtonMultiPush gamepad2DpadDown;
    public GamepadButtonMultiPush gamepad2DpadLeft;
    public GamepadButtonMultiPush gamepad2DpadRight;
    public GamepadButtonMultiPush gamepad2LeftStickButton;
    public GamepadButtonMultiPush gamepad2RightStickButton;

    // joystick and joystick value declarations - game pad 2
    JoyStick gamepad2LeftJoyStickX;
    JoyStick gamepad2LeftJoyStickY;
    double gamepad2LeftJoyStickXValue = 0;
    double gamepad2LeftJoyStickYValue = 0;

    JoyStick gamepad2RightJoyStickX;
    JoyStick gamepad2RightJoyStickY;
    double gamepad2RightJoyStickXValue = 0;
    double gamepad2RightJoyStickYValue = 0;

    public CRServo collectionServoLeft;
    public CRServo collectionServoRight;
    public Servo8863 gateServo;
    public CRServo decisionStar;

    // get a reference to the color sensor.
    ColorSensor sensorColor;
    DistanceSensor sensorDistance;


    double red = 0;
    double blue = 0;
    double green = 0;
    double argb = 0;
    double distance = 0;

    float hsvValues[] = {0F, 0F, 0F};

    // values is a reference to the hsvValues array.
    final float values[] = hsvValues;

    // sometimes it helps to multiply the raw RGB values with a scale factor
// to amplify/attentuate the measured values.
    final double SCALE_FACTOR = 255;
    DataLogging dataLogging;


    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);

        // create the gamepad 1 buttons and tell each button how many commands it has
        gamepad1RightBumper = new GamepadButtonMultiPush(1);
        gamepad1LeftBumper = new GamepadButtonMultiPush(1);
        gamepad1a = new GamepadButtonMultiPush(2);
        gamepad1b = new GamepadButtonMultiPush(2);
        gamepad1y = new GamepadButtonMultiPush(1);
        gamepad1x = new GamepadButtonMultiPush(1);
        gamepad1DpadUp = new GamepadButtonMultiPush(1);
        gamepad1DpadDown = new GamepadButtonMultiPush(1);
        gamepad1DpadLeft = new GamepadButtonMultiPush(1);
        gamepad1DpadRight = new GamepadButtonMultiPush(1);
        gamepad1LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad1RightStickButton = new GamepadButtonMultiPush(1);

        // Game Pad 1 joysticks
        gamepad1LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad1RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        // create the gamepad 2 buttons and tell each button how many commands it has
        gamepad2RightBumper = new GamepadButtonMultiPush(1);
        gamepad2LeftBumper = new GamepadButtonMultiPush(1);
        gamepad2a = new GamepadButtonMultiPush(2);
        gamepad2b = new GamepadButtonMultiPush(2);
        gamepad2y = new GamepadButtonMultiPush(1);
        gamepad2x = new GamepadButtonMultiPush(1);
        gamepad2DpadUp = new GamepadButtonMultiPush(1);
        gamepad2DpadDown = new GamepadButtonMultiPush(1);
        gamepad2DpadLeft = new GamepadButtonMultiPush(1);
        gamepad2DpadRight = new GamepadButtonMultiPush(1);
        gamepad2LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad2RightStickButton = new GamepadButtonMultiPush(1);

        // Game Pad 2 joysticks
        gamepad2LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad2RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        collectionServoLeft = hardwareMap.get(CRServo.class, "collectionServoLeft");
        collectionServoRight = hardwareMap.get(CRServo.class, "collectionServoRight");
        collectionServoRight.setDirection(CRServo.Direction.REVERSE);
        decisionStar = hardwareMap.get(CRServo.class, "decisionStar");
        gateServo = new Servo8863("gateServo", hardwareMap, telemetry, 0.70, 1, 0, 0.52, Servo.Direction.FORWARD);
        //collectionServoRight = new CRServo8863("collectionServoRight", hardwareMap, .50, .50, .1, Servo.Direction.REVERSE, telemetry);
        sensorColor = hardwareMap.get(ColorSensor.class, "revColorSensor");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "revColorSensor");
        dataLogging = new DataLogging("revColorDistanceSensorReadings", telemetry);
        collectionTimer = new ElapsedTime();
        collectionState = CollectionStates.NO_MINERAL;

        // Wait for the start button
        telemetry.addData(">", "Press start to run Test");
        telemetry.update();
        waitForStart();
        dataLogging.startTimer();
        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        // set the positions that the various systems need to be in when the robot is running

        while (opModeIsActive()) {
            red = sensorColor.red();
            green = sensorColor.green();
            blue = sensorColor.blue();
            distance = sensorDistance.getDistance(DistanceUnit.CM);
            automaticStarShutoff();
            // convert the RGB values to HSV values.
            // multiply by the SCALE_FACTOR.
            // then cast it back to int (SCALE_FACTOR is a double)
            Color.RGBToHSV((int) (red * SCALE_FACTOR),
                    (int) (green * SCALE_FACTOR),
                    (int) (blue * SCALE_FACTOR),
                    hsvValues);
            dataLogging.logData(distance + " " + hsvValues[0]);
            // send the info back to driver station using telemetry function.
            telemetry.addData("Distance (cm)", distance);
            //telemetry.addData("Red  ", red);
            //telemetry.addData("Green", green);
            //telemetry.addData("Blue ", blue);
            telemetry.addData("Hue", hsvValues[0]);


            //*************************************************************************************
            // Gamepad 1 buttons
            //*************************************************************************************

            // example for a button with multiple commands attached to it:
            // don't forget to change the new line with the number of commands attached like this:
            // gamepad1x = new GamepadButtonMultiPush(4);
            //                                        ^
            //                                        |
            //
//            if (gamepad1x.buttonPress(gamepad1.x)) {
//                if (gamepad1x.isCommand1()) {
//                    // call the first command you want to run
//                }
//                if (gamepad1x.isCommand2()) {
//                    // call the 2nd command you want to run
//                }
//                if (gamepad1x.isCommand3()) {
//                    // call the 3rd command you want to run
//                }
//                if (gamepad1x.isCommand4()) {
//                    // call the 4th command you want to run
//                }
//            }

            if (gamepad1RightBumper.buttonPress(gamepad1.right_bumper)) {
                //gateServo.goUp();

            }

            if (gamepad1LeftBumper.buttonPress(gamepad1.left_bumper)) {
                //gateServo.goDown();
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1a.buttonPress(gamepad1.a)) {
                if (gamepad1a.isCommand1()) {
                    //collectionServoLeft.setPower(1);
                    //collectionServoRight.setPower(1);
                }
                if (gamepad1a.isCommand2()) {
                    //collectionServoLeft.setPower(0);
                   // collectionServoRight.setPower(0);
                }
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1b.buttonPress(gamepad1.b)) {
                if (gamepad1b.isCommand1()) {
                   // gateServo.goHome();
                }
                if (gamepad1b.isCommand2()) {
                   // gateServo.goHome();
                }
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1y.buttonPress(gamepad1.y)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1x.buttonPress(gamepad1.x)) {
                decisionStar.setPower(0);
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
                decisionStar.setPower(1);
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
                decisionStar.setPower(-1);
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1DpadRight.buttonPress(gamepad1.dpad_right)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1LeftStickButton.buttonPress(gamepad1.left_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1RightStickButton.buttonPress(gamepad1.right_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            //**************************************************************************************
            // Gamepad 1 joysticks
            //**************************************************************************************

            gamepad1LeftJoyStickXValue = gamepad1LeftJoyStickX.scaleInput(gamepad1.left_stick_x);
            gamepad1LeftJoyStickYValue = gamepad1LeftJoyStickY.scaleInput(gamepad1.left_stick_y);

            gamepad1RightJoyStickXValue = gamepad1RightJoyStickX.scaleInput(gamepad1.right_stick_x);
            gamepad1RightJoyStickYValue = gamepad1RightJoyStickY.scaleInput(gamepad1.right_stick_y);

            //**************************************************************************************
            // Gamepad 2 buttons
            //**************************************************************************************

            // example for a button with multiple commands attached to it:
            // don't forget to change the new line with the number of commands attached like this:
            // gamepad1x = new GamepadButtonMultiPush(4);
            //                                        ^
            //                                        |
            //
//            if (gamepad1x.buttonPress(gamepad1.x)) {
//                if (gamepad1x.isCommand1()) {
//                    // call the first command you want to run
//                }
//                if (gamepad1x.isCommand2()) {
//                    // call the 2nd command you want to run
//                }
//                if (gamepad1x.isCommand3()) {
//                    // call the 3rd command you want to run
//                }
//                if (gamepad1x.isCommand4()) {
//                    // call the 4th command you want to run
//                }
//            }

            if (gamepad2RightBumper.buttonPress(gamepad2.right_bumper)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2LeftBumper.buttonPress(gamepad2.left_bumper)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2a.buttonPress(gamepad2.a)) {
                // this was a new button press, not a button held down for a while
                if (gamepad2a.isCommand1()) {

                }
                if (gamepad2a.isCommand2()) {

                }

                // put the command to be executed here

            }

            if (gamepad2b.buttonPress(gamepad2.b)) {
                if (gamepad2b.isCommand1()) {
                    // call the first command you want to run

                }
                if (gamepad2b.isCommand2()) {
                    // call the 2nd command you want to run

                }
            }

            if (gamepad2y.buttonPress(gamepad2.y)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2x.buttonPress(gamepad2.x)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2DpadUp.buttonPress(gamepad2.dpad_up)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2DpadDown.buttonPress(gamepad2.dpad_down)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2DpadLeft.buttonPress(gamepad2.dpad_left)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2DpadRight.buttonPress(gamepad2.dpad_right)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2LeftStickButton.buttonPress(gamepad2.left_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2RightStickButton.buttonPress(gamepad2.right_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            //**************************************************************************************
            // Gamepad 2 joysticks
            //**************************************************************************************

            gamepad2LeftJoyStickXValue = gamepad2LeftJoyStickX.scaleInput(gamepad2.left_stick_x);
            gamepad2LeftJoyStickYValue = gamepad2LeftJoyStickY.scaleInput(gamepad2.left_stick_y);

            gamepad2RightJoyStickXValue = gamepad2RightJoyStickX.scaleInput(gamepad2.right_stick_x);
            gamepad2RightJoyStickYValue = gamepad2RightJoyStickY.scaleInput(gamepad2.right_stick_y);

            // Display telemetry
            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();
            update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************
    public void automaticStarShutoff()

    {
        if (distance < 10) {
            collectionServoLeft.setPower(0);
            collectionServoRight.setPower(0);
        }
    }
    public void update(){
        switch (collectionState){
            case NO_MINERAL:
                if (gamepad1.a){
                    collectionState= CollectionStates.INTAKE_ON;
                    collectionServoLeft.setPower(1);
                    collectionServoRight.setPower(1);
                }
                 gateServo.goHome();
                telemetry.addData("state= ",collectionState.toString());
                break;
            case INTAKE_ON:
                if (gamepad1.b){
                    collectionState= CollectionStates.NO_MINERAL;
                    collectionServoLeft.setPower(0);
                    collectionServoRight.setPower(0);
                }
                if ( distance < 10){
                    collectionState= CollectionStates.MINERAL_DETECTED;
                    collectionServoLeft.setPower(0);
                    collectionServoRight.setPower(0);
                }
                telemetry.addData("state= ",collectionState.toString());
                break;
            case MINERAL_DETECTED:
                if (gamepad1.left_bumper){
                    gateServo.goDown();
                    decisionStar.setPower(1);
                    collectionTimer.reset();
                    collectionState= CollectionStates.KEEP;
                }
                telemetry.addData("state= ",collectionState.toString());
                if (gamepad1.right_bumper){
                    gateServo.goUp();
                    collectionServoLeft.setPower(1);
                    collectionServoRight.setPower(1);
                    collectionTimer.reset();
                    collectionState= CollectionStates.REJECT;
                }
                telemetry.addData("state= ",collectionState.toString());
                break;
            case KEEP:
                if (collectionTimer.milliseconds()>1500){
                    gateServo.goHome();
                    decisionStar.setPower(0);
                    collectionServoLeft.setPower(0);
                    collectionServoRight.setPower(0);
                    collectionState=CollectionStates.NO_MINERAL;
                }
                telemetry.addData("state= ",collectionState.toString());
                break;
            case REJECT:
                if (collectionTimer.milliseconds()>1500){
                    gateServo.goHome();
                    decisionStar.setPower(0);
                    collectionServoLeft.setPower(0);
                    collectionServoRight.setPower(0);
                    collectionState=CollectionStates.NO_MINERAL;
                }
                telemetry.addData("state= ",collectionState.toString());
                break;
        }
    }
}


