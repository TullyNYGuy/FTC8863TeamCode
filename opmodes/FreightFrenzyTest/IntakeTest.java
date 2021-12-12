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

import java.util.Timer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = " Kellen's incredible Intake test", group = "Test")
//@Disabled
public class IntakeTest extends LinearOpMode {

    // Put your variable declarations her
    private NormalizedColorSensor intakeSensor;
    private DcMotor8863 intakeSweeperMotor;
    private ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperMotor = new DcMotor8863("intakeSweeperMotor", hardwareMap);
        intakeSweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        intakeSweeperMotor.setMovementPerRev(360);
        timer = new ElapsedTime();
        boolean intakeFull = false;

        intakeSensor = hardwareMap.get(NormalizedColorSensor.class, "intakeSensor");
        if (intakeSensor instanceof SwitchableLight) {
            ((SwitchableLight) intakeSensor).enableLight(true);
        }
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        intakeSweeperMotor.runAtConstantPower(1);
        // Put your calls here - they will not run in a loop

        while (opModeIsActive() && !intakeFull) {

            // Put your calls that need to run in a loop here

            if (isIntakeFull()) {
                timer.reset();
                intakeSweeperMotor.moveToPosition(0.3, 300, DcMotor8863.FinishBehavior.HOLD);
                if (intakeSweeperMotor.isMovementComplete())
                    intakeSweeperMotor.setPower(0);
                //timer.reset();
                //OOPS! HERE IS A BUG! YOU FORGOT THE SQUIRRELY BRACKETS AND SO YOU SET INTAKE FULL NO MATTER WHAT AND THAT TERMINATES
                // THE LOOP SO YOU NEVER GET TO WATCH
                // TO SEE IF THE MOVEMENT IS COMPLETE. YOU NEVER SET THE POWER TO 0. AND THEN YOUR "INCREDIBLE" OPMODE JUST FINISHES.
                // THE EFFECT IS THAT THE MOTOR NEVER GETS TO THE LOCATION YOU WANT BEFORE THE PROGRAM TERMINATES. BUMMER!
                intakeFull = true;
            }
            // PRO TIP: RUN THE MENU COMMAND CODE->REFORMAT CODE. THIS BUG WAS HARD TO SEE BECAUSE YOUR CODE WAS NOT FORMATTED CORRECTLY. ONCE I
            // REFORMATTED YOUR CODE IT WAS IMMEDIATELY OBVIOUS. THIS IS HOW IT LOOKED BEFORE THE REFORMAT:
            if (intakeSweeperMotor.isMovementComplete())
                intakeSweeperMotor.setPower(0);
                // HAHA - AND YOU THOUGHT THIS WAS INSIDE THE IF STATEMENT! NOW GO LOOK AT HOW YOU COULD HAVE WRITTEN IT WITH A SIMPLE STATE MACHINE
                // SEE IntakeTestGlenn for a truly incredible opmode :-O
                intakeFull = true;
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

    public boolean isIntakeFull() {
        if (((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM) < 3) {
            return true;
        } else {
            return false;
        }
    }

    public void displaySwitches(Telemetry telemetry) {
        telemetry.addData("distance=", ((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM));
    }
}
