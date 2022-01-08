package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;


/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Kellen's Exceptional Sensor Test ", group = "Test")

public class DistanceSensorTest extends LinearOpMode {

    // Put your variable declarations her
    private NormalizedColorSensor stage1Sensor;

    @Override
    public void runOpMode() {


        // Put your initializations here

        stage1Sensor = hardwareMap.get(NormalizedColorSensor.class, "sensor");
        if (stage1Sensor instanceof SwitchableLight) {
            ((SwitchableLight) stage1Sensor).enableLight(true);
        }
            // Wait for the start button
            telemetry.addData(">", "Press Start to run");
            telemetry.update();
            waitForStart();

            // Put your calls here - they will not run in a loop

            while (opModeIsActive()) {
                displaySWitches(telemetry);
                // Put your calls that need to run in a loop here
                // if (stage1Sensor instanceof DistanceSensor) {
                //   if (((DistanceSensor) stage1Sensor).getDistance(DistanceUnit.CM) < 8) {
                //     result = true;


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

    public void displaySWitches(Telemetry telemetry) {
        telemetry.addData("distance=", ((DistanceSensor) stage1Sensor).getDistance(DistanceUnit.CM));
    }
}
