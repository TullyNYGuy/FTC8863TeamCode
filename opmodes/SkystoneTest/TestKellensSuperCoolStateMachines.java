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
    public SkystoneRobot robot = new SkystoneRobot(hardwareMap, telemetry, config, datalog, DistanceUnit.INCH);
    public ElapsedTime timer;

    public void megaMasterUpdate() {
        robot.intakeBlockUpdate();
        robot.gripStateUpdate();
        robot.deportStateUpdate();
        robot.liftBlockStateUpdate();
        robot.placeBlockStateUpdate();
        robot.prepareIntakeUpdate();
    }

    public SkystoneRobot.IntakeStates intakeStates;
    public SkystoneRobot.GripStates gripStates;
    public SkystoneRobot.DeportStates deportStates;
    public SkystoneRobot.LiftBlockStates liftBlockStates;
    public SkystoneRobot.PlaceBlockStates placeBlockStates;
    public SkystoneRobot.PrepareIntakeStates prepareIntakeStates;


    @Override
    public void runOpMode() throws InterruptedException {

        robot.intakeBlock();
        timer.reset();
        while (opModeIsActive()) {
            if (timer.milliseconds() > 5000) {
                robot.intakeOff();
                robot.gripBlock();
            }
            if (robot.isGripComplete()) {
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
            }
            telemetry.addData("CurrentState", robot.getCurrentIntakeState());
            telemetry.addData("CurrentState", robot.getCurrentGripperState());
            telemetry.addData("CurrentState", robot.getCurrentDeportState());
            telemetry.addData("CurrentState", robot.getCurrentLiftState());
            telemetry.addData("CurrentState", robot.getCurrentPlaceBlockState());
            telemetry.addData("CurrentState", robot.getCurrentPrepareIntakeState());

            megaMasterUpdate();

        }

    }
}
