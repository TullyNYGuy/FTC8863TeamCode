package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.BaseGrabberServo;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.DualLift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArmConstants;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Gripper;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.GripperRotator;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakePusherServos;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakeWheels;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20_ORBITAL;

public class UltimateGoalRobot implements FTCRobot {

    public enum HardwareName {

        FRONT_LEFT_MOTOR("FrontLeft"),
        FRONT_RIGHT_MOTOR("FrontRight"),
        BACK_LEFT_MOTOR("BackLeft"),
        BACK_RIGHT_MOTOR("BackRight"),
        IMU("imu"),
        IMU_2000("IMU_2000"),
        ODOMETRY_MODULE_LEFT("FrontLeft"),
        ODOMETRY_MODULE_RIGHT("BackLeft"),
        ODOMETRY_MODULE_BACK("BackRight"),
        LIFT_RIGHT_MOTOR("LiftMotorRight"),
        LIFT_RIGHT_NAME("LiftRight"),
        LIFT_RIGHT_EXTENSION_SWITCH("LiftExtensionLimitSwitchRight"),
        LIFT_RIGHT_RETRACTION_SWITCH("LiftRetractionLimitSwitchRight"),
        LIFT_LEFT_MOTOR("LiftMotorLeft"),
        LIFT_LEFT_NAME("LiftLeft"),
        LIFT_LEFT_EXTENSION_SWITCH("LiftExtensionLimitSwitchLeft"),
        LIFT_LEFT_RETRACTION_SWITCH("LiftRetractionLimitSwitchLeft"),
        INTAKE_RIGHT_MOTOR("IntakeMotorRight"),
        INTAKE_LEFT_MOTOR("IntakeMotorLeft"),
        INTAKE_SWITCH_BACK_LEFT("IntakeSwitchBackLeft"),
        INTAKE_SWITCH_BACK_RIGHT("IntakeSwitchBackRight"),
        INTAKE_SWITCH_FRONT_LEFT("IntakeSwitchFrontLeft"),
        INTAKE_SWITCH_FRONT_RIGHT("IntakeSwitchFrontRight"),
        EXT_ARM_SERVO("ExtensionArmServoMotor"),
        EXT_ARM_MOTOR_NAME_FOR_ENCODER_PORT("BackRight"),
        EXT_ARM_RETRACTION_SWITCH("RetractionLimitSwitchArm"),
        EXT_ARM_EXTENSION_SWITCH("ExtensionLimitSwitchArm"),
        GRIPPER_SERVO("Gripper"),
        INTAKE_PUSHER_RIGHT_SERVO("IntakePusherRight"),
        INTAKE_PUSHER_LEFT_SERVO("IntakePusherLeft"),
        GRIPPER_ROTATOR_SERVO("GripperRotator"),
        INTAKE_SWITCH("IntakeLimitSwitch"),
        BASE_MOVER_RIGHT_SERVO("FoundationGrabberRight"),
        BASE_MOVER_LEFT_SERVO("FoundationGrabberLeft"),
        ;

        public final String hwName;

        HardwareName(String name) {
            this.hwName = name;
        }
    }

    public enum Subsystem {
        MECANUM,
        //INTAKE_MOTORS,
        //INTAKE_PUSHER,
        // these are now part of the IntakeWheels object
        //INTAKE_LIMIT_SW,
        ODOMETRY,
        //LIFT,
        //EXT_ARM,
        //BASE_MOVER
    }

    Set<Subsystem> capabilities;

    HardwareMap hardwareMap;
    Telemetry telemetry;
    DistanceUnit units;
    Configuration config;
    private DataLogging dataLog;
    Map<String, FTCRobotSubsystem> subsystemMap;

    private ElapsedTime timer;
    private LinearOpMode opMode;

    private boolean dataLoggingEnabled = true;

    public boolean isDataLoggingEnabled() {
        return dataLoggingEnabled;
    }

    boolean isCapableOf(Subsystem subsystem) {
        return capabilities.contains(subsystem);
    }

    private AdafruitIMU8863 imu;
    private Mecanum mecanum;
    private OdometrySystem odometry;
    // private DualLift dualLift;

    /* TODO: Needs initialization */
    private IntakeWheels intake;
    private DualLift lift;
    private ExtensionArm extensionArm;
    private GripperRotator gripperRotator;
    private Gripper gripper;
    private IntakePusherServos intakePusherServos;
    Switch intakeLimitSwitch;
    private BaseGrabberServo baseGrabberServo;
    private Double deportHeight;

    public UltimateGoalRobot(HardwareMap hardwareMap, Telemetry telemetry, Configuration config, DataLogging dataLog, DistanceUnit units, LinearOpMode opMode) {
        timer = new ElapsedTime();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.units = units;
        this.config = config;
        this.dataLog = dataLog;
        enableDataLogging();
        this.opMode = opMode;
        this.subsystemMap = new HashMap<String, FTCRobotSubsystem>();
        setCapabilities(Subsystem.values());

       // capabilities.remove(Subsystem.ODOMETRY);
        // capabilities.remove(Subsystem.LIFT);
    }

    /*
     * This function should be called, if needed, before createRobot() call
     */
    public void setCapabilities(Subsystem[] subsystems) {
        capabilities = new HashSet<Subsystem>(Arrays.asList(subsystems));
    }

    @Override
    public boolean createRobot() {
        imu = new AdafruitIMU8863(hardwareMap, null, "IMU", HardwareName.IMU.hwName);
        if (capabilities.contains(Subsystem.MECANUM)) {

            DcMotor8863 frontLeft = DcMotor8863.createMotorFromFile(config, "FLMotor", hardwareMap);
            DcMotor8863 backLeft = DcMotor8863.createMotorFromFile(config, "BLMotor", hardwareMap);
            DcMotor8863 frontRight = DcMotor8863.createMotorFromFile(config, "FRMotor", hardwareMap);
            DcMotor8863 backRight = DcMotor8863.createMotorFromFile(config, "BRMotor", hardwareMap);

            mecanum = new Mecanum(frontLeft, frontRight, backLeft, backRight, telemetry);
        }
        if (capabilities.contains(Subsystem.ODOMETRY)) {
            OdometryModule left = new OdometryModule(1440, 3.8*Math.PI, units, HardwareName.ODOMETRY_MODULE_LEFT.hwName, hardwareMap);
            OdometryModule right = new OdometryModule(1440, 3.8*Math.PI, units, HardwareName.ODOMETRY_MODULE_RIGHT.hwName, hardwareMap);
            OdometryModule back = new OdometryModule(1440, 3.8*Math.PI, units, HardwareName.ODOMETRY_MODULE_BACK.hwName, hardwareMap);
            odometry = new OdometrySystem(units, left, right, back);
            subsystemMap.put(odometry.getName(), odometry);
        }

        // My preference is to encapsulate as much as possible so that creation code can be reused.
        // So move this stuff into the IntakeWheels. It should know how to create itself. I should
        // not have to know that at the robot level.

//        DcMotor8863 rightIntake = new DcMotor8863("intakeMotorRight", hardwareMap);
//        DcMotor8863 leftIntake = new DcMotor8863("intakeMotorLeft", hardwareMap);
//        rightIntake.setMotorType(ANDYMARK_20_ORBITAL);
//        leftIntake.setMotorType(ANDYMARK_20_ORBITAL);





        init();
        return true;
    }
public void setPosition(double currentpositionx,double currentPositiionY,double currentPositionRot){
        if(odometry != null){

            odometry.setCoordinates(units, currentpositionx, currentPositiionY, AngleUnit.DEGREES, currentPositionRot);
        }else{
            if(imu != null){
                //it dodesnt want to work. implement inti thfr imu clas instead of  tnrd bno0ffimu class
                //imu.stopAccelerationIntegration();
                Position place = new Position(units, currentpositionx, currentPositiionY, 0, 0);
                Velocity velocity = new Velocity(units,0,0,0,0);
                imu.startAccelerationIntegration(place, velocity, 100);
            }
        }
}
    /**
     * Every system has an init. Call it.
     */
    @Override
    public void init() {
        dataLog.logData("Init starting");
        for (FTCRobotSubsystem subsystem : subsystemMap.values()) {
            subsystem.setDataLog(dataLog);
            subsystem.enableDataLogging();
            if (!subsystem.init(config)) {
                if (dataLoggingEnabled)
                    dataLog.logData(subsystem.getName() + " initialization failed");
            }
        }

        if (mecanum != null && !mecanum.init(config)) {
            if (dataLoggingEnabled)
                dataLog.logData("Mecanum initialization failed");
        }

        // inits for the command state machines
        initDeportStateMachine();
        initLiftBlockStateMachine();
        initPlaceBlockStateMachine();
        initPrepareBlockStateMachine();

        // Start IMU-based positioning if Odometry is not enabled
        if(!capabilities.contains(Subsystem.ODOMETRY)) {
            imu.startAccelerationIntegration(null, null, 100);
        }
        // wait until all the updates are complete or until the timer has expired
        timer.reset();
        while (!isInitComplete()) {
            update();

            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway
                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                //How cheerful. How comforting...
                break;
            }
            telemetry.update();
            opMode.idle();
        }
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
        // put the isInitComplete for each subsystem here. In other words repeat this block of code
        // for each subsystem
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

        deportStateUpdate();
        liftBlockStateUpdate();
        placeBlockStateUpdate();
        prepareIntakeUpdate();


//        if (capabilities.contains(Subsystem.INTAKE_LIMIT_SW))
//            updateIntakeSwitches();
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
        dataLoggingEnabled = true;
    }

    /**
     * For each subsystem that supports logging, turn it off
     */
    public void disableDataLogging() {
        dataLoggingEnabled = false;

    }

    private void log(String stringToLog) {
        if (dataLog != null && dataLoggingEnabled) {
            dataLog.logData(stringToLog);

        }
    }

    public boolean getCurrentPosition(Position position) {
        if (odometry != null && odometry.isInitComplete()) {
            odometry.getCurrentPosition(position);
            return true;
        } else if(imu != null) {
            Position p = imu.getPosition();
            position.acquisitionTime = p.acquisitionTime;
            position.unit = p.unit;
            position.x = p.x;
            position.y = p.y;
            position.z = p.z;
            return true;
        } else {
            return false;
        }
    }

    /*
     * Return current robot rotation. If odometry is initialized odometry is used, otherwise IMU is used
     */
    @Override
    public double getCurrentRotation(AngleUnit unit) {
        if (odometry != null && odometry.isInitComplete())
            return odometry.getCurrentRotation(unit);
        else if (imu != null)
            return unit.fromDegrees(imu.getHeading());
        else
            return 0;
    }
    public double getCurrentRotationIMU(AngleUnit unit){
        return unit.fromDegrees(imu.getHeading());
    }
    public boolean getCurrentRobotPosition(RobotPosition position) {
        if (odometry != null && odometry.isInitComplete()) {
            odometry.getCurrentPosition(position);
            return true;
        } else if (imu != null) {
            Position p = imu.getPosition();
            position.x = position.distanceUnit.fromUnit(p.unit, p.x);
            position.y = position.distanceUnit.fromUnit(p.unit, p.y);
            position.rotation = position.angleUnit.fromDegrees(imu.getHeading());
            return true;
        } else {
            return false;
        }
    }

    public void setMovement(MecanumCommands commands) {
        if (mecanum != null)
            mecanum.setMotorPower(commands);
    }

    public void setMaxMovementPower(double maxPower) {
        if (mecanum != null)
            mecanum.setMaxMotorPower(maxPower);
    }

    /*
    public enum GripStates {
        IDLE,
        START,
        GRIPPING,
        COMPLETE
    }

    private GripStates gripState = GripStates.IDLE;

    public void initGripStateMachine() {
        gripState = GripStates.IDLE;
    }

    public void gripBlock() {
        gripState = GripStates.START;
    }

    public void gripStateUpdate() {
        switch (gripState) {
            case IDLE:
                //nothing just chilling
                break;
            case START:
                gripper.grip();
                gripState = GripStates.GRIPPING;
                break;

            case GRIPPING:
                if (gripper.isGripComplete()) {
                    gripState = GripStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
    }

    public boolean isGripBlockComplete() {
        if (gripState == GripStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public GripStates getCurrentGripperState() {
        return gripState;
    }
    */


    //*********************************************
    //BLOCK DEPORTATION//
    //********************************************
    private ElapsedTime deportTimer;

    public enum DeportStates {
        IDLE,
        START,
        LIFT_RAISING,
        ARM_EXTENDING,
        GRIPPER_ROTATING,
        COMPLETE
    }

    private DeportStates deportState = DeportStates.IDLE;
    private DeportStates previousDeportState;

    private void logState(DeportStates deportState) {
        if (dataLog != null && dataLoggingEnabled) {
            if (deportState != previousDeportState) {
                dataLog.logData("Deport state is now ", deportState.toString());
                previousDeportState = deportState;
            }
        }
    }

    public void initDeportStateMachine() {
        deportState = DeportStates.IDLE;
    }


    public void deportBlock() {
        if (deportState == DeportStates.IDLE || deportState == DeportStates.COMPLETE) {
            deportHeight = 8.0;
            log("Robot commanded to deport stone");
            deportState = DeportStates.START;
        }
    }

    public void deportBlockCapstone() {
        //Special Secret Sauce Height//
        if (deportState == DeportStates.IDLE || deportState == DeportStates.COMPLETE) {
            deportHeight = 10.0;
            log("Robot commanded to deport stone(capstone)");
            deportState = DeportStates.START;
        } else {
            log("Robot command to deport stone IGNORED");
        }
    }


    public void deportStateUpdate() {
        switch (deportState) {
            case IDLE:
                //nothing just chilling
                break;
            case START:
                if (lift != null) {
                    lift.goToPosition(deportHeight, 1);
                    deportState = DeportStates.LIFT_RAISING;
                }
                break;
            case LIFT_RAISING:
                if (lift != null) {
                    if (lift.isPositionReached()) {
                        if (extensionArm != null)
                            extensionArm.goToPosition(5, 1);
                        deportState = DeportStates.ARM_EXTENDING;
                    }
                }
            case ARM_EXTENDING:
                if (extensionArm != null && extensionArm.isPositionReached()) {
                    if (gripperRotator != null)
                        gripperRotator.rotateOutward();
                    deportState = DeportStates.GRIPPER_ROTATING;
                }
                break;
            case GRIPPER_ROTATING:
                if (gripperRotator != null && gripperRotator.isRotateOutwardComplete()) {
                    deportState = DeportStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
        logState(deportState);
    }

    public boolean isDeportBlockComplete() {
        if (deportState == DeportStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public DeportStates getCurrentDeportState() {
        return deportState;
    }

    //*********************************************
    //BLOCK LIFTING//
    //********************************************
    private ElapsedTime liftBlockTimer;

    public enum LiftBlockStates {
        IDLE,
        START,
        BLOCK_LIFTING,
        COMPLETE
    }

    private LiftBlockStates liftBlockState = LiftBlockStates.IDLE;
    private LiftBlockStates previousliftBlockState;

    private void logState(LiftBlockStates liftBlockState) {
        if (dataLog != null && dataLoggingEnabled) {
            if (liftBlockState != previousliftBlockState) {
                dataLog.logData("LiftBlock state is now ", liftBlockState.toString());
                previousliftBlockState = liftBlockState;
            }
        }
    }

    private double liftBlockTimerLimit;

    int skyscraperLevel = 0;

    public void resetSkyscraperLevel() {
        log("Robot commanded to reset skyscraper level");
        skyscraperLevel = 0;
    }

    public int getSkyscraperLevel() {
        return skyscraperLevel;
    }

    public void increaseDesiredHeightForLift() {
        log("Robot commanded to increase skyscraper level");
        skyscraperLevel = skyscraperLevel + 1;
        if (skyscraperLevel > 8) {
            skyscraperLevel = 0;
            telemetry.addData("DESIRED HEIGHT =", skyscraperLevel);
        }
        log("Robot commanded to change skyscraper level = " + skyscraperLevel);
    }

    public void initLiftBlockStateMachine() {
        liftBlockTimer = new ElapsedTime();
        liftBlockState = LiftBlockStates.IDLE;
    }


    public void liftBlock() {
        if (liftBlockState == LiftBlockStates.IDLE || liftBlockState == LiftBlockStates.COMPLETE) {
            log("Robot commanded to lift stone");
            liftBlockState = LiftBlockStates.START;
        } else {
            log("Robot command to lift stone IGNORED");
        }
    }

    public void liftBlockStateUpdate() {
        switch (liftBlockState) {
            case IDLE:
                skyscraperLevel = 3;
                //nothing just chilling
                break;
            case START:
                if (lift != null)
                    lift.goToBlockHeights(skyscraperLevel);
                liftBlockState = LiftBlockStates.BLOCK_LIFTING;
                break;
            case BLOCK_LIFTING:
                if (lift != null && lift.isPositionReached()) {
                    liftBlockState = LiftBlockStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
        logState(liftBlockState);
    }

    public boolean isLiftBlockComplete() {
        if (liftBlockState == LiftBlockStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public LiftBlockStates getCurrentLiftState() {
        return liftBlockState;
    }

    //*********************************************
    //BLOCK PLACING//
    //********************************************
    private ElapsedTime placeBlockTimer;

    public enum PlaceBlockStates {
        IDLE,
        EXTENDING,
        GRIPPER_RELEASING,
        COMPLETE
    }

    private PlaceBlockStates placeBlockState = PlaceBlockStates.IDLE;
    private PlaceBlockStates previousPlaceBlockState;

    private void logState(PlaceBlockStates placeBlockState) {
        if (dataLog != null && dataLoggingEnabled) {
            if (placeBlockState != previousPlaceBlockState) {
                dataLog.logData("PlaceBlock state is now ", placeBlockState.toString());
                previousPlaceBlockState = placeBlockState;
            }
        }
    }

    private double placeBlockTimerLimit;

    public void initPlaceBlockStateMachine() {
        placeBlockTimer = new ElapsedTime();
        placeBlockState = PlaceBlockStates.IDLE;
    }


    public void placeBlock() {
        if (placeBlockState == PlaceBlockStates.IDLE.IDLE || placeBlockState == PlaceBlockStates.COMPLETE) {
            log("Robot commanded to place stone");
            placeBlockState = PlaceBlockStates.EXTENDING;
        } else {
            log("Robot command to place stone IGNORED");
        }
    }


    public void placeBlockStateUpdate() {
        switch (placeBlockState) {
            case IDLE:
                //The Driver will extend the arm using joystick then call place block
                break;
            case EXTENDING:
                if (gripper != null)
                    gripper.releaseBlock();
                break;
            case GRIPPER_RELEASING:
                if (gripper != null && gripper.isReleaseComplete()) {
                    placeBlockState = PlaceBlockStates.COMPLETE;
                }
                break;
            case COMPLETE:
                //we chillin'
                break;
        }
        logState(placeBlockState);
    }

    public boolean isPlaceBlockComplete() {
        if (placeBlockState == PlaceBlockStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public PlaceBlockStates getCurrentPlaceBlockState() {
        return placeBlockState;
    }

    //*********************************************
    //Prepare to intake//
    //********************************************

    public enum PrepareIntakeStates {
        IDLE,
        START,
        PREPARATION_PHASE_1_ROTATOR,
        PREPARATION_PHASE_2_RETRACTION,
        PREPARATION_PHASE_3_LOWERING,
        COMPLETE
    }

    private PrepareIntakeStates prepareIntakeState = PrepareIntakeStates.IDLE;
    private PrepareIntakeStates previousPrepareIntakeState;

    private void logState(PrepareIntakeStates prepareIntakeState) {
        if (dataLog != null && dataLoggingEnabled) {
            if (prepareIntakeState != previousPrepareIntakeState) {
                dataLog.logData("PlaceBlock state is now ", prepareIntakeState.toString());
                previousPrepareIntakeState = prepareIntakeState;
            }
        }
    }

    public void initPrepareBlockStateMachine() {
        prepareIntakeState = PrepareIntakeStates.IDLE;
    }


    public void prepareToIntakeBlock() {
        if (prepareIntakeState == PrepareIntakeStates.IDLE || prepareIntakeState == PrepareIntakeStates.COMPLETE) {
            log("Robot commanded to prepare to intake a stone");
            prepareIntakeState = PrepareIntakeStates.START;
        } else {
            log("Robot command to prepare to intake IGNORED");
        }
    }


    public void prepareIntakeUpdate() {
        switch (prepareIntakeState) {
            case IDLE:
                //chillin' like a villain
                break;
            case START:
                if (gripperRotator != null)
                    gripperRotator.rotateInward();
                if (gripper != null)
                    gripper.releaseBlock();
                prepareIntakeState = PrepareIntakeStates.PREPARATION_PHASE_1_ROTATOR;
                break;
            case PREPARATION_PHASE_1_ROTATOR:
                if (gripper != null && gripper.isReleaseComplete()
                        && gripperRotator != null && gripperRotator.isRotateInwardComplete()) {
                    if (extensionArm != null)
                        extensionArm.goToPosition(0, 1);
                    prepareIntakeState = PrepareIntakeStates.PREPARATION_PHASE_2_RETRACTION;
                }
                break;
            case PREPARATION_PHASE_2_RETRACTION:
                if (extensionArm != null && extensionArm.isPositionReached()) {
                    if (lift != null)
                        lift.goToPosition(0, 1);
                    prepareIntakeState = PrepareIntakeStates.PREPARATION_PHASE_3_LOWERING;
                }
                break;
            case PREPARATION_PHASE_3_LOWERING:
                if (lift != null && lift.isPositionReached() && extensionArm != null && extensionArm.isPositionReached()) {
                    prepareIntakeState = PrepareIntakeStates.COMPLETE;
                }
                break;

            case COMPLETE:
                //we chillin'
                break;
        }
        logState(prepareIntakeState);
    }

    public boolean isPrepareIntakeComplete() {
        if (prepareIntakeState == PrepareIntakeStates.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    public PrepareIntakeStates getCurrentPrepareIntakeState() {
        return prepareIntakeState;
    }

    public void baseGrab() {
        if (baseGrabberServo != null) {
            log("Robot commanded to grab foundation");
            baseGrabberServo.grabBase();
        }
    }

    public void baseRelease() {
        if (baseGrabberServo != null) {
            log("Robot commanded to release foundation");
            baseGrabberServo.releaseBase();
        }
    }

}
