//package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;
//
//import com.qualcomm.robotcore.hardware.HardwareMap;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
//import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
//import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
//import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
//
//public class CenterStageIntakeDeliveryCoordinator implements FTCRobotSubsystem {
//
//    //*********************************************************************************************
//    //          ENUMERATED TYPES
//    //
//    // user defined types
//    //
//    //*********************************************************************************************
//    public enum Command {
//        OFF,
//        SETUP_FOR_DELIVERY,
//        SETUP_FOR_INTAKE
//    }
//
//    private Command command = Command.OFF;
//
//    public enum State {
//        PRE_INIT,
//        WAITING_FOR_LIFT_INIT_TO_COMPLETE,
//        WAITING_FOR_SERVOS_INIT_TO_COMPLETE,
//        READY,
//        INTAKING,
//        LIFT_MOVING_TO_SETUP_FOR_DELIVERY,
//        WRIST_SERVO_MOVING_TO_SETUP_FOR_DELIVERY,
//        WRIST_CURLING,
//        LIFT_MOVING_TO_WRIST_CURLING_POSITION,
//        WRIST_SERVO_MOVING_TO_POSITION,
//        LIFT_MOVING_TO_INTAKE_POSITION,
//        LIFT_MOVING_TO_HIGH_POSITION,
//        ARM_MOVING_TO_HIGH_DROP_POSITION,
//        LIFT_MOVING_TO_MEDIUM_POSITION,
//        ARM_MOVING_TO_MEDIUM_DROP_POSITION,
//        LIFT_MOVING_TO_LOW_POSITION,
//        ARM_MOVING_TO_LOW_DROP_POSITION
//    }
//
//    private State state = State.PRE_INIT;
//
//    public State getState() {
//        return state;
//    }
//
//    //*********************************************************************************************
//    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
//    //
//    // can be accessed only by this class, or by using the public
//    // getter and setter methods
//    //*********************************************************************************************
//    private final String DELIVERY_CONTROLLER_NAME = CenterStageRobot.HardwareName.DELIVERY_CONTROLLER.hwName;
//
//    private CenterStageLift lift;
//    public CenterStageArmServo armServo;
//    public CenterStageWristServo wristServo;
//
//    private DataLogging logFile;
//    private boolean enableLogging = false;
//    private DataLogOnChange logStateOnChange;
//    private DataLogOnChange logCommandOnchange;
//
//    private boolean commandComplete = true;
//    private boolean initComplete = false;
//
//    //*********************************************************************************************
//    //          Constructors
//    //
//    // the function that builds the class when an object is created
//    // from it
//    //*********************************************************************************************
//    public CenterStageIntakeDeliveryCoordinator(HardwareMap hardwareMap, Telemetry telemetry) {
//        lift = new CenterStageLift(hardwareMap, telemetry);
//        armServo = new CenterStageArmServo(hardwareMap, telemetry);
//        wristServo = new CenterStageWristServo(hardwareMap, telemetry);
//
//        command = Command.OFF;
//        state = State.PRE_INIT;
//    }
//
//    //*********************************************************************************************
//    //          Helper Methods
//    //
//    // methods that aid or support the major functions in the class
//    //*********************************************************************************************
//    private void logState() {
//        if (enableLogging && logFile != null) {
//            logStateOnChange.log(getName() + " state = " + state.toString());
//        }
//    }
//
//    private void logCommand(String command) {
//        if (enableLogging && logFile != null) {
//            logCommandOnchange.log(getName() + " command = " + command);
//        }
//    }
//
//    //*********************************************************************************************
//    //          MAJOR METHODS
//    //
//    // public methods that give the class its functionality
//    //*********************************************************************************************
//
//    public void off() {
//        command = Command.OFF;
//        logCommand("Off");
//    }
//
//    public void setupForDelivery() {
//        command = Command.SETUP_FOR_DELIVERY;
//        lift.moveToSetupForDelivery();
//        state = State.LIFT_MOVING_TO_SETUP_FOR_DELIVERY;
//        logCommand("Setup For Delivery");
//    }
//
//    public void setUpForHighPosition() {
//        command = Command.SETUP_FOR_HIGH_DROP;
//        lift.moveToHigh();
//        state = State.LIFT_MOVING_TO_HIGH_POSITION;
//        logCommand("Setup For High Drop");
//    }
//
//    public void setUpForMediumPosition() {
//        command = Command.SETUP_FOR_MEDIUM_DROP;
//        lift.moveToMedium();
//        state = State.LIFT_MOVING_TO_MEDIUM_POSITION;
//        logCommand("Setup For Medium Drop");
//    }
//
//    public void setUpForLowPosition() {
//        command = Command.SETUP_FOR_LOW_DROP;
//        lift.moveToLow();
//        state = State.LIFT_MOVING_TO_LOW_POSITION;
//        logCommand("Setup For Low Drop");
//    }
//
//    public boolean isCommandComplete() {
//        return commandComplete;
//    }
//
//    public boolean isPositionReached() {
//        if (state == State.READY) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public String getName() {
//        return DELIVERY_CONTROLLER_NAME;
//    }
//
//    @Override
//    public boolean isInitComplete() {
//        return initComplete;
//    }
//
//    @Override
//    public boolean init(Configuration config) {
//        lift.init(config);
//        update();
//        state = State.WAITING_FOR_LIFT_INIT_TO_COMPLETE;
//        commandComplete = false;
//        return true;
//    }
//
//    @Override
//    public void update() {
//        lift.update();
//        logState();
//
//        switch (state) {
//            case PRE_INIT:
//                break;
//
//            case WAITING_FOR_LIFT_INIT_TO_COMPLETE:
//                if (lift.isInitComplete()) {
//                    armServo.intakePosition();
//                    wristServo.intakePosition();
//                    state = State.WAITING_FOR_SERVOS_INIT_TO_COMPLETE;
//                }
//                break;
//
//            case WAITING_FOR_SERVOS_INIT_TO_COMPLETE:
//                if (armServo.isPositionReached() && wristServo.isPositionReached()) {
//                    initComplete = true;
//                    commandComplete = true;
//                    state = State.READY;
//                }
//                break;
//
//            case READY:
//                break;
//
//            case LIFT_MOVING_TO_SETUP_FOR_DELIVERY:
//                if (lift.isPositionReached()) {
//                    wristServo.setSetUpForDeliveryPosition();
//                    state = State.WRIST_SERVO_MOVING_TO_SETUP_FOR_DELIVERY;
//                }
//            break;
//
//            case WRIST_SERVO_MOVING_TO_SETUP_FOR_DELIVERY:
//                if (wristServo.isPositionReached()) {
//                    lift.moveToWristCurlPosition();
//                    state = State.LIFT_MOVING_TO_WRIST_CURLING_POSITION;
//                }
//                break;
//
//            case LIFT_MOVING_TO_WRIST_CURLING_POSITION:
//                if (lift.isPositionReached()) {
//                    wristServo.highDropPosition();
//                    state = State.WRIST_SERVO_MOVING_TO_POSITION;
//                }
//                break;
//
//            case WRIST_SERVO_MOVING_TO_POSITION:
//                switch (command){
//                    case SETUP_FOR_DELIVERY:
//                        if (wristServo.isPositionReached()) {
//                            lift.moveToIntake();
//                            state = State.LIFT_MOVING_TO_INTAKE_POSITION;
//                        }
//                        break;
//                    case SETUP_FOR_HIGH_DROP:
//                    case SETUP_FOR_MEDIUM_DROP:
//                    case SETUP_FOR_LOW_DROP:
//                        if (wristServo.isPositionReached()){
//                            commandComplete = true;
//                            state = State.READY;
//                        }
//                        break;
//                }
//
//                break;
//
//            case LIFT_MOVING_TO_INTAKE_POSITION:
//                if (lift.isPositionReached()) {
//                    commandComplete = true;
//                    state = State.READY;
//                }
//                break;
//
//            case LIFT_MOVING_TO_HIGH_POSITION:
//                if (lift.isPositionReached()) {
//                    armServo.highDropPosition();
//                    state = State.ARM_MOVING_TO_HIGH_DROP_POSITION;
//                }
//                break;
//
//            case ARM_MOVING_TO_HIGH_DROP_POSITION:
//                if (armServo.isPositionReached()) {
//                    wristServo.highDropPosition();
//                    state = State.WRIST_SERVO_MOVING_TO_POSITION;
//                }
//                break;
//
//            case LIFT_MOVING_TO_MEDIUM_POSITION:
//                if (lift.isPositionReached()) {
//                    armServo.mediumDropPosition();
//                    state = State.ARM_MOVING_TO_MEDIUM_DROP_POSITION;
//                }
//                break;
//
//            case ARM_MOVING_TO_MEDIUM_DROP_POSITION:
//                if (armServo.isPositionReached()) {
//                    wristServo.mediumDropPosition();
//                    state = State.WRIST_SERVO_MOVING_TO_POSITION;
//                }
//                break;
//
//            case LIFT_MOVING_TO_LOW_POSITION:
//                if (lift.isPositionReached()) {
//                    armServo.lowDropPosition();
//                    state = State.ARM_MOVING_TO_LOW_DROP_POSITION;
//                }
//                break;
//
//            case ARM_MOVING_TO_LOW_DROP_POSITION:
//                if (armServo.isPositionReached()) {
//                    wristServo.lowDropPosition();
//                    state = State.WRIST_SERVO_MOVING_TO_POSITION;
//                }
//                break;
//        }
//
//
//    }
//
//    @Override
//    public void shutdown() {
//        // set the delivery mechanism to some known position
//    }
//
//    @Override
//    public void setDataLog(DataLogging logFile) {
//        this.logFile = logFile;
//        logCommandOnchange = new DataLogOnChange(logFile);
//        logStateOnChange = new DataLogOnChange(logFile);
//    }
//
//    @Override
//    public void enableDataLogging() {
//        enableLogging = true;
//    }
//
//    @Override
//    public void disableDataLogging() {
//        enableLogging = false;
//    }
//
//    @Override
//    public void timedUpdate(double timerValueMsec) {
//
//    }
//}