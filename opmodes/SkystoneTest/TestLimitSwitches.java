package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Skystone Limit Switches", group = "Test")
//@Disabled
public class TestLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    public Lift liftLeft;
    public Lift liftRight;

    public double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    public double movementPerRevolution = spoolDiameter * Math.PI * 5;

    @Override
    public void runOpMode() {


        // Put your initializations here
        liftLeft = new Lift(hardwareMap, telemetry, "liftLeft",
                SkystoneRobot.HardwareName.LIFT_LEFT_EXTENSION_SWITCH.hwName, SkystoneRobot.HardwareName.LIFT_LEFT_RETRACTION_SWITCH.hwName, SkystoneRobot.HardwareName.LIFT_LEFT_MOTOR.hwName,
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        //liftLeft.reverseMotor();

        liftRight = new Lift(hardwareMap, telemetry, "liftRight",
                SkystoneRobot.HardwareName.LIFT_RIGHT_EXTENSION_SWITCH.hwName, SkystoneRobot.HardwareName.LIFT_RIGHT_RETRACTION_SWITCH.hwName, SkystoneRobot.HardwareName.LIFT_RIGHT_MOTOR.hwName,
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        liftRight.reverseMotor();

        //extensionRetractionMechanismArm = new ExtensionRetractionMechanism(hardwareMap,telemetry,"extensionArm",
        //        "extensionLimitSwitchArm", "retractionLimitSwitchArm", "extensionArmMotor",
        //        DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            telemetry.addData("left", ":");
            liftLeft.testLimitSwitches();
            telemetry.addData("right", ":");
            liftRight.testLimitSwitches();
            telemetry.addData("arm", ":");
            //extensionRetractionMechanismArm.testLimitSwitches();
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
