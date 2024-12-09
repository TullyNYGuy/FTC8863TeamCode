package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeColorSensor;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeSweeperServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Intake", group = "Test")
//@Disabled
public class ITDTestIntake extends LinearOpMode {

    // Put your variable declarations here

    public ITDIntakeSweeperServo intakeSweeperServo;
    public ITDIntakeColorSensor intakeColorSensorLeft;
    public ITDIntakeColorSensor intakeColorSensorRight;
    public ElapsedTime timer;

    public boolean delayStarted = false;
    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperServo = new ITDIntakeSweeperServo(hardwareMap, telemetry);
        intakeColorSensorLeft = new ITDIntakeColorSensor(hardwareMap, telemetry, "intakeColorSensorV3Left");
        intakeColorSensorRight = new ITDIntakeColorSensor(hardwareMap, telemetry, "intakeColorSensorV3Right");
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            intakeSweeperServo.update();
            if (gamepad1.x) {
                intakeSweeperServo.intake();
                delayStarted = false;
            }
            if (gamepad1.a) {
                intakeSweeperServo.stop();
            }
            if (gamepad1.b) {
                intakeSweeperServo.outtake();
            }
            if (gamepad1.y) {
                // does nothing
            }

            if (intakeColorSensorLeft.getDistance() < 1.5 && delayStarted == false) {
                intakeSweeperServo.intakeThenStop(1000);
                delayStarted = true;
            }

            intakeColorSensorLeft.displayColorSensorDistance(telemetry);
            intakeColorSensorLeft.displayColors(telemetry);
            intakeColorSensorRight.displayColorSensorDistance(telemetry);
            intakeColorSensorRight.displayColors(telemetry);

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
