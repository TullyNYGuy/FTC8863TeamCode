package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.FrontBeaconPusherControlGB;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Front Beacon Pusher Control", group = "Test")
@Disabled
public class TestFrontBeaconPusherControlGB extends LinearOpMode {

    // Put your variable declarations here

    private FrontBeaconPusherControlGB frontBeaconPusherControl;

    private MuxPlusColorSensors muxPlusColorSensors;

    private DriveTrain driveTrain;

    private FrontBeaconPusherControlGB.AllianceColor allianceColor = FrontBeaconPusherControlGB.AllianceColor.BLUE;

    private FrontBeaconPusherControlGB.FrontBeaconControlState frontBeaconControlState;

    @Override
    public void runOpMode() {


        // Put your initializations here

        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        frontBeaconPusherControl =  new FrontBeaconPusherControlGB(hardwareMap, telemetry,
                muxPlusColorSensors, driveTrain, allianceColor);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        frontBeaconPusherControl.initialize();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            frontBeaconControlState = frontBeaconPusherControl.update();

            // Display the current value
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.addData("State = ", frontBeaconControlState.toString());

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
