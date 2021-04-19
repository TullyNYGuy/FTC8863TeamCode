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

    /**
     * Handling an intake command is the same no matter what state the intake is in, with one minor
     * difference in the NO_RING state.
     * @param currentState
     */
    private void handleIntakeCommand (States currentState) {
        switch (intake.whereAreRings()) {
            case NO_RINGS:
                intake.requestTurnStage123On();
                currentState = States.NO_RING;
                if (currentState== States.NO_RING) {
                    commandComplete = true;
                }
                else {
                    commandComplete = false;
                }
                break;
            case THREE:
                intake. requestTurnStage12On();
                commandComplete= true;
                currentState= States.ONE_RING;
                break;
            case TWO_THREE:
                intake.requestTurnStage1On();
                commandComplete= true;
                currentState= States.TWO_RING;
                break;
            case ONE_TWO_THREE:
                intake.requestTurnIntakeOFF();
                commandComplete=true;
                currentState= States.THREE_RING;
                break;
            case TWO:
                intake.requestTurnStage123On();
                commandComplete=false;
                currentState= States.ONE_RING;
                break;
            case ONE:
                intake.requestTurnStage123On();
                commandComplete= false;
                currentState= States.ONE_RING;
                break;
            case ONE_TWO:
                intake.requestTurnStage123On();
                commandComplete= false;
                currentState= States.TWO_RING;
                break;
            case ONE_THREE:
                intake.requestTurnStage12On();
                commandComplete= false;
                currentState= States.TWO_RING;
                break;
        }
    }
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
                        handleIntakeCommand(currentState);
                        break;
                    case FIRE_1:
                    case FIRE_2:
                    case FIRE_3:
                        intake.requestTurnIntakeOFF();
                        commandComplete= true;
                        currentState= States.IDLE;
                        break;
                }
                break;
            case ONE_RING:
                switch (currentCommand) {
                    case ESTOP:
                        intake.requestTurnIntakeOFF();
                        break;
                    case OFF:
                        intake.requestTurnIntakeOFF();
                        commandComplete=true;
                        currentState= States.IDLE;
                        break;
                    case INTAKE:
                        handleIntakeCommand(currentState);
                        break;
                    case FIRE_1:
                    case FIRE_2:
                    case FIRE_3:
                        switch (intake.whereAreRings()) {
                            case THREE:
                                intake.requestTurnStage3On();
                                commandComplete=false;
                                currentState= States.ONE_RING;
                                break;
                            case NO_RINGS:
                                intake.requestTurnIntakeOFF();
                                commandComplete= true;
                                currentState= States.IDLE;
                                break;
                             //no other cases matter
                        }
                        break;
                }
                break;
            case TWO_RING:
                switch (currentCommand) {
                    case ESTOP:
                        intake.requestTurnIntakeOFF();
                        break;
                    case OFF:
                        intake.requestTurnIntakeOFF();
                        commandComplete=true;
                        currentState= States.IDLE;
                        break;
                    case INTAKE:
                        handleIntakeCommand(currentState);
                        break;
                    case FIRE_1:
                        switch (intake.whereAreRings()) {
                            case TWO_THREE:
                                intake.requestTurnStage23On();
                                currentState=States.TWO_RING;
                                commandComplete= false;
                                break;
                            case TWO:
                                intake.requestTurnStage23On();
                                currentState=States.TWO_RING;
                                commandComplete= false;
                                break;
                            case NO_RINGS:
                                intake.requestTurnStage23On();
                                currentState=States.TWO_RING;
                                commandComplete= false;
                                break;
                            case THREE:
                                intake.requestTurnIntakeOFF();
                                commandComplete= true;
                                currentState= States.IDLE;
                                break;
                        }
                        break;
                    case FIRE_2:
                    case FIRE_3:
                        switch (intake.whereAreRings()) {
                            case TWO_THREE:
                                intake.requestTurnStage23On();
                                commandComplete= false;
                                currentState= States.TWO_RING;
                                break;
                            case TWO:
                                intake.requestTurnStage23On();
                                commandComplete= false;
                                currentState= States.TWO_RING;
                                break;
                            case NO_RINGS:
                                if (intake.getNumberOfRingsAtStage3()==1){
                                    intake.requestTurnStage23On();
                                    commandComplete= false;
                                    currentState=States.TWO_RING;
                            }
                                if (intake.getNumberOfRingsAtStage3()==2){
                                    intake.requestTurnIntakeOFF();
                                    commandComplete= true;
                                    currentState= States.IDLE;
                                }
                                break;
                            case THREE:
                                intake.requestTurnStage3On();
                                commandComplete= false;
                                currentState= States.TWO_RING;
                                break;
                        }
                        break;
                }
                break;
            case THREE_RING:
                switch (currentCommand) {
                    case ESTOP:
                        intake.requestTurnIntakeOFF();
                        break;
                    case OFF:
                        intake.requestTurnIntakeOFF();
                        commandComplete=true;
                        currentState= States.IDLE;
                        break;
                    case INTAKE:
                        handleIntakeCommand(currentState);
                        break;
                    case FIRE_1:
                        switch (intake.whereAreRings()) {
                            case ONE_TWO_THREE:
                                intake.requestTurnStage123On();
                                commandComplete= false;
                                currentState= States.THREE_RING;
                                break;
                            case TWO_THREE:
                                intake.requestTurnIntakeOFF();
                                commandComplete= true;
                                currentState= States.TWO_RING;
                                break;
                            case THREE:
                                intake.requestTurnStage12On();
                                commandComplete=false;
                                currentState=States.THREE_RING;
                                break;
                            case NO_RINGS:
                            case TWO:
                            case ONE_THREE:
                            case ONE_TWO:
                            case ONE:
                                intake.requestTurnStage123On();
                                currentState= States.THREE_RING;
                                commandComplete= false;
                                break;
                        }
                        break;
                    case FIRE_2:
                        switch (intake.whereAreRings()) {
                            case ONE_TWO_THREE:
                                intake.requestTurnStage123On();
                                commandComplete= false;
                                currentState= States.THREE_RING;
                                break;
                            case TWO_THREE:
                                intake.requestTurnStage23On();
                                commandComplete= false;
                                currentState= States.THREE_RING;
                                break;
                            case THREE:
                                intake.requestTurnIntakeOFF();
                                commandComplete= true;
                                currentState= States.ONE_RING;
                                break;
                            case NO_RINGS:
                            case TWO:
                            case ONE_THREE:
                            case ONE_TWO:
                            case ONE:
                                intake.requestTurnStage123On();
                                currentState= States.THREE_RING;
                                commandComplete= false;
                                break;
                        }
                        break;
                    case FIRE_3:
                        switch (intake.whereAreRings()) {
                            case ONE_TWO_THREE:
                                intake.requestTurnStage123On();
                                commandComplete= false;
                                currentState= States.THREE_RING;
                                break;
                            case NO_RINGS:
                                if (intake.getNumberOfRingsAtStage3()==1 || intake.getNumberOfRingsAtStage3()== 2) {
                                    intake.requestTurnStage123On();
                                    commandComplete= false;
                                    currentState= States.THREE_RING;
                                }
                                if (intake.getNumberOfRingsAtStage3()==3){
                                    intake.requestTurnIntakeOFF();
                                    commandComplete= true;
                                    currentState= States.IDLE;
                                }
                                break;
                            case THREE:
                            case TWO_THREE:
                            case ONE:
                            case TWO:
                            case ONE_TWO:
                            case ONE_THREE:
                                intake.requestTurnStage123On();
                                currentState= States.THREE_RING;
                                commandComplete= false;
                                break;

                        }
                        break;
                }
                break;
        }
    }
}
