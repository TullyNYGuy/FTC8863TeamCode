package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.DualLift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Dual Lift Joystick", group = "Test")
//@Disabled
public class TestDualLiftJoystick extends LinearOpMode {

    // Put your variable declarations here
    JoyStick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickYValue = 0;
    final static double JOYSTICK_DEADBAND_VALUE = .15;

    public DualLift lift;

    public Lift.ExtensionRetractionStates extensionRetractionStateRight;
    public Lift.ExtensionRetractionStates extensionRetractionStateLeft;

    public int encoderValueRight = 0;
    public int encoderValueMaxRight = 0;
    public int encoderValueMinRight = 0;

    public int encoderValueLeft = 0;
    public int encoderValueMaxLeft = 0;
    public int encoderValueMinLeft = 0;

    public DataLogging logFile;

    public CSVDataFile timeEncoderValueFile;

    public String buffer = "";

    public double speed = 0.1;

    @Override
    public void runOpMode() {

        // Put your initializations here
        lift = new DualLift(hardwareMap,
                SkystoneRobot.HardwareName.LIFT_RIGHT_NAME.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_MOTOR.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_RETRACTION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_NAME.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_MOTOR.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_RETRACTION_SWITCH.hwName,
                telemetry);

        gamepad1LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        logFile = new DataLogging("TestDualLiftJoystick", telemetry);

        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        lift.setDataLog(logFile);
        lift.enableDataLogging();
        lift.enableCollectData("dualLiftTimeEncoderValues");
        lift.setRetractionPower(-speed);
        lift.setExtensionPower(+speed);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        // resetting the lift requires the state machine to update
        lift.reset();
        while (opModeIsActive() && !lift.isResetComplete()) {
            lift.update();
        }

        sleep(1000);

        while (opModeIsActive()) {
            lift.update();

            //gamepad1LeftJoyStickYValue = gamepad1LeftJoyStickY.scaleInput(gamepad1.left_stick_y);
            gamepad1LeftJoyStickYValue = .5;
            lift.setPowerUsingJoystick(gamepad1LeftJoyStickYValue);

            telemetry.addData("", lift.stateToString());
            telemetry.addData("", lift.encoderValuesToString());
            telemetry.addData("joystick = ", gamepad1LeftJoyStickYValue);
            telemetry.update();
            idle();
        }

        telemetry.addData("", "DONE!");
        telemetry.update();

        lift.shutdown();
    }
}
