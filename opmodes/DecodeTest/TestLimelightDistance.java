package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIMU;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeLimelight;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Limelight Distance", group = "Test")
//@Disabled
public class TestLimelightDistance extends LinearOpMode {

    // Put your variable declarations here
    public DecodeLimelight limelight;
    public DecodeIMU imu;

    @Override
    public void runOpMode() {


        // Put your initializations here
        imu = new DecodeIMU(hardwareMap, telemetry);
        limelight = new DecodeLimelight(hardwareMap, telemetry, imu);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        limelight.start();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            telemetry.addData("Distance To Goal ", limelight.getDistaceToGoal(DistanceUnit.INCH));
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
