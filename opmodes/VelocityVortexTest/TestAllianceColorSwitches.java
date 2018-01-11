package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Alliance Color Switches", group = "Test")
@Disabled
public class TestAllianceColorSwitches extends LinearOpMode {

    // Put your variable declarations here
    private Switch redSwitch;
    private Switch blueSwitch;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        redSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getRedAllianceSwitchName(), Switch.SwitchType.NORMALLY_CLOSED);
        blueSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getBlueAllianceSwitchName(), Switch.SwitchType.NORMALLY_CLOSED);

        timer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            telemetry.addData("red switch Pressed = ", redSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE));
            telemetry.addData("red switch Released = ", redSwitch.isReleased(Switch.Debounce.NO_DEBOUNCE));
            telemetry.addData("blue switch Pressed = ", blueSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE));
            telemetry.addData("blue switch Released = ", blueSwitch.isReleased(Switch.Debounce.NO_DEBOUNCE));
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(2000);

    }
}
