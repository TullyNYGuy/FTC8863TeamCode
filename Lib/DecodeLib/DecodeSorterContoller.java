package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class DecodeSorterContoller implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum SorterState {
        WAITING_ARTIFACT_123,
        WAITING_FOR_120_INTAKE,
        ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE,
        WAITING_FOR_240_INTAKE,
        TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE,
        WAITING_FOR_360_INTAKE,
        THREE_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE,
        WAITING_FOR_RAMP_UP,
        THREE_ARTIFACT_IN_SORTER_123_PRESHOOT,
        WAITING_FOR_FIRST_SHOT_TO_COMPLETE,
        TWO_ARTIFACT_LEFT_312_SHOOTING_CYCLE,
        WAITING_FOR_SECOND_SHOT_TO_COMPLETE,
        ONE_ARTIFACT_LEFT_231_SHOOTING_CYCLE,
        WAITING_FOR_THIRD_SHOT_TO_COMPLETE,
        ZERO_ARTIFACT_LEFT_123_SHOOTING_CYCLE;
    }

    private SorterState currentState = SorterState.WAITING_ARTIFACT_123;

    private enum ShootCommand {
        SHOOT_ONE,
        SHOOT_TWO,
        SHOOT_THREE,
        SHOOT_NONE;
    }

    private ShootCommand shootCommand = ShootCommand.SHOOT_NONE;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private DecodeColorSensorController colorSensorController;
    private DecodeSorterMotor sorterMotor;
    private DecodeRampServo rampServo;
    private DecodeIntakeMotor decodeIntakeMotor;
    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods For Implementing FTCRobotSubsystem
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private final String SUB_SYSTEM_NAME = DecodeRobot.HardwareName.SORTER_CONTROLLER.hwName;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeSorterContoller(HardwareMap hardwareMap, Telemetry telemetry,
                                 DecodeColorSensorController decodeColorSensorController,
                                 DecodeSorterMotor sorterMotor,
                                 DecodeRampServo decodeRampServo,
                                 DecodeIntakeMotor decodeIntakeMotor) {
        this.colorSensorController = decodeColorSensorController;
        this.sorterMotor = sorterMotor;
        this.rampServo = decodeRampServo;
        this.decodeIntakeMotor = decodeIntakeMotor;
        this.currentState = SorterState.WAITING_ARTIFACT_123;
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public String getState(){
        return currentState.toString();
    }
    public void shootOne() {
        shootCommand = ShootCommand.SHOOT_ONE;
    }

    public void shootTwo() {
        shootCommand = ShootCommand.SHOOT_TWO;
    }

    public void shootThree() {
        shootCommand = ShootCommand.SHOOT_THREE;
    }

    public void intakeOn(){
        decodeIntakeMotor.on();
    }

    public void intakeOff(){
        decodeIntakeMotor.off();
    }

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    @Override
    public void update() {
        switch (currentState) {

            // no artifacts in the sorter
            // intake should be running
            case WAITING_ARTIFACT_123:
                colorSensorController.getFreshData();
                if (colorSensorController.isArtifactPresent()) {
                    // got 1st artifact in the sorter
                    colorSensorController.colorSensorsOff();
                    sorterMotor.moveToPosition(120);
                    currentState = SorterState.WAITING_FOR_120_INTAKE;
                }
                break;
                
                // waiting for motor to arrive at position
            case WAITING_FOR_120_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    colorSensorController.colorSensorsOn();
                    currentState = SorterState.ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE;
                }
                break;

                // 1 artifact in the sorter
            case ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE:
                colorSensorController.getFreshData();
                if (colorSensorController.isArtifactPresent()) {
                    // got 2nd artifact in the sorter
                    sorterMotor.moveToPosition(240);
                    currentState = SorterState.WAITING_FOR_240_INTAKE;
                }
                break;

            // waiting for motor to arrive at position
            case WAITING_FOR_240_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    colorSensorController.colorSensorsOn();
                    currentState = SorterState.TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE;
                }
                break;

            // 2 artifacts in the sorter
            case TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE:
                colorSensorController.getFreshData();
                if (colorSensorController.isArtifactPresent()) {
                    // got 3rd artifact in the sorter
                    // prepare to shoot
                    colorSensorController.colorSensorsOff();
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_360_INTAKE;
                }
                break;

            // waiting for motor to arrive at position
            case WAITING_FOR_360_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    // prepare to shoot
                    sorterMotor.resetEncoder();
                    rampServo.upPosition();
                    intakeOff();
                    currentState = SorterState.WAITING_FOR_RAMP_UP;
                }
                break;

//            case THREE_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE:
//                break;

            case WAITING_FOR_RAMP_UP:
                if (rampServo.isPositionReached()) {
                    currentState = SorterState.THREE_ARTIFACT_IN_SORTER_123_PRESHOOT;
                }
                break;

            // 3 artifacts in the sorter
            case THREE_ARTIFACT_IN_SORTER_123_PRESHOOT:
                if (shootCommand == ShootCommand.SHOOT_ONE) {
                    // shoot 1 artifact
                    colorSensorController.colorSensorsOff();
                    sorterMotor.moveToPosition(120);
                    currentState = SorterState.WAITING_FOR_FIRST_SHOT_TO_COMPLETE;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                if (shootCommand == ShootCommand.SHOOT_TWO) {
                    // shoot 2 artifacts
                    sorterMotor.moveToPosition(240);
                    currentState = SorterState.WAITING_FOR_SECOND_SHOT_TO_COMPLETE;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                if (shootCommand == ShootCommand.SHOOT_THREE) {
                    // shoot 3 artifacts
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_THIRD_SHOT_TO_COMPLETE;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                break;

            // waiting for motor to arrive at position
            case WAITING_FOR_FIRST_SHOT_TO_COMPLETE:
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.TWO_ARTIFACT_LEFT_312_SHOOTING_CYCLE;
                }
                break;

            // 2 artifacts lerft in the sorter
            case TWO_ARTIFACT_LEFT_312_SHOOTING_CYCLE:
                if (shootCommand == ShootCommand.SHOOT_ONE) {
                    // shoot 1 artifact
                    sorterMotor.moveToPosition(240);
                    currentState = SorterState.WAITING_FOR_SECOND_SHOT_TO_COMPLETE;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                // there are 2 artifacts left. If driver says shoot 3, we can only really shoot 2
                if (shootCommand == ShootCommand.SHOOT_TWO || shootCommand == ShootCommand.SHOOT_THREE) {
                    // shoot 2 artifacts
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_THIRD_SHOT_TO_COMPLETE;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                break;

            // waiting for motor to arrive at position
            case WAITING_FOR_SECOND_SHOT_TO_COMPLETE:
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.ONE_ARTIFACT_LEFT_231_SHOOTING_CYCLE;
                }
                break;

            // waiting for motor to arrive at position
            case WAITING_FOR_THIRD_SHOT_TO_COMPLETE:
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.ZERO_ARTIFACT_LEFT_123_SHOOTING_CYCLE;
                }
                break;

             // 1 artifact left in the sorter
            case ONE_ARTIFACT_LEFT_231_SHOOTING_CYCLE:
                if (shootCommand == ShootCommand.SHOOT_ONE || shootCommand == ShootCommand.SHOOT_TWO || shootCommand == ShootCommand.SHOOT_THREE) {
                    // shoot 1 artifact
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_THIRD_SHOT_TO_COMPLETE; // THREE_SHOT refers to the position of the last artifact being shot, not the number being shot.
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                break;

             // 0 artifacts left in the sorter
            case ZERO_ARTIFACT_LEFT_123_SHOOTING_CYCLE:
                if (sorterMotor.isMovementComplete()) {
                    // prepare to intake
                    rampServo.downPosition();
                    sorterMotor.resetEncoder();
                    intakeOn();
                    colorSensorController.colorSensorsOn();
                }
                currentState = SorterState.WAITING_ARTIFACT_123;
                break;
        }
    }

    @Override
    public String getName() {
        return SUB_SYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        sorterMotor.resetEncoder();
        colorSensorController.colorSensorsOn();
        return true;
    }
}
