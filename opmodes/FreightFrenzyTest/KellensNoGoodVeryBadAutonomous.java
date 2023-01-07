package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousVisionLoadDuckSpinParkDepot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousStateMachineFreightFrenzy;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;

import java.util.List;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Kellen's auto", group = "AA")
@Disabled
public class KellensNoGoodVeryBadAutonomous extends LinearOpMode {

    // Put your variable declarations her
    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyGamepad gamepad;
    public Configuration config;
    //public FreightFrenzyStartSpot startSpot;
    // todo I got rid of the detrious we don't need. Keep it simple and it should be easier to debug
    //private ElapsedTime timer;
    public boolean autoDone;
    DataLogging dataLog = null;
    //int cameraMonitorViewId;
    //public FreightFrenzyField field;
    //public double distance = 0;
    //public double angleBetween = 0;
    //private AutonomousStateMachineFreightFrenzy autonomous;


    private enum State {
        ONE,
        TWO,
        THREE,
        FOUR,
    }
    private State state = State.ONE;

    @Override
    public void runOpMode() {
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        dataLog = new DataLogging("Autonomous", telemetry);
        config = null;
     //   cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        //timer = new ElapsedTime();
        //field = new FreightFrenzyField();
        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        //startSpot = PersistantStorage.getStartSpot();

        // I deleted all of hte commented out code. Hmmmm ....


       // autonomous = new AutonomousVisionLoadDuckSpinParkDepot(robot, field, telemetry);

        //timer.reset();
        //robot.loopTimer.startLoopTimer();

        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        // Wait for the start button
        waitForStart();

        // Put your calls here - they will not run in a loop
        robot.freightSystem.setMiddle();

        while (opModeIsActive() ) {
            update();
            telemetry.update();
            robot.update();
            idle();
        }


        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }


    public void update() {
        switch (state){

            case ONE: {
                robot.freightSystem.start();
                state = State.TWO;
            }
            break;

            case TWO: {
                if(robot.freightSystem.isReadyToExtend()){
                    robot.freightSystem.extend();
                    state = State.THREE;
                }
            }
            break;

            case THREE: {
                if(robot.freightSystem.isReadyToDump()){
                    robot.freightSystem.dump();
                }
            }
            break;

            case FOUR: {
                //nothing

            }
            break;
        }
    }

}
