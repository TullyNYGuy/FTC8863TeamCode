package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PidUdpReceiver;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test PID Turn", group = "Test")
//@Disabled
public class TestPidTurn extends LinearOpMode {

    // Put your variable declarations here
    DriveTrain driveTrain;
    PidUdpReceiver pidUdpReceiver;
    double correction;
    private double p, i, d;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        pidUdpReceiver = new PidUdpReceiver();

        //
        telemetry.setMsTransmissionInterval(50);
        telemetry.addLine("receiver created");
        telemetry.addLine("receiver listening");
        telemetry.addLine("transmission interval set");
        //telemetry.update();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        updateCoefficients();

        telemetry.addData("Kp", formatVal(p));
        telemetry.addData("Ki", formatVal(i));
        telemetry.addData("Kd", formatVal(d));
        telemetry.update();

        idle();


        anyTurn(-90, 0.5);
        telemetry.addData("Finished Turn", "2");
        telemetry.update();
        sleep(1000);

//        driveTrain.setupTurn(90, 0.6); // angle, power
//
//        // Put your calls here - they will not run in a loop
//
//        while(opModeIsActive()) {
//
//            driveTrain.updateTurn();
//
//            // Put your calls that need to run in a loop here
////            if(driveTrain.updateTurn()) {
////                driveTrain.shutdown();
////                break;
////            }
//            // Display the current value
//            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
//            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
//            telemetry.addData(">", "Press Stop to end test." );
//            telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
//            telemetry.update();
//
//            idle();
//        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
        sleep(3000);
    }

    private void updateCoefficients()
    {
        p = pidUdpReceiver.getP();
        i = pidUdpReceiver.getI();
        d = pidUdpReceiver.getD();
    }

    /*
     * This method formats a raw double for nice display on the DS telemetry
     */
    private String formatVal(double val)
    {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(val);
    }

    public void anyTurn(double angle, double power) {
        driveTrain.setupTurn(angle,power, AdafruitIMU8863.AngleMode.RELATIVE);
        driveTrain.pidControl.setThreshold(2.0);

        driveTrain.pidControl.setKp(p);
        driveTrain.pidControl.setKi(i);
        driveTrain.pidControl.setKd(d);

        while(opModeIsActive() && !driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }

        driveTrain.stopTurn();

    }
}
