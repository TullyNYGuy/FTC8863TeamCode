package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = " Glenn's incredible Intake test", group = "Test")
//@Disabled
public class IntakeTestGlenn extends LinearOpMode {

    // Put your variable declarations her
    private NormalizedColorSensor intakeSensor;
    private DcMotor8863 intakeSweeperMotor;
    private ElapsedTime timer;

    private enum IntakeState {
        IDLE,
        INTAKE,
        WAIT_FOR_FREIGHT,
        HOLD_FREIGHT,
        WAIT_FOR_ROTATION,
        OUTAKE;
    }

    private IntakeState intakeState = IntakeState.IDLE;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperMotor = new DcMotor8863("intakeSweeperMotor", hardwareMap);
        intakeSweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        intakeSweeperMotor.setMovementPerRev(360);

        timer = new ElapsedTime();

        intakeSensor = hardwareMap.get(NormalizedColorSensor.class, "intakeSensor");
        if (intakeSensor instanceof SwitchableLight) {
            ((SwitchableLight) intakeSensor).enableLight(true);
        }

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        intakeState = IntakeState.INTAKE;
        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            update();

            displaySwitches(telemetry);
            telemetry.addData("Intake state = ", intakeState.toString());
            ;
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
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

    // The state machine Kellen could have written. IMO, longer but easier to write and debug
    // because the logic is simple. Or maybe I am just not so smart and I like simple logic.
    // IMO, there are times when more effort up front saves time in the long run.
    public void update() {
        switch (intakeState) {
            case IDLE: {
                // do nothing
            }
            break;
            case INTAKE: {
                // fire up that motor baby! Dang that thing is loud!
                intakeSweeperMotor.runAtConstantPower(.6);
                intakeState = IntakeState.WAIT_FOR_FREIGHT;
            }
            break;

            case WAIT_FOR_FREIGHT: {
                // do we have something?
                if (isIntakeFull()) {
                    // yup stop the motor and try to cage the freight
                    intakeSweeperMotor.setPower(.2);
                    timer.reset();
                    //intakeSweeperMotor.moveToPosition(.3, 300, DcMotor8863.FinishBehavior.HOLD);
                    intakeState = IntakeState.WAIT_FOR_ROTATION;
                }
            }
            break;

            case HOLD_FREIGHT: {
                // is the caging done?
                if (intakeSweeperMotor.isMovementComplete()) {
                    // yup, now the human has to rotate the intake because that intake guy has not completed the rotation hardware yet :-)
                    timer.reset();
                    intakeState = IntakeState.WAIT_FOR_ROTATION;
                }
            }
            break;

            case WAIT_FOR_ROTATION: {
                // has the human done his thing?
                if (timer.milliseconds() > 3500) {
                    // hope so cause I'm about to eject the freight
                    intakeSweeperMotor.setPower(-.1);
                    timer.reset();
                    intakeState = IntakeState.OUTAKE;
                }
            }
            break;

            case OUTAKE: {
                // hopefully the freight ejects in this amount of time
                if (timer.milliseconds() > 3500) {
                    // done ejecting, time to go back to sleep
                    intakeSweeperMotor.setPower(0);
                    intakeState = IntakeState.IDLE;
                }
            }
            break;

        }
    }
}
