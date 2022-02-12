package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousLowLoadDuckSpinParkDepotBlueNearWall;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousLowLoadDuckSpinParkDepotBlueNearWarehouse;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousLowLoadDuckSpinParkDepotRedNearWall;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousLowLoadDuckSpinParkDepotRedNearWarehouse;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousStateMachineFreightFrenzy;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.List;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Auto - make sure you ran appropriate position set before", group = "AA")
//@Disabled
public class AutonomousFreightFrenzy extends LinearOpMode {

    // Put your variable declarations her
    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyGamepad gamepad;
    public Configuration config;
    public FreightFrenzyStartSpot startSpot;
    private ElapsedTime timer;
    public boolean autoDone;
    DataLogging dataLog = null;
    int cameraMonitorViewId;
    public FreightFrenzyField field;
    public double distance = 0;
    public double angleBetween = 0;
    private AutonomousStateMachineFreightFrenzy autonomous;

    @Override
    public void runOpMode() {
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        dataLog = new DataLogging("Autonomous", telemetry);
        config = null;
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        timer = new ElapsedTime();
        field = new FreightFrenzyField();
        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        startSpot = PersistantStorage.getStartSpot();


       /* switch(startSpot){
            case BLUE_WALL:
                cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                robot.webcamLeft.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    robot.webcamLeft.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });

                break;
            case RED_WALL:
                cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                robot.webcamRight.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    robot.webcamLeft.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });
                break;
            case BLUE_WAREHOUSE:
                cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                robot.webcamLeft.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    robot.webcamLeft.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });
                break;
            case RED_WAREHOUSE:
                cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                robot.webcamRight.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    robot.webcamLeft.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });
                break;
        }*/





        // Put your initializations here
        // create the robot and run the init for it
        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);
       switch (startSpot) {
           case RED_WALL: autonomous = new AutonomousLowLoadDuckSpinParkDepotRedNearWall(robot, field, telemetry);
           break;
           case BLUE_WALL:autonomous = new AutonomousLowLoadDuckSpinParkDepotBlueNearWall(robot, field, telemetry);
           break;
           case RED_WAREHOUSE:autonomous = new AutonomousLowLoadDuckSpinParkDepotRedNearWarehouse(robot, field, telemetry);
           break;
           case BLUE_WAREHOUSE:autonomous = new AutonomousLowLoadDuckSpinParkDepotBlueNearWarehouse(robot, field, telemetry);
           break;
       }

        timer.reset();
        robot.loopTimer.startLoopTimer();


        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        // Wait for the start button
        waitForStart();

        // Put your calls here - they will not run in a loop


       autonomous.start();
        while (opModeIsActive() && !autonomous.isComplete()) {
            autonomous.update();
            telemetry.update();
            robot.update();
            // TANYA - need the idle so we don't hog all the CPU
            idle();
            //autonomous.update();
        }

        // save the pose so we can use it to start out in teleop


        // save the shooter angle so we can use it later in teleop
        // the angle changer knows how to do this. My opinion is that you should not be down in these
        // details
        //PersistantStorage.setMotorTicks(robot.shooter.getMotorTicks());


        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    public void enableBulkReads(HardwareMap hardwareMap, LynxModule.BulkCachingMode mode) {
        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(mode);
        }
    }
}
