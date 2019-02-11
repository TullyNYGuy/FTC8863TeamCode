package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import java.util.ArrayList;
import java.util.Iterator;

public class AutonomousDirector {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum AutonomousTasks {
        STOP,
        LOCATE_GOLD_MINERAL,
        DEHANG,
        DELAY,
        HIT_GOLD_MINERAL_FROM_FLOOR,
        HIT_GOLD_MINERAL_FROM_LANDER,
        CLAIM_DEPOT_FROM_CRATER_SIDE_MINERALS,
        CLAIM_DEPOT_FROM_DEPOT_SIDE_MINERALS,
        CLAIM_DEPOT_FROM_CRATER_SIDE_LANDER,
        CLAIM_DEPOT_FROM_DEPOT_SIDE_LANDER,
        PARK_IN_OUR_CRATER_FROM_CRATER_SIDE_LANDER,
        PARK_IN_OUR_CRATER_FROM_DEPOT_SIDE_LANDER,
        PARK_IN_OUR_CRATER_FROM_CRATER_SIDE_MINERALS,
        PARK_IN_OTHER_CRATER_FROM_CRATER_SIDE_MINERALS,
        PARK_IN_OUR_CRATER_FROM_DEPOT_SIDE_MINERALS,
        PARK_IN_OTHER_CRATER_FROM_DEPOT_SIDE_MINERALS
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private ArrayList<AutonomousTasks> taskList;
    private Iterator<AutonomousTasks> iterator;
    private AutonomousConfigurationFile conFigFile;
    private double delay = 0;
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
    public AutonomousDirector(AutonomousConfigurationFile conFigFile) {
        this.conFigFile = conFigFile;
        taskList = new ArrayList<AutonomousTasks>();
        iterator = taskList.iterator();
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
    public AutonomousTasks getNextTask() {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return AutonomousTasks.STOP;
        }
    }

    private boolean isSampling() {
        return conFigFile.getSample() == AutonomousConfigurationFile.Sample.CRATER_SIDE || conFigFile.getSample() == AutonomousConfigurationFile.Sample.DEPOT_SIDE || conFigFile.getSample() == AutonomousConfigurationFile.Sample.BOTH;
    }

    private void translator() {
        // if we are sampling, find the location of the gold mineral while the robot is hanging
        if (conFigFile.getSample() == AutonomousConfigurationFile.Sample.CRATER_SIDE || conFigFile.getSample() == AutonomousConfigurationFile.Sample.DEPOT_SIDE || conFigFile.getSample() == AutonomousConfigurationFile.Sample.BOTH) {
            taskList.add(AutonomousTasks.LOCATE_GOLD_MINERAL);
        }
        // if the robot is hanging, dehang
        if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE || conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.DEPOT_SIDE) {
            taskList.add(AutonomousTasks.DEHANG);
        }
        // the robot has landed, run the delay if there is one
        if (conFigFile.getDelay() != 0) {
            taskList.add(AutonomousTasks.DELAY);
            delay = conFigFile.getDelay();
        }
        // the robot has landed and any delay has been run, is the robot supposed to sample?
        if (isSampling()) {
            // The robot has just landed so the we are assuming that there is at least one sample and it is on our side of the lander.
            // The movements to the minerals are the same for both the crater and depot side of the lander, but the movements away from the mineral are different between
            // the crater side and the depot side so this is not going to work, unless we separate out the movements to
            // get to the minerals from the movements to move away from the minerals
            taskList.add(AutonomousTasks.HIT_GOLD_MINERAL_FROM_LANDER);
            // ok now the first sample is completed
            if (conFigFile.isClaimDepot()) {
                // The robot is supposed to claim the depot. The movements depend on which side of the lander the robot is located on.
                if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.DEPOT_SIDE) {
                    taskList.add(AutonomousTasks.CLAIM_DEPOT_FROM_DEPOT_SIDE_MINERALS);
                }
                if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE) {
                    taskList.add(AutonomousTasks.CLAIM_DEPOT_FROM_CRATER_SIDE_MINERALS);
                }
                // now that the robot has claimed the depot, what are we supposed to do next? Possibilities:
                // sample the other side of the lander's minerals
                //    crater side of the lander minerals from the depot
                //    depot side of the lander minerals from the depot
                //        now that the robot has sampled the second gold mineral, is it supposed to park?
                //        If yes, then the parking depends on where the robot current is located, and where
                //        it is supposed to park
                // There is no second sample after claiming the depot, just park
                //    in other crater
                //    in our crater
                //    in the depot
            }
            // not claiming depot and just parking from the location of the minerals
            if (!conFigFile.isClaimDepot()) {
                // is the robot on the crater side?
                if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE) {
                    // park in our crater from the crater side minerals
                    if (conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OUR_CRATER) {
                        taskList.add(AutonomousTasks.PARK_IN_OUR_CRATER_FROM_CRATER_SIDE_MINERALS);
                    }
                    // part in the other crater from the cater side minerals
                    if (conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OTHER_CRATER) {
                        taskList.add(AutonomousTasks.PARK_IN_OTHER_CRATER_FROM_CRATER_SIDE_MINERALS);
                    }
                }
                // is the robot on the depot side?
                if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.DEPOT_SIDE) {
                    // park in our crater from the depot side minerals
                    if (conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OUR_CRATER) {
                        taskList.add(AutonomousTasks.PARK_IN_OUR_CRATER_FROM_DEPOT_SIDE_MINERALS);
                    }
                    // part in the other crater from the depot side minerals
                    if (conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OTHER_CRATER) {
                        taskList.add(AutonomousTasks.PARK_IN_OTHER_CRATER_FROM_DEPOT_SIDE_MINERALS);
                    }
                }
            }
        }
        // not sampling
        // this needs to be like the sampling logic. The logic works down the possibilities in order.
        if (!isSampling() && conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OUR_CRATER && !conFigFile.isClaimDepot()) {
            if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE) {
                taskList.add(AutonomousTasks.PARK_IN_OUR_CRATER_FROM_CRATER_SIDE_LANDER);
            } else {
                taskList.add(AutonomousTasks.PARK_IN_OUR_CRATER_FROM_DEPOT_SIDE_LANDER);
            }
        }

        if (!isSampling() && conFigFile.isClaimDepot()) {
            if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE) {
                taskList.add(AutonomousTasks.CLAIM_DEPOT_FROM_CRATER_SIDE_LANDER);
            } else {
                taskList.add(AutonomousTasks.CLAIM_DEPOT_FROM_DEPOT_SIDE_LANDER);
            }
        }
    }
}

