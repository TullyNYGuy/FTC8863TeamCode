package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FFExtensionArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "AA kellens mega lift test ", group = "Test")
//@Disabled
public class AAKellensMegaExtensionArmTest extends LinearOpMode {
public Configuration config;
    // Put your variable declarations her
private FFExtensionArm delivery;
    @Override
    public void runOpMode() {
        delivery = new FFExtensionArm(AllianceColor.BLUE, hardwareMap, telemetry);

        // Put your initializations here
        delivery.init(config );
        while(!delivery.isInitComplete()){
            idle();
        }
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
            delivery.extendToTop();
        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            delivery.update();
            telemetry.addData("Super Cool State:", delivery.getLiftState());
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
