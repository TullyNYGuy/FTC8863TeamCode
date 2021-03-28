package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class UltimateGoalIntakeController {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum States {
        IDLE,
        NO_RING,
        ONE_RING,
        TWO_RING,
        THREE_RING;
    }

    private enum Commands {
        ESTOP,
        OFF,
        INTAKE,
        FIRE_1,
        FIRE_2,
        FIRE_3;
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private States currentState = States.IDLE;
    private Commands currentCommand = Commands.OFF;
    public UltimateGoalIntake intake;
    private boolean commandComplete= false;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public UltimateGoalIntakeController(HardwareMap hardwareMap, Telemetry telemetry, UltimateGoalIntake intake) {
        this.intake = intake;
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

    public void update() {
        switch (currentState) {
            case IDLE:
                switch (currentCommand) {
                    case ESTOP:
                        //already idle
                        break;
                    case OFF:
                        //already idle
                        break;
                    case INTAKE:
                    case FIRE_1:
                    case FIRE_2:
                    case FIRE_3:
                        switch (intake.whereAreRings()) {
                            case NO_RINGS:
                                currentState= States.NO_RING;
                                break;
                            case THREE:
                                currentState= States.ONE_RING;
                                break;
                            case TWO_THREE:
                                currentState= States.TWO_RING;
                                break;
                            case ONE_TWO_THREE:
                                currentState= States.THREE_RING;
                                break;
                            case TWO:
                                currentState= States.ONE_RING;
                                break;
                            case ONE:
                                currentState= States.ONE_RING;
                                break;
                            case ONE_TWO:
                                currentState= States.TWO_RING;
                                break;
                            case ONE_THREE:
                                currentState= States.TWO_RING;
                                break;
                        }
                        break;
                }
                break;
            case NO_RING:
                switch (currentCommand) {
                    case ESTOP:
                        intake.requestTurnIntakeOFF();
                        break;
                    case OFF:
                        if (intake.whereAreRings()== UltimateGoalIntake.RingsAt.NO_RINGS) {
                            intake.requestTurnIntakeOFF();
                        commandComplete=true;
                        currentState= States.IDLE;
                        }
                        break;
                    case INTAKE:
                        switch (intake.whereAreRings()) {
                            case NO_RINGS:
                                intake.requestTurnStage123On();
                                currentState= States.NO_RING;
                                commandComplete= true;
                                break;
                            case THREE:
                                //STOPPED HERE. we need to handle turning off stage 3.
                                currentState= States.ONE_RING;
                                break;
                            case TWO_THREE:
                                currentState= States.TWO_RING;
                                break;
                            case ONE_TWO_THREE:
                                currentState= States.THREE_RING;
                                break;
                            case TWO:
                                currentState= States.ONE_RING;
                                break;
                            case ONE:
                                currentState= States.ONE_RING;
                                break;
                            case ONE_TWO:
                                currentState= States.TWO_RING;
                                break;
                            case ONE_THREE:
                                currentState= States.TWO_RING;
                                break;
                        }
                        break;
                    case FIRE_1:
                        break;
                    case FIRE_2:
                        break;
                    case FIRE_3:
                        break;
                }
                break;
            case ONE_RING:
                switch (currentCommand) {
                    case ESTOP:
                        intake.requestTurnIntakeOFF();
                        break;
                    case OFF:
                        break;
                    case INTAKE:
                        break;
                    case FIRE_1:
                        break;
                    case FIRE_2:
                        break;
                    case FIRE_3:
                        break;
                }
                break;
            case TWO_RING:
                switch (currentCommand) {
                    case ESTOP:
                        intake.requestTurnIntakeOFF();
                        break;
                    case OFF:
                        break;
                    case INTAKE:
                        break;
                    case FIRE_1:
                        break;
                    case FIRE_2:
                        break;
                    case FIRE_3:
                        break;
                }
                break;
            case THREE_RING:
                switch (currentCommand) {
                    case ESTOP:
                        intake.requestTurnIntakeOFF();
                        break;
                    case OFF:
                        break;
                    case INTAKE:
                        break;
                    case FIRE_1:
                        break;
                    case FIRE_2:
                        break;
                    case FIRE_3:
                        break;
                }
                break;
        }
    }
}
