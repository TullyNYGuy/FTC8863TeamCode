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
@TeleOp(name = "Test Front Beacon Pusher Switches", group = "Test")
@Disabled
public class TestBeaconLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    Switch leftFrontLimitSwitch;
    Switch rightFrontLimitSwitch;
    Switch leftBackLimitSwitch;
    Switch rightBackLimitSwitch;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        leftFrontLimitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getLeftFrontLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);
        rightFrontLimitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getRightFrontLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);
        leftBackLimitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getLeftBackLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);
        rightBackLimitSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getRightBackLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN);

        timer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();

        // for the first 10 seconds test the pressed and released methods.
        // Since these clear the bumped status, I can't test that here
        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            leftFrontLimitSwitch.updateSwitch();
            rightFrontLimitSwitch.updateSwitch();
            leftBackLimitSwitch.updateSwitch();
            rightBackLimitSwitch.updateSwitch();

            telemetry.addData("leftFrontLimitSwitch Pressed = ", leftFrontLimitSwitch.isPressed());
            telemetry.addData("leftFrontLimitSwitch Released = ", leftFrontLimitSwitch.isReleased());
            telemetry.addData("rightFrontLimitSwitch Pressed = ", rightFrontLimitSwitch.isPressed());
            telemetry.addData("rightFrontLimitSwitch Released = ", rightFrontLimitSwitch.isReleased());
            telemetry.addData("leftBackLimitSwitch Pressed = ", leftBackLimitSwitch.isPressed());
            telemetry.addData("leftBackLimitSwitch Released = ", leftBackLimitSwitch.isReleased());
            telemetry.addData("rightBackLimitSwitch Pressed = ", rightBackLimitSwitch.isPressed());
            telemetry.addData("rightBackLimitSwitch Released = ", rightBackLimitSwitch.isReleased());
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(2000);

    }
}
