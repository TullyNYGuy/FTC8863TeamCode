package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;

@Config
public class AutonomousTestDeliveryStateMachine implements AutonomousStateMachineFreightFrenzy {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        WAITING_FOR_EXTEND_COMPLETE,
        WAITING_FOR_DUMP_COMPLETE,
        WAITING_FOR_RETRACTION_COMPLETE,
        COMPLETE
    }
    private States currentState;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private FreightFrenzyRobotRoadRunner robot;
    private boolean isComplete = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public String getCurrentState(){
        return currentState.toString();
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AutonomousTestDeliveryStateMachine(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
        this.robot = robot;
        currentState = States.IDLE;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Place all of the trajectories for the autonomous opmode in this method. This method gets
     * called from the constructor so that the trajectories are created when the autonomous object
     * is created.
     */
    @Override
    public void createTrajectories() {
    }

        //*********************************************************************************************
        //          MAJOR METHODS
        //
        // public methods that give the class its functionality
        //*********************************************************************************************

        @Override
        public void start () {
        // SET THE LEVEL OF the shipping hub to dump into
        robot.freightSystem.setTop();
            currentState = States.START;
            isComplete = false;
        }

        @Override
        public void update () {
            switch (currentState) {

                case IDLE:
                    break;

                case START:
                    isComplete = false;
                    robot.freightSystem.extend();
                    currentState = States.WAITING_FOR_EXTEND_COMPLETE;
                    break;

                case WAITING_FOR_EXTEND_COMPLETE:
                    if (robot.freightSystem.isReadyToDump()) {
                        robot.freightSystem.dump();
                        currentState = States.WAITING_FOR_DUMP_COMPLETE;
                    }
                    break;

                case WAITING_FOR_DUMP_COMPLETE:
                    if (robot.freightSystem.isDumpComplete()) {
                        currentState = States.WAITING_FOR_RETRACTION_COMPLETE;
                    }
                    break;

                case WAITING_FOR_RETRACTION_COMPLETE:
                    if (robot.freightSystem.isRetractionComplete()){
                        currentState = States.COMPLETE;
                    }
                    break;
                    
                case COMPLETE:
                    isComplete = true;
                    break;
            }
        }
    }
