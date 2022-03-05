package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLED;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDBlinker;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FFBlinkinLed;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FFIntake;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Kellen's Absolutely MAGNIFICENT Intake Test", group = "Test")
//@Disabled
public class IntakeClassTest extends LinearOpMode {

    // Put your variable declarations her
    public FFIntake ffIntake;
    public RevLEDBlinker ledBlinker;
    public FFBlinkinLed ledStrip;

    @Override
    public void runOpMode() {
        ledBlinker = new RevLEDBlinker(2, RevLED.Color.GREEN, hardwareMap,
                FreightFrenzyRobotRoadRunner.HardwareName.LED_PORT1.hwName,
                FreightFrenzyRobotRoadRunner.HardwareName.LED_PORT2.hwName);
        ffIntake = new FFIntake(hardwareMap, telemetry, ledBlinker, ledStrip);
        // Put your initializations here

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            ffIntake.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
