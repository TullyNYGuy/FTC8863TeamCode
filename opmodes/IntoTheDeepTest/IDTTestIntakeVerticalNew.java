package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest;

import static org.firstinspires.ftc.teamcode.Lib.Color.RED;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorSensorUpdatable;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeColorSensor;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeSweeperVertical;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Intake Vertical New", group = "Test")
//@Disabled
public class IDTTestIntakeVerticalNew extends LinearOpMode {

    // Put your variable declarations here
    public ITDIntakeSweeperVertical intakeSweeperVertical;
    public ColorSensorUpdatable intakeColorSensor;
    public ElapsedTime timer;
    public boolean outtaking = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperVertical = new ITDIntakeSweeperVertical(hardwareMap, telemetry);
        intakeColorSensor = new ColorSensorUpdatable(hardwareMap, telemetry, "intakeColorSensorV3Left");
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        intakeColorSensor.turnSensorOn();

        while (opModeIsActive()) {
            intakeSweeperVertical.update();
            intakeColorSensor.update();

            if (gamepad1.x) {
                intakeSweeperVertical.intake();
                outtaking = false;
            }
            if (gamepad1.a) {
                intakeSweeperVertical.stop();
            }
            if (gamepad1.b) {
                intakeSweeperVertical.outtake();
                outtaking = true;
            }
            if (gamepad1.y) {
                // does nothing
            }

            if (intakeColorSensor.getDistance(DistanceUnit.CM) < 3 && outtaking == false) {
                intakeSweeperVertical.stop();
            }

//            if (intakeColorSensor.getDistance(DistanceUnit.CM) < 2.5 &&
//                    (intakeColorSensor.getColor() == Color.BLUE || intakeColorSensor.getColor() == RED || intakeColorSensor.getColor() == Color.YELLOW)) {
//                intakeSweeperVertical.stop();
//            }

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
