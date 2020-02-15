package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArmConstants;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Extension Arm Joystick", group = "Test")
//@Disabled
public class TestExtensionArmJoystick extends LinearOpMode {

    public enum Steps {
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        ELEVEN,
        TWELVE,
        THIRTEEN
    }

    public Steps steps = Steps.ZERO;
    public ElapsedTime stateTimer;
    public double waitTime = 2000; // milliseconds

    // Put your variable declarations here
    public ExtensionArm extensionArm;

    public ExtensionRetractionMechanism.ExtensionRetractionStates extensionArmState;

    public int encoderValue = 0;

    public DataLogging logFile;
    public CSVDataFile timeEncoderValueFile;

    public ElapsedTime timer;
    public double startTime = 0;

    public String buffer = "";

    public double speed = 1;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionArm = new ExtensionArm(hardwareMap, telemetry,
                ExtensionArmConstants.mechanismName,
                SkystoneRobot.HardwareName.EXT_ARM_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.EXT_ARM_RETRACTION_SWITCH.hwName,
                SkystoneRobot.HardwareName.EXT_ARM_MOTOR_NAME_FOR_ENCODER_PORT.hwName,
                ExtensionArmConstants.motorType,
                ExtensionArmConstants.movementPerRevolution);

        timer = new ElapsedTime();
        stateTimer = new ElapsedTime();

        logFile = new DataLogging("TestExtensionArmJoystick", telemetry);
        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues");


        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        extensionArm.setDataLog(logFile);
        extensionArm.enableDataLogging();
        extensionArm.enableCollectData();
        extensionArm.setRetractionPower(-speed);
        extensionArm.setExtensionPower(+speed);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();

        while (opModeIsActive()) {
            extensionArm.update();
            extensionArmState = extensionArm.getExtensionRetractionState();

            switch (steps) {
                case ZERO:
                    extensionArm.init();
                    steps = Steps.ONE;
                    break;
                case ONE:
                    if (extensionArm.isInitComplete()) {
                        extensionArm.goToPosition(10, speed);
                        steps = Steps.TWO;
                    }
                    break;
                case TWO:
                    if (extensionArm.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.THREE;
                        //break out of loop
                        break;
                    }
                    break;
                case THREE:
                    break;
                case FOUR:
                    break;
                case FIVE:
                    break;
                case SIX:
                    break;
                case SEVEN:
                    break;
                case EIGHT:
                    break;
                case NINE:
                    break;
                case TEN:
                    break;
                case ELEVEN:
                    break;
                case TWELVE:
                    break;

            }

            extensionArm.testJoystick(this);

            telemetry.addData("STEP = ", steps.toString());
            telemetry.addData("STATE = ", extensionArmState.toString());
            telemetry.addData("ENCODER = ", encoderValue);
            telemetry.update();
            idle();
        }
    }

    // Put your cleanup code here - it runs as the application shuts down
}
