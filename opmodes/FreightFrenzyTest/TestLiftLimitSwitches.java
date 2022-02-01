package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

<<<<<<< HEAD:opmodes/FreightFrenzyTest/TestLiftLimitSwitches.java
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
=======
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FFIntake;
>>>>>>> RoadRunnerIntegration:opmodes/FreightFrenzyTest/IntakeClassTest.java

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
<<<<<<< HEAD:opmodes/FreightFrenzyTest/TestLiftLimitSwitches.java
@TeleOp(name = "Test Lift Switches", group = "Test")
//@Disabled
public class TestLiftLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism lift;

=======
@TeleOp(name = "Kellen's Absolutely MAGNIFICENT Intake Test", group = "Test")
//@Disabled
public class IntakeClassTest extends LinearOpMode {

    // Put your variable declarations her
   public FFIntake ffIntake;
>>>>>>> RoadRunnerIntegration:opmodes/FreightFrenzyTest/IntakeClassTest.java
    @Override
    public void runOpMode() {

ffIntake = new FFIntake(hardwareMap, telemetry);
        // Put your initializations here
        lift = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "Lift",
                "ExtensionLimitSwitch",
                "RetractionLimitSwitch",
                "LiftMotor",
                 DcMotor8863.MotorType.GOBILDA_435,
                4.517);
        lift.reverseMotorDirection();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
<<<<<<< HEAD:opmodes/FreightFrenzyTest/TestLiftLimitSwitches.java

            lift.testLimitSwitches();
=======
            ffIntake.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
>>>>>>> RoadRunnerIntegration:opmodes/FreightFrenzyTest/IntakeClassTest.java
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
