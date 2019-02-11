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
        LOCATE_GOLD_MINERAL,
        DEHANG,
        GO_TO_OUR_CRATER_FROM_CRATERSIDE_LANDER,
        STOP,
        DELAY,
        HIT_GOLD_MINERAL_FROM_FLOOR,
        HIT_GOLD_MINERAL_FROM_LANDER,
        CLAIM_DEPOT_FROM_CRATORSIDE_MINERALS,
        CLAIM_DEPOT_FROM_DEPOTSIDE_MINERALS,
        CLAIM_DEPOT_FROM_CRATORSIDE_LANDER,
        CLAIM_DEPOT_FROM_DEPOTSIDE_LANDER,
        PARK_IN_OUR_CRATER_FROM_CRATERSIDE_LANDER,
        PARK_IN_OUR_CRATER_FROM_DEPOTSIDE_LANDER,
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
        if (conFigFile.getSample() == AutonomousConfigurationFile.Sample.CRATER_SIDE || conFigFile.getSample() == AutonomousConfigurationFile.Sample.DEPOT_SIDE || conFigFile.getSample() == AutonomousConfigurationFile.Sample.BOTH) {
            taskList.add(AutonomousTasks.LOCATE_GOLD_MINERAL);
        }
        if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE || conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.DEPOT_SIDE) {
            taskList.add(AutonomousTasks.DEHANG);
        }
        if (conFigFile.getDelay() != 0) {
            taskList.add(AutonomousTasks.DELAY);
            delay = conFigFile.getDelay();
        }

        if (isSampling()) {
            taskList.add(AutonomousTasks.HIT_GOLD_MINERAL_FROM_LANDER);
            if (conFigFile.isClaimDepot()) {
                if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.DEPOT_SIDE) {
                    taskList.add(AutonomousTasks.CLAIM_DEPOT_FROM_DEPOTSIDE_MINERALS);
                }
                if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE) {
                    taskList.add(AutonomousTasks.CLAIM_DEPOT_FROM_CRATORSIDE_MINERALS);
                }
            }
            if (!conFigFile.isClaimDepot()) {
                if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE) {
                    if (conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OUR_CRATER) {
                        taskList.add(AutonomousTasks.PARK_IN_OUR_CRATER_FROM_CRATER_SIDE_MINERALS);
                    }
                    if (conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OTHER_CRATER) {
                        taskList.add(AutonomousTasks.PARK_IN_OTHER_CRATER_FROM_CRATER_SIDE_MINERALS);
                    }
                }
                if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.DEPOT_SIDE) {
                    if (conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OUR_CRATER) {
                        taskList.add(AutonomousTasks.PARK_IN_OUR_CRATER_FROM_DEPOT_SIDE_MINERALS);
                    }
                    if (conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OTHER_CRATER) {
                        taskList.add(AutonomousTasks.PARK_IN_OTHER_CRATER_FROM_DEPOT_SIDE_MINERALS);
                    }
                }
            }
        }

        if (!isSampling() && conFigFile.getParkLocation() == AutonomousConfigurationFile.ParkLocation.OUR_CRATER && !conFigFile.isClaimDepot()) {
            if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE) {
                taskList.add(AutonomousTasks.PARK_IN_OUR_CRATER_FROM_CRATERSIDE_LANDER);
            } else {
                taskList.add(AutonomousTasks.PARK_IN_OUR_CRATER_FROM_DEPOTSIDE_LANDER);
            }
        }

        if (!isSampling() && conFigFile.isClaimDepot()) {
            if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE) {
                taskList.add(AutonomousTasks.CLAIM_DEPOT_FROM_CRATORSIDE_LANDER);
            } else {
                taskList.add(AutonomousTasks.CLAIM_DEPOT_FROM_DEPOTSIDE_LANDER);
            }
        }
    }
}

