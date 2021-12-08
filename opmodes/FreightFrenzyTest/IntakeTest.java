package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = " Kellen's incredible Intake test", group = "Test")
//@Disabled
public class IntakeTest extends LinearOpMode {

    // Put your variable declarations her
    private NormalizedColorSensor intakeSensor;
    private DcMotor8863 intakeSweeperMotor;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperMotor = new DcMotor8863("intakeSweeperMotor",hardwareMap);
        intakeSweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);

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

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            if (isIntakeFull()){
                intakeSweeperMotor.setPower(0);
            }
            else {
                intakeSweeperMotor.setPower(1);
            }
            displaySwitches(telemetry);
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
    public boolean isIntakeFull(){
         if(((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM) < 5){
             return true;
         }
         else{
             return false;
         }
    }
    public void displaySwitches(Telemetry telemetry) {
        telemetry.addData("distance=", ((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM));
    }
}
