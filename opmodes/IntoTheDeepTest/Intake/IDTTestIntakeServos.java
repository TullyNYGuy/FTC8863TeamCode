package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Intake;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeSweeperVertical;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Intake Servos", group = "Test")
//@Disabled
public class IDTTestIntakeServos extends LinearOpMode {

    // Put your variable declarations here
    public ITDIntakeSweeperVertical intakeSweeperVertical;

    public ElapsedTime timer;
    public boolean outtaking = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperVertical = new ITDIntakeSweeperVertical(hardwareMap, telemetry, 1);
        timer = new ElapsedTime();
        intakeSweeperVertical.setAllianceColor(Color.RED);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        // turn the color sensor LED on
        intakeSweeperVertical.colorSensorOn();

        while (opModeIsActive()) {
            intakeSweeperVertical.update();

            if (gamepad1.x) {
                intakeSweeperVertical.runIntakeServos();
                outtaking = false;
            }

//            if (intakeSweeperVertical.getDistanceToSample(DistanceUnit.CM) < 3 && outtaking == false) {
//                intakeSweeperVertical.stop();
//            }

//            if (intakeColorSensor.getDistance(DistanceUnit.CM) < 2.5 &&
//                    (intakeColorSensor.getColor() == Color.BLUE || intakeColorSensor.getColor() == RED || intakeColorSensor.getColor() == Color.YELLOW)) {
//                intakeSweeperVertical.stop();
//            }

            intakeSweeperVertical.displayState(telemetry);
            intakeSweeperVertical.displayDistanceToSample(telemetry);
            intakeSweeperVertical.displayColorData(telemetry);
            intakeSweeperVertical.displaySampleColor(telemetry);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
