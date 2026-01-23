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
        ONE_ARTIFACT_312_INTAKE,
        WAITING_FOR_240_INTAKE,
        TWO_ARTIFACT_231_INTAKE,
        WAITING_FOR_360_INTAKE,
        THREE_ARTIFACT_231_INTAKE,
        WAITING_FOR_RAMP_UP,
        THREE_ARTIFACT_123_PRESHOOT,
        WAITING_FOR_FIRST_SHOT,
        TWO_ARTIFACT_312_SHOOTING_CYCLE,
        WAITING_FOR_SECOND_SHOT,
        ONE_ARTIFACT_231_SHOOTING_CYCLE,
        WAITING_FOR_THIRD_SHOT,
        ZERO_ARTIFACT_123_SHOOTING_CYCLE;
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
                                 DecodeRampServo decodeRampServo) {
        this.colorSensorController = decodeColorSensorController;
        this.sorterMotor = sorterMotor;
        this.rampServo = decodeRampServo;
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
    public void shootOne() {
        shootCommand = ShootCommand.SHOOT_ONE;
    }

    public void shootTwo() {
        shootCommand = ShootCommand.SHOOT_TWO;
    }

    public void shootThree() {
        shootCommand = ShootCommand.SHOOT_THREE;
    }

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    @Override
    public void update() {
        switch (currentState) {

            case WAITING_ARTIFACT_123:
                if (colorSensorController.isArtifactPresent()) {
                    sorterMotor.moveToPosition(120);
                    currentState = SorterState.WAITING_FOR_120_INTAKE;
                }
                break;

            case WAITING_FOR_120_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.ONE_ARTIFACT_312_INTAKE;
                }
                break;

            case ONE_ARTIFACT_312_INTAKE:
                if (colorSensorController.isArtifactPresent()) {
                    sorterMotor.moveToPosition(240);
                    currentState = SorterState.WAITING_FOR_240_INTAKE;
                }
                break;

            case WAITING_FOR_240_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.TWO_ARTIFACT_231_INTAKE;
                }
                break;

            case TWO_ARTIFACT_231_INTAKE:
                if (colorSensorController.isArtifactPresent()) {
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_360_INTAKE;
                }
                break;

            case WAITING_FOR_360_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    sorterMotor.resetEncoder();
                    rampServo.upPosition();
                    currentState = SorterState.WAITING_FOR_RAMP_UP;
                }
                break;

            case THREE_ARTIFACT_231_INTAKE:
                break;

            case WAITING_FOR_RAMP_UP:
                if (rampServo.isPositionReached()) {
                    currentState = SorterState.THREE_ARTIFACT_123_PRESHOOT;
                }
                break;

            case THREE_ARTIFACT_123_PRESHOOT:
                if (shootCommand == ShootCommand.SHOOT_ONE) {
                    sorterMotor.moveToPosition(120);
                    currentState = SorterState.WAITING_FOR_FIRST_SHOT;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                if (shootCommand == ShootCommand.SHOOT_TWO) {
                    sorterMotor.moveToPosition(240);
                    currentState = SorterState.WAITING_FOR_SECOND_SHOT;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                if (shootCommand == ShootCommand.SHOOT_THREE) {
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_THIRD_SHOT;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                break;

            case WAITING_FOR_FIRST_SHOT:
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.TWO_ARTIFACT_312_SHOOTING_CYCLE;
                }
                break;

            case TWO_ARTIFACT_312_SHOOTING_CYCLE:
                if (shootCommand == ShootCommand.SHOOT_ONE) {
                    sorterMotor.moveToPosition(240);
                    currentState = SorterState.WAITING_FOR_SECOND_SHOT;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                if (shootCommand == ShootCommand.SHOOT_TWO || shootCommand == ShootCommand.SHOOT_THREE) {
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_THIRD_SHOT;
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                break;

            case WAITING_FOR_SECOND_SHOT:
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.ONE_ARTIFACT_231_SHOOTING_CYCLE;
                }
                break;

            case WAITING_FOR_THIRD_SHOT:
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.ZERO_ARTIFACT_123_SHOOTING_CYCLE;
                }
                break;

            case ONE_ARTIFACT_231_SHOOTING_CYCLE:
                if (shootCommand == ShootCommand.SHOOT_ONE || shootCommand == ShootCommand.SHOOT_TWO || shootCommand == ShootCommand.SHOOT_THREE) {
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_THIRD_SHOT; // THREE_SHOT refers to the position of the last artifact being shot, not the number being shot.
                    shootCommand = ShootCommand.SHOOT_NONE;
                }
                break;

            case ZERO_ARTIFACT_123_SHOOTING_CYCLE:
                if (sorterMotor.isMovementComplete()) {
                    rampServo.downPosition();
                    sorterMotor.resetEncoder();
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
        return true;
    }
}
