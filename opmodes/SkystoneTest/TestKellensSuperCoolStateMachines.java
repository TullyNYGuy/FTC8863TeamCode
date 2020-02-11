package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.DualLift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Gripper;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.GripperRotator;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakePusherServos;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakeWheels;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test them there state doo hickeys", group = "Test")
//@Disabled
public class TestKellensSuperCoolStateMachines extends LinearOpMode {
    private Configuration config;
    private DataLogging datalog;
    private SkystoneRobot robot;

    public ElapsedTime timer;

    enum Actions {
        START,
        INTAKE,
        GRIP,
        DEPORT,
        LIFT,
        PLACE,
        PREPARE,
        COMPLETE,
        IDLE
    }

    Actions action = Actions.IDLE;

    @Override
    public void runOpMode() throws InterruptedException {
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        datalog = new DataLogging("State Machine Test", telemetry);

        SkystoneRobot robot = new SkystoneRobot(hardwareMap, telemetry, config, datalog, DistanceUnit.INCH);
        robot.createRobot();
        // start the inits for the robot subsytems
        robot.init();

        while (!robot.isInitComplete()) {
            timer = new ElapsedTime();
            robot.update();
            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway

                datalog.logData("Init failed to complete on time. Proceeding anyway!");
                //How cheerful. How comforting...
                break;
            }
            idle();
        }

        timer = new ElapsedTime();

        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        action = Actions.START;
        timer.reset();
        while (opModeIsActive()) {
            robot.update();

            switch (action) {
                case IDLE:
                    break;
                case START:
                    robot.intakeBlock();
                    action = Actions.INTAKE;
                    break;
                case INTAKE:
                    if (robot.isIntakeBlockComplete()) {
                        robot.gripBlock();
                        action = Actions.GRIP;
                    }
                    break;
                case GRIP:
                    if (robot.isGripBlockComplete()) {
                        robot.deportBlock();
                        action = Actions.DEPORT;
                    }
                    break;
                case DEPORT:
                    if (robot.isDeportBlockComplete()) {
                        robot.liftBlock();
                        action = Actions.LIFT;
                    }
                    break;
                case LIFT:
                    if (robot.isLiftBlockComplete()) {
                        robot.placeBlock();
                        action = Actions.PLACE;
                    }
                    break;
                case PLACE:
                    if (robot.isPlaceBlockComplete()) {
                        robot.prepareToIntakeBlock();
                        action = Actions.PREPARE;
                    }
                    break;
                case PREPARE:
                    if (robot.isPrepareIntakeComplete()) {
                        action = Actions.COMPLETE;
                    }
                    break;
                case COMPLETE:
                    break;
            }
           /* if (timer.milliseconds() > 5000) {
                robot.intakeOff();
                robot.gripBlock();
            }
           if (robot.isGripBlockComplete()) {
                robot.deportBlock();
            }
            if (robot.isDeportBlockComplete()) {
                robot.liftBlockStateUpdate();
            }
            if (robot.isLiftBlockComplete()) {
                robot.placeBlock();
            }
            if (robot.isPlaceBlockComplete()) {
                robot.prepareToIntakeBlock();
            }
            if (robot.isPrepareIntakeComplete()) {
                stop();
            }*/
            telemetry.addData("CurrentStateIntake", robot.getCurrentIntakeState());
            telemetry.addData("CurrentStateGrip", robot.getCurrentGripperState());
            telemetry.addData("CurrentStateDeport", robot.getCurrentDeportState());
            telemetry.addData("CurrentStateLift", robot.getCurrentLiftState());
            telemetry.addData("CurrentStatePlaceBlock", robot.getCurrentPlaceBlockState());
            telemetry.addData("CurrentStatePrepareIntake", robot.getCurrentPrepareIntakeState());


            idle();
        }

    }
}
