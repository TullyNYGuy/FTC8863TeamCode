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
        DELAY
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
    public AutonomousDirector(AutonomousConfigurationFile conFigFile){
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
    public AutonomousTasks getNextTask(){
        if(iterator.hasNext()){
            return iterator.next();
        } else{
            return AutonomousTasks.STOP;
        }
    }
    private void translator(){
        if (conFigFile.getSample()== AutonomousConfigurationFile.Sample.CRATER_SIDE || conFigFile.getSample() == AutonomousConfigurationFile.Sample.DEPOT_SIDE || conFigFile.getSample() == AutonomousConfigurationFile.Sample.BOTH){
           taskList.add(AutonomousTasks.LOCATE_GOLD_MINERAL);
        }
        if (conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.CRATER_SIDE || conFigFile.getHangLocation() == AutonomousConfigurationFile.HangLocation.DEPOT_SIDE){
            taskList.add(AutonomousTasks.DEHANG);
        }
        if (conFigFile.getDelay() != 0){
            taskList.add(AutonomousTasks.DELAY);
            delay = conFigFile.getDelay();
        }
    }

}

