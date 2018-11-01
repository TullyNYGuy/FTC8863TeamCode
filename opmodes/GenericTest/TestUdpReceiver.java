package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.PidUdpReceiver;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test UPD Receiver", group = "Test")
//@Disabled
public class TestUdpReceiver extends LinearOpMode {

    // Put your variable declarations here
    private double p, i, d;
    private PidUdpReceiver pidUdpReceiver;

    @Override
    public void runOpMode() {


        // Put your initializations here
        pidUdpReceiver = new PidUdpReceiver();
        telemetry.addLine("receiver created");
        telemetry.update();

        pidUdpReceiver.beginListening();
        telemetry.addLine("receiver created");
        telemetry.addLine("receiver listening");
        telemetry.update();

        telemetry.setMsTransmissionInterval(50);
        telemetry.addLine("receiver created");
        telemetry.addLine("receiver listening");
        telemetry.addLine("transmission interval set");
        telemetry.addLine("wait for start ...");
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();

        // Wait for the start button
        //telemetry.addData(">", "Press Start to run" );
        //telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            updateCoefficients();

            telemetry.addData("P", formatVal(p));
            telemetry.addData("I", formatVal(i));
            telemetry.addData("D", formatVal(d));
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        /*
         * Make sure to call this at the end of your OpMode or else
         * the receiver won't work again until the app is restarted
         */
        pidUdpReceiver.shutdown();

        telemetry.addData(">", "Done");
        telemetry.update();

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
}
