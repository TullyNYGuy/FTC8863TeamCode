package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.DualLift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Dual Lift Reset", group = "Test")
//@Disabled
public class TestDualLiftResetCode extends LinearOpMode {

    // Put your variable declarations here

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

    public ElapsedTime timer;

    public double startTime = 0;

    public double endUpTimeRight = 0;
    public double endDownTimeRight = 0;

    public double endUpTimeLeft = 0;
    public double endDownTimeLeft = 0;

    public String buffer = "";

    public double speed = 1.0;

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

        timer = new ElapsedTime();

        logFile = new DataLogging("ResetTestDualLift", telemetry);

        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        lift.setDataLog(logFile);
        lift.enableDataLogging();
        lift.enableCollectData("dualLiftTimeEncoderValues");
        lift.setResetPower(-0.1);
        lift.setRetractionPower(-speed);
        lift.setExtensionPower(+speed);
        lift.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        lift.reset();

        timer.reset();

        while (opModeIsActive() && !lift.isResetComplete()) {
            lift.update();

            telemetry.addData("", lift.stateToString());
            telemetry.addData("", lift.encoderValuesToString());
            telemetry.addData("", lift.resetStateToString());
            telemetry.update();
            idle();
        }

        telemetry.addData("Left", " lift");
        telemetry.addData("Tension Complete encoder = ", lift.getLiftLeftTensionCompleteEncoderValue());

        // have to update the state machine in order to generate the last state update
        lift.update();

        buffer = String.format(String.format("%.2f", endUpTimeRight));
        telemetry.addData("DONE! time up = ", buffer);
        telemetry.update();

        // wait for user to kill the app
        while (opModeIsActive()) {
            idle();
        }

        lift.shutdown();
    }
}
