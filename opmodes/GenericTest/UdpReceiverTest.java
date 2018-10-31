package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PidUdpReceiver ;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UdpReceiverTest {

    @TeleOp(name="PID test 2", group="utils")
    public class PIDtest2 extends LinearOpMode
    {
        private double p, i, d;
        private PidUdpReceiver pidUdpReceiver;

        @Override
        public void runOpMode() throws InterruptedException
        {
            /*
             * Initialize the network receiver
             */
            pidUdpReceiver = new PidUdpReceiver();
            pidUdpReceiver.beginListening();

            telemetry.setMsTransmissionInterval(50);
            waitForStart();

            /*
             * Main loop
             */
            while (opModeIsActive())
            {
                updateCoefficients();

                telemetry.addData("P", formatVal(p));
                telemetry.addData("I", formatVal(i));
                telemetry.addData("D", formatVal(d));
                telemetry.update();
            }

            /*
             * Make sure to call this at the end of your OpMode or else
             * the receiver won't work again until the app is restarted
             */
            pidUdpReceiver.shutdown();
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
}
