package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;

/**
 * This opmode test if you can talk to the core device interface module. If you see the blue and
 * red leds blinking then you are talking to it.
 *
 * Phone configuration:
 * core device interface module name: coreDIM
 *
 */
@TeleOp(name = "Test Core DIM Connection", group = "Test")
@Disabled
public class TestCoreDIMConnection extends LinearOpMode {
    /**
     * The core DIM has a blue and a red led built into it. You can use these to give a visual
     * indication of what color the color sensor is reading. Turn the red led on if the color
     * sensor is reading red, or turn the blue led on if the core DIM is reading blue. Just one
     * idea of what to do with them.
     */
    private enum CoreDIMLEDChannel {
        BLUE(0x00),
        RED(0x01);

        public final byte byteVal;

        CoreDIMLEDChannel(int i) {
            this.byteVal = (byte) i;
        }
    }

    // Put your variable declarations here
    DeviceInterfaceModule coreDIM;

    @Override
    public void runOpMode() {


        // Put your initializations here
        coreDIM = hardwareMap.deviceInterfaceModule.get("coreDIM");

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here


            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();

            // blink the blue then the red leds in the core dim. If they actually blink then you
            // know you are talking to the core dime
            turnCoreDIMBlueLEDOff();
            turnCoreDIMRedLEDOff();
            turnCoreDIMBlueLEDOn();
            sleep(1000);
            turnCoreDIMBlueLEDOff();
            turnCoreDIMRedLEDOn();
            sleep(1000);
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
    /**
     * Turn the blue led in the core DIM on
     */
    public void turnCoreDIMBlueLEDOn() {
        if (coreDIM != null) {
            coreDIM.setLED(CoreDIMLEDChannel.BLUE.byteVal, true);
        }
    }

    /**
     * Turn the blue led in the core DIM off
     */
    public void turnCoreDIMBlueLEDOff() {
        if (coreDIM != null) {
            coreDIM.setLED(CoreDIMLEDChannel.BLUE.byteVal, false);
        }
    }

    /**
     * Turn the red led in the core DIM on
     */
    public void turnCoreDIMRedLEDOn() {
        if (coreDIM != null) {
            coreDIM.setLED(CoreDIMLEDChannel.RED.byteVal, true);
        }
    }

    /**
     * Turn the red led in the core DIM off
     */
    public void turnCoreDIMRedLEDOff() {
        if (coreDIM != null) {
            coreDIM.setLED(CoreDIMLEDChannel.RED.byteVal, false);
        }
    }
}
