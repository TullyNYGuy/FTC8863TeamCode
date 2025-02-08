package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Intake;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.Color.RED;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeColorSensor;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeSweeperVertical;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Intake Vertical", group = "Test")
@Disabled
public class IDTTestIntakeVertical extends LinearOpMode {

    // Put your variable declarations here
    public ITDIntakeSweeperVertical intakeSweeperVertical;
    public ITDIntakeColorSensor intakeColorSensor;
    public ElapsedTime timer;

    public boolean delayStarted = false;
    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperVertical = new ITDIntakeSweeperVertical(hardwareMap, telemetry);
        intakeColorSensor = new ITDIntakeColorSensor(hardwareMap, telemetry, "intakeColorSensorV3Left");
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            intakeSweeperVertical.update();
            if (gamepad1.x) {
                intakeSweeperVertical.intake();
                delayStarted = false;
            }
            if (gamepad1.a) {
                intakeSweeperVertical.stop();
            }
            if (gamepad1.b) {
                intakeSweeperVertical.outtake();
            }
            if (gamepad1.y) {
                // does nothing
            }

            if (intakeColorSensor.getDistance() < 2.5 &&
                    (intakeColorSensor.getColor() == Color.BLUE || intakeColorSensor.getColor() == RED)
                    && delayStarted == false) {
                intakeSweeperVertical.stop();
                delayStarted = true;
            }

            intakeColorSensor.displayColorSensorDistance(telemetry);
            intakeColorSensor.displayColors(telemetry);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
