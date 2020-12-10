package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AngleAdjustedIMU;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Angle Adjusted IMU", group = "Test")
@Disabled
public class TestAngleAdjustedIMU extends LinearOpMode {

    // Put your variable declarations here
    AngleAdjustedIMU angleAdjustedIMU;
    AdafruitIMU8863 imu;

    @Override
    public void runOpMode() {


        // Put your initializations here
        imu = new AdafruitIMU8863(hardwareMap);
        telemetry.addData("IMU Initialized", "!");
        angleAdjustedIMU = new AngleAdjustedIMU(imu);
//        angleAdjustedIMU.setTargetAngle(90);
//        angleAdjustedIMU.setTargetAngle(180);
//        angleAdjustedIMU.setTargetAngle(-90);
        angleAdjustedIMU.setTargetAngle(-180);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop


        while (opModeIsActive()) {
            // Display the current value
            telemetry.addData("heading = ", angleAdjustedIMU.getHeading());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
