package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LoopTimer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.MecanumDriveFreightFrenzy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PowerPlayRobot implements FTCRobot {

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

        WEBCAM("Webcam"),

        CONE_GRABBER_SERVO("coneGrabberServo"),
        CONE_GRABBER_ARM_SERVO("coneGrabberArmServo"),

        DISTANCE_SENSOR_NORMAL("distanceSensorNormal"),
        DISTANCE_SENSOR_INVERSE("distanceSensorInverse"),

        CYCLE_TRACKER("cycleTracker"),
        SPEED_CONTROLLER("speedController"),

        LEFT_LIFT_MOTOR("leftLiftMotor"),
        LEFT_LIFT_LIMIT_SWITCH_RETRACTION("leftLiftRetractionLimitSwitch"),
        LEFT_LIFT_LIMIT_SWITCH_EXTENSION("leftLiftExtensionLimitSwitch"),

        LED_PORT1("ledPort1"),
        LED_PORT2("ledPort2"),
        LED_STRIP("ledStrip");

        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    public enum Subsystem {
        MECANUM_DRIVE,
        ODOMETRY,
        WEBCAM,
        LEFT_LIFT,
        CONE_GRABBER,
        CONE_GRABBER_ARM_CONTROLLER,
        DISTANCE_SENSOR_NORMAL,
        DISTANCE_SENSOR_INVERSE,
        CYCLE_TRACKER,
        SPEED_CONTROLLER
        //DISTANCE_SENSOR_INVERSE
        //LED_BLINKER,
        //LED_STRIP,
    }

    Set<Subsystem> capabilities;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    private MatchPhase matchPhase;
    Map<String, FTCRobotSubsystem> subsystemMap;
    private ElapsedTime timer;
    private LinearOpMode opMode;


    private boolean dataLoggingEnabled = true;

    public boolean isDataLoggingEnabled() {
        return dataLoggingEnabled;
    }

    private AdafruitIMU8863 imu;
    public PowerPlayMecanumDrive mecanum;
    public LoopTimer loopTimer;
    public PowerPlayLeftLift leftLift;
    public PowerPlayConeGrabber coneGrabber;
    public PowerPlayConeGrabberLiftController coneGrabberArmController;
    public PowerPlayWebcam webcam;
    public PowerPlay2mDistanceSensor distanceSensorForNormal;
    public PowerPlay2mDistanceSensor distanceSensorForInverse;
    public PowerPlayCycleTracker cycleTracker;
    public PowerPlaySpeedController speedController;
    public PowerPlayRobotModes robotModes;
    //public RevLEDBlinker ledBlinker;
    //public FFBlinkinLed ledStrip;

    public PowerPlayRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config,
                          DataLogging dataLog, DistanceUnit units, LinearOpMode opMode) {
        timer = new ElapsedTime();
        loopTimer = new LoopTimer();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        this.opMode = opMode;
        robotModes = new PowerPlayRobotModes();
        this.subsystemMap = new HashMap<String, FTCRobotSubsystem>();
        setCapabilities(Subsystem.values());
        enableDataLogging();

        this.matchPhase = PowerPlayPersistantStorage.getMatchPhase();
    }

    /*
     * This function should be called, if needed, before createRobot() call
     */
    public void setCapabilities(Subsystem[] subsystems) {
        capabilities = new HashSet<Subsystem>(Arrays.asList(subsystems));
    }

    boolean isCapableOf(Subsystem subsystem) {
        return capabilities.contains(subsystem);
    }

    /**
     * Create the robot should be called from the teleop or auto opmode.
     *
     * @return
     */

    @Override
    public boolean createRobot() {
        imu = new AdafruitIMU8863(hardwareMap, null, "IMU", HardwareName.IMU.hwName);
        if (capabilities.contains(Subsystem.MECANUM_DRIVE)) {
            mecanum = new PowerPlayMecanumDrive(HardwareName.CONFIG_FL_MOTOR.hwName, HardwareName.CONFIG_BL_MOTOR.hwName, HardwareName.CONFIG_FR_MOTOR.hwName, HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap);
            subsystemMap.put(mecanum.getName(), mecanum);
        }

        // Only setup and init the camera if this is autonomous. It takes up CPU and memory and is not needed in teleop.
        // Note that this does not actually start the camera streaming. The autonomous opmode must do that because it
        // needs to set the pipeline for the camera.
        if (capabilities.contains(Subsystem.WEBCAM) && matchPhase == MatchPhase.AUTONOMOUS) {
            webcam = new PowerPlayWebcam(hardwareMap, telemetry, HardwareName.WEBCAM.hwName);
            subsystemMap.put(webcam.getName(), webcam);
        }

        if (capabilities.contains(Subsystem.LEFT_LIFT)) {
            leftLift = new PowerPlayLeftLift(hardwareMap, telemetry);
            subsystemMap.put(leftLift.getName(), leftLift);
        }

        if (capabilities.contains(Subsystem.CYCLE_TRACKER)) {
            cycleTracker = new PowerPlayCycleTracker(hardwareMap, telemetry);
            subsystemMap.put(cycleTracker.getName(), cycleTracker);
        }

        if (capabilities.contains(Subsystem.DISTANCE_SENSOR_NORMAL)) {
            distanceSensorForNormal = new PowerPlay2mDistanceSensor(hardwareMap, telemetry, HardwareName.DISTANCE_SENSOR_NORMAL.hwName, DistanceUnit.INCH);
            subsystemMap.put(distanceSensorForNormal.getName(), distanceSensorForNormal);
        }

        if (capabilities.contains(Subsystem.DISTANCE_SENSOR_INVERSE)) {
            distanceSensorForInverse = new PowerPlay2mDistanceSensor(hardwareMap, telemetry, HardwareName.DISTANCE_SENSOR_INVERSE.hwName, DistanceUnit.INCH);
            subsystemMap.put(distanceSensorForInverse.getName(), distanceSensorForInverse);
        }

        if (capabilities.contains(Subsystem.CONE_GRABBER)) {
            coneGrabber = new PowerPlayConeGrabber(hardwareMap, telemetry, cycleTracker);
            subsystemMap.put(coneGrabber.getName(), coneGrabber);
        }

        if (capabilities.contains(Subsystem.CONE_GRABBER_ARM_CONTROLLER)) {
            coneGrabberArmController = new PowerPlayConeGrabberLiftController(coneGrabber,leftLift, cycleTracker);
            subsystemMap.put(coneGrabberArmController.getName(), coneGrabberArmController);
        }

        if (capabilities.contains(Subsystem.SPEED_CONTROLLER)) {
            speedController = new PowerPlaySpeedController(hardwareMap, telemetry, this);
            subsystemMap.put(speedController.getName(), speedController);
        }

//        if (capabilities.contains(Subsystem.LED_BLINKER)) {
//            ledBlinker = new RevLEDBlinker(2, RevLED.Color.GREEN, hardwareMap,
//                    HardwareName.LED_PORT1.hwName, HardwareName.LED_PORT2.hwName);
//            subsystemMap.put(ledBlinker.getName(), ledBlinker);
//        }
//
//        if (capabilities.contains(Subsystem.LED_STRIP)) {
//            ledStrip = new FFBlinkinLed(hardwareMap);
//            subsystemMap.put(ledStrip.getName(), ledStrip);
//        }

        init();
        return true;
    }

    /**
     * Every system has an init. Call it.
     */
    @Override
    public void init() {
        dataLog.logData("Robot Init starting");
        timer.reset();

        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
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
        //ledStrip.setPattern(RevBlinkinLedDriver.BlinkinPattern.COLOR_WAVES_FOREST_PALETTE);
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
            // the subsystems now take care of their own logging for init and init completion
            // This is so we don't dump huge amounts of repetitive data into the log file.
//            if (subsystem.isInitComplete()) {
//                if (dataLoggingEnabled) {
//                    dataLog.logData("Init complete for " + subsystem.getName());
//                }
//
//            } else {
//                dataLog.logData("Init is not complete for " + subsystem.getName());
//            }
            result &= subsystem.isInitComplete();
        }
        if (dataLoggingEnabled && result == true) {
            dataLog.logData("Robot init complete");
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

}

