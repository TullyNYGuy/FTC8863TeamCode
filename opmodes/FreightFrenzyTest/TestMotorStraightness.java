package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Motor drift Test", group = "Test")
@Disabled
public class TestMotorStraightness extends LinearOpMode {

    // Put your variable declarations her

  // FreightFrenzyRobot robot = new FreightFrenzyRobot(hardwareMap, telemetry, config, datalog, i)
    private ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here

        timer = new ElapsedTime();
        boolean intakeFull = false;


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive() ) {

            // Put your calls that need to run in a loop here


            // PRO TIP: RUN THE MENU COMMAND CODE->REFORMAT CODE. THIS BUG WAS HARD TO SEE BECAUSE YOUR CODE WAS NOT FORMATTED CORRECTLY. ONCE I
            // REFORMATTED YOUR CODE IT WAS IMMEDIATELY OBVIOUS. THIS IS HOW IT LOOKED BEFORE THE REFORMAT:

                // HAHA - AND YOU THOUGHT THIS WAS INSIDE THE IF STATEMENT! NOW GO LOOK AT HOW YOU COULD HAVE WRITTEN IT WITH A SIMPLE STATE MACHINE
                // SEE IntakeTestGlenn for a truly incredible opmode :-O

            displaySwitches(telemetry);
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        //  while(opModeIsActive() && timer.milliseconds()<3500){
        //idle();
        //}
        // while(opModeIsActive() && timer.milliseconds()<5100){
        // intakeSweeperMotor.setPower(-0.3);

        // }
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }



    public void displaySwitches(Telemetry telemetry) {
        //telemetry.addData("distance=", ((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM));
    }
}
