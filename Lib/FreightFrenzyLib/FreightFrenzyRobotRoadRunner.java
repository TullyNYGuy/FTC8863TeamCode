package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LoopTimer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLED;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDBlinker;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FreightFrenzyRobotRoadRunner implements FTCRobot {

    public enum HardwareName {
        IMU("imu"),
        CONFIG_FL_MOTOR("leftFrontMotor"),
        CONFIG_FR_MOTOR("rightFrontMotor"),
        CONFIG_BL_MOTOR("leftRearMotor"),
        CONFIG_BR_MOTOR("rightRearMotor"),
        CONFIG_LEFT_ODOMETRY_MODULE("LeftOdometryModule"),
        CONFIG_RIGHT_ODOMETRY_MODULE("RightOdometryModule"),
        CONFIG_BACK_ODOMETRY_MODULE("BackOdometryModule"),
        ODOMETRY_MODULE_LEFT("leftFrontMotor"),
        ODOMETRY_MODULE_RIGHT("rightRearMotor"),
        ODOMETRY_MODULE_BACK("leftRearMotor"),
        WEBCAM_LEFT("WebcamLeft"),
        WEBCAM_RIGHT("WebcamRight"),
        DUCK_SPINNER("duckServo"),
        SHOULDER_SERVO("shoulderServo"),
        SHOULDER_MOTOR("shoulderMotor"),
        WRIST_SERVO("wristServo"),
        CLAW_SERVO("clawServo"),
        INTAKE_SWEEPER_MOTOR("intakeSweeperMotor"),
        INTAKE_SENSOR("intakeSensor"),
        INTAKE_ROTATE_SERVO("intakeRotateServo"),
        LIFT_MOTOR("extensionArmMotor"),
        LIFT_LIMIT_SWITCH_RETRACTION("retractionLimitSwitch"),
        LIFT_LIMIT_SWITCH_EXTENSION("extensionLimitSwitch"),
        LIFT_SERVO("deliveryServo"),
        LED_PORT1("ledPort1"),
        LED_PORT2("ledPort2");


        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    public enum Subsystem {
        MECANUM_DRIVE,
        DUCK_SPINNER,
        ODOMETRY,
        INTAKE,
        WEBCAM_LEFT,
        WEBCAM_RIGHT,
        ARM,
        LIFT,
        LED_BLINKER
    }

    Set<Subsystem> capabilities;
    public OpenCvCamera activeWebcam;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    private FreightFrenzyMatchInfo robotMode;
    Map<String, FTCRobotSubsystem> subsystemMap;
    private FreightFrenzyStartSpot color;
    private String activeWebcamName;
    private AllianceColor allianceColor;
    private ElapsedTime timer;
    private LinearOpMode opMode;

    private boolean dataLoggingEnabled = true;

    public boolean isDataLoggingEnabled() {
        return dataLoggingEnabled;
    }

    int cameraMonitorViewId;

    boolean isCapableOf(Subsystem subsystem) {
        return capabilities.contains(subsystem);
    }

    private AdafruitIMU8863 imu;
    public MecanumDriveFreightFrenzy mecanum;
    public LoopTimer loopTimer;
    public DuckSpinner duckSpinner;
    public FFArm arm;
    public FFIntake intake;
    public OpenCvWebcam webcamLeft;
    public OpenCvWebcam webcamRight;
    public FFExtensionArm lift;
    public RevLEDBlinker ledBlinker;

    public FreightFrenzyRobotRoadRunner(HardwareMap hardwareMap, Telemetry telemetry, Configuration config, DataLogging dataLog, DistanceUnit units, LinearOpMode opMode) {
        timer = new ElapsedTime();
        loopTimer = new LoopTimer();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        this.opMode = opMode;
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        this.subsystemMap = new HashMap<String, FTCRobotSubsystem>();
        setCapabilities(Subsystem.values());
        enableDataLogging();
        color = FreightFrenzyStartSpot.BLUE_WALL;
        if (PersistantStorage.getStartSpot() != null) {
            color = PersistantStorage.getStartSpot();
        } else {
            color = FreightFrenzyStartSpot.BLUE_WALL;
        }
        allianceColor = AllianceColor.getAllianceColor(color);
    }

    /*
     * This function should be called, if needed, before createRobot() call
     */
    public void setCapabilities(Subsystem[] subsystems) {
        capabilities = new HashSet<Subsystem>(Arrays.asList(subsystems));
    }

    public void setColor(FreightFrenzyStartSpot color) {
        this.color = color;
        allianceColor = AllianceColor.getAllianceColor(color);
    }

    public FreightFrenzyStartSpot getColor() {

        return color;
    }

    /**
     * Create the robot should be called from the teleop or auto opmode.
     *
     * @return
     */

    @Override
    public boolean createRobot() {
        imu = new AdafruitIMU8863(hardwareMap, null, "IMU", HardwareName.IMU.hwName);
        color = PersistantStorage.getStartSpot();
        if (capabilities.contains(Subsystem.MECANUM_DRIVE)) {
            mecanum = new MecanumDriveFreightFrenzy(HardwareName.CONFIG_FL_MOTOR.hwName, HardwareName.CONFIG_BL_MOTOR.hwName, HardwareName.CONFIG_FR_MOTOR.hwName, HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap);
            subsystemMap.put(mecanum.getName(), mecanum);
        }

        if (capabilities.contains(Subsystem.ARM)) {
            arm = new FFArm(hardwareMap, telemetry);
            subsystemMap.put(arm.getName(), arm);
        }

        if (capabilities.contains(Subsystem.DUCK_SPINNER)) {
            duckSpinner = new DuckSpinner(hardwareMap, telemetry);
            subsystemMap.put(duckSpinner.getName(), duckSpinner);
        }

        if (capabilities.contains(Subsystem.LIFT)) {
            // the extension arm needs to know what the alliance color is because motors and servos have to get reversed
            lift = new FFExtensionArm(allianceColor, hardwareMap, telemetry);
            subsystemMap.put(lift.getName(), lift);
        }
        // THE WEBCAM PROCESSING TAKES UP A BUNCH OF RESOURCES. PROBABLY NOT A GOOD IDEA TO RUN THIS IN TELEOP
        if (FreightFrenzyMatchInfo.getMatchPhase() == FreightFrenzyMatchInfo.MatchPhase.AUTONOMOUS)
            switch (color) {
                case RED_WALL:
                case RED_WAREHOUSE:
                    if (capabilities.contains(Subsystem.WEBCAM_LEFT)) {
                        // Timeout for obtaining permission is configurable. Set before opening.
                        //activeWebcam = webcamLeft;
                        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                        webcamLeft = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "WebcamLeft"), cameraMonitorViewId);
                        activeWebcam = webcamLeft;
                        activeWebcamName = "webcamLeft";
                        webcamLeft.setMillisecondsPermissionTimeout(2500);
                    }
                    break;
                case BLUE_WAREHOUSE:
                case BLUE_WALL:
                    if (capabilities.contains(Subsystem.WEBCAM_RIGHT)) {


                        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
                        webcamRight = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "WebcamRight"), cameraMonitorViewId);
                        activeWebcam = webcamRight;
                        activeWebcamName = "webcamRight";
                        webcamRight.setMillisecondsPermissionTimeout(2500);
                    }
                    break;
            }

        if (capabilities.contains(Subsystem.LED_BLINKER)) {
            ledBlinker = new RevLEDBlinker(2, RevLED.Color.GREEN, hardwareMap,
                    HardwareName.LED_PORT1.hwName, HardwareName.LED_PORT2.hwName);
            subsystemMap.put(ledBlinker.getName(), ledBlinker);
        }

        if (capabilities.contains(Subsystem.INTAKE)) {
            intake = new FFIntake(hardwareMap, telemetry, ledBlinker);
            subsystemMap.put(intake.getName(), intake);
        }

        init();
        return true;
    }

    /**
     * Every system has an init. Call it.
     */
    @Override
    public void init() {
        dataLog.logData("Init starting");
        lift.setDataLog(dataLog);
        lift.enableDataLogging();
        if (!lift.init(config)) {
            if (dataLoggingEnabled)
                dataLog.logData(lift.getName() + " initialization failed");
        }
        timer.reset();
        while (!lift.isInitComplete()) {
            update();

            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway
                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                break;
            }
            telemetry.update();
            opMode.idle();
        }
        Map<String, FTCRobotSubsystem> subsystemMapNew = new HashMap<String, FTCRobotSubsystem>(subsystemMap);
        subsystemMapNew.remove(lift.getName());

        for (FTCRobotSubsystem subsystem : subsystemMapNew.values()) {
            subsystem.setDataLog(dataLog);
            subsystem.enableDataLogging();
            if (!subsystem.init(config)) {
                if (dataLoggingEnabled)
                    dataLog.logData(subsystem.getName() + " initialization failed");
            }
        }

        // wait until all the updates are complete or until the timer has expired
        timer.reset();
        while (!isInitComplete()) {
            update();

            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway
                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                break;
            }
            telemetry.update();
            opMode.idle();
        }
    }

    boolean isLiftInit;

    public boolean initPartOne() {
        isLiftInit = false;
        lift.init(config);
        while (!lift.isInitComplete()) {
            lift.update();
        }
        if (lift.isInitComplete()) {
            isLiftInit = true;
        }
        return isLiftInit;
    }


    /*
     * Every system must tell us when its init is complete. When all of the inits are complete, the
     * robot init is complete.
     *
     * @return
     */
    @Override
    public boolean isInitComplete() {
        boolean result = true;
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            if (subsystem.isInitComplete()) {
                if (dataLoggingEnabled) {
                    dataLog.logData("Init complete for " + subsystem.getName());
                }

            } else {
                dataLog.logData("Init is not complete for " + subsystem.getName());
            }
            result &= subsystem.isInitComplete();
        }
        if (dataLoggingEnabled && result == true) {
            dataLog.logData("Init complete");
        }
        return result;
    }

    /**
     * Every system has an update() method that can be used to run a state machine for that system.
     * Note that some systems don't have a state machine but the update() method will be there
     * anyway just in case that changes in the future.
     */
    @Override
    public void update() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.update();
        }
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.timedUpdate(timerValueMsec);
        }
    }

    @Override
    public void shutdown() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.shutdown();
        }
    }

    /**
     * For each subsystem that supports logging turn it on.
     */
    public void enableDataLogging() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.enableDataLogging();
        }
        dataLoggingEnabled = true;
    }

    /**
     * For each subsystem that supports logging, turn it off
     */
    public void disableDataLogging() {
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.disableDataLogging();
        }
        dataLoggingEnabled = false;
    }

    private void log(String stringToLog) {
        if (dataLog != null && dataLoggingEnabled) {
            dataLog.logData(stringToLog);

        }
    }

    public boolean getCurrentPosition(Position position) {
        return true;
    }

    @Override
    public double getCurrentRotation(AngleUnit unit) {
        return 0;
    }

    public double getCurrentRotationIMU(AngleUnit unit) {
        return unit.fromDegrees(imu.getHeading());
    }

    public boolean getCurrentRobotPosition(RobotPosition position) {
        return true;
    }

    public String getWebcamName() {
        return activeWebcamName;
    }
}

