package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.AllianceColorSwitch;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.SideBeaconPusher;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.SideBeaconPusherControl;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Side Beacon Control", group = "Test")
//@Disabled
public class TestSideBeaconPusherControl extends LinearOpMode {

    // Put your variable declarations here
    SideBeaconPusherControl sideBeaconPusherControl;
    DriveTrain driveTrain;
    MuxPlusColorSensors muxPlusColorSensors;

    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setCmPerRotation(31.1); // cm
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        sideBeaconPusherControl = new SideBeaconPusherControl(hardwareMap, telemetry,
                muxPlusColorSensors, driveTrain,
                SideBeaconPusher.SideBeaconPusherPosition.RIGHT, AllianceColorSwitch.AllianceColor.RED);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            telemetry.addData(">", "Press Stop to end test." );
            //navigate to wall first
            sideBeaconPusherControl.startSideBeaconPusherControl();
            sideBeaconPusherControl.update();
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
