package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitBackpackLED;

/**
 * This opmode is a demo state machine that uses a heating and cooling system as a demo
 */
@TeleOp(name = "Demo State Machine", group = "Test")
//@Disabled
public class DemoACHeatStateMachine extends LinearOpMode {

    public enum ACHeatState {
        OFF,
        ON,
        HEAT,
        COOL
    }
    // Put your variable declarations here
    private ACHeatState acHeatState = ACHeatState.OFF;
    private ACHeatState currentState;

    // temperature in degrees C
    private double temperature = 22;
    private double loTempLimit = 20;
    private double hiTempLimit = 25;

    @Override
    public void runOpMode() {

        // Put your initializations here

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            currentState = update();
            telemetry.update();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    public ACHeatState update(){
        switch (acHeatState) {
            case OFF:
                // test for the condition to advance to the next state
                if (gamepad1.a) {
                    // condition to advance is met so set the next state
                    acHeatState = ACHeatState.ON;
                    // turn system on
                } else {
                    // condition to advance is not met so do whatever gets done in this state
                    telemetry.addData("OFF", "!");
                }
            case ON:
                // test for the condition to advance to the next state
                if (temperature < loTempLimit) {
                    // condition to advance is met so set the next state
                    acHeatState = ACHeatState.HEAT;
                } else {
                    if (temperature > hiTempLimit) {
                        acHeatState = ACHeatState.COOL;
                        // turn AC on
                    } else {
                        if (gamepad1.b) {
                            acHeatState = ACHeatState.OFF;
                            // turn system off
                        } else {
                            // condition to advance is not met so do whatever gets done in this state
                            telemetry.addData("COMFY", "!");
                        }
                    }
                }
            case HEAT:
                // test for the condition to advance to the next state
                if (temperature > hiTempLimit) {
                    // condition to advance is met so set the next state
                    acHeatState = ACHeatState.ON;
                    // turn furnace on
                } else {
                    if (gamepad1.b) {
                        acHeatState = ACHeatState.OFF;
                        // turn system off
                    } else {
                        // condition to advance is not met so do whatever gets done in this state
                        telemetry.addData("Heating", "!");
                    }
                }
            case COOL:
                // test for the condition to advance to the next state
                if (temperature < loTempLimit) {
                    // condition to advance is met so set the next state
                    acHeatState = ACHeatState.ON;
                    // turn AC off and wait for a change in system
                } else {
                    if (gamepad1.b) {
                        acHeatState = ACHeatState.OFF;
                        // turn system off
                    } else {
                        // condition to advance is not met so do whatever gets done in this state
                        telemetry.addData("Cooling", "!");
                    }
                }
        }
        return acHeatState;
    }
}
