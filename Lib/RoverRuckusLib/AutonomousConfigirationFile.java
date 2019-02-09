package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AutonomousConfigirationFile {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum HangLocation {
        CRATER_SIDE,
        DEPOT_SIDE,
        DONT_HANG
    }

    public enum Sample {
        CRATER_SIDE,
        DEPOT_SIDE,
        NO_SAMPLE,
        BOTH
    }

    public enum ParkLocation {
        OUR_CRATER,
        OTHER_CRATER,
        DEPOT
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private double delay = 0;
    private HangLocation hangLocation = HangLocation.DONT_HANG;
    private Sample sample = Sample.NO_SAMPLE;
    private boolean claimDepot = true;
    private ParkLocation parkLocation = ParkLocation.OUR_CRATER;
    private String folderPath = "/sdcard/FTC8863/";
    private String filename = "autonomousConfigirationFile.txt";

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************
    public void setDelay(double delay) {
        if (delay > 30) {
            delay = 30;
        }
        this.delay = delay;
    }

    public double getDelay() {
        return delay;
    }

    public HangLocation getHangLocation() {
        return hangLocation;
    }

    public void setHangLocation(HangLocation hangLocation) {
        this.hangLocation = hangLocation;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public boolean isClaimDepot() {
        return claimDepot;
    }

    public void setClaimDepot(boolean claimDepot) {
        this.claimDepot = claimDepot;
    }

    public ParkLocation getParkLocation() {
        return parkLocation;
    }

    public void setParkLocation(ParkLocation parkLocation) {
        this.parkLocation = parkLocation;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public AutonomousConfigirationFile() {

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
    public void writeConfigirationFile() {


        File file = new File(folderPath + filename);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write("hang " + hangLocation.toString());
            fileWriter.write("delay " + delay);
            fileWriter.write("sample " + sample.toString());
            fileWriter.write("claimDepot " + claimDepot);
            fileWriter.write("parkLocation " + parkLocation.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close resources
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void readConfigurationFile() {
        File file = new File(folderPath + filename);
        Scanner scanner=null;
        try{
            scanner = new Scanner(file);
            while (scanner.hasNextLine()){
                String key=scanner.next();
                switch (key){
                    case "hang":
                        String value=scanner.next();
                        switch(value){
                            case "CRATER_SIDE":
                                hangLocation=HangLocation.CRATER_SIDE;
                                break;
                            case "DEPOT_SIDE":
                                hangLocation=HangLocation.DEPOT_SIDE;
                                break;
                            case "DONT_HANG":
                                hangLocation=HangLocation.DONT_HANG;
                                break;
                        }
                        break;
                    case "delay":
                        delay=scanner.nextDouble();
                        break;
                    case "sample":
                        switch(scanner.next()){
                            case "CRATER_SIDE":
                                sample=Sample.CRATER_SIDE;
                                break;
                            case "DEPOT_SIDE":
                                sample=Sample.DEPOT_SIDE;
                                break;
                            case "NO_SAMPLE":
                                sample=Sample.NO_SAMPLE;
                                break;
                            case "BOTH":
                                sample=Sample.BOTH;
                                break;
                        }
                        break;
                    case "claimDepot":
                        claimDepot=scanner.nextBoolean();
                        break;
                    case "parkLocation":
                        switch(scanner.next()){
                            case "OUR_CRATER":
                                parkLocation=ParkLocation.OUR_CRATER;
                                break;
                            case "OTHER_CRATER":
                                parkLocation=ParkLocation.OTHER_CRATER;
                                break;
                            case "DEPOT":
                                parkLocation=ParkLocation.DEPOT;
                                break;
                        }
                        break;
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            //close resources
            scanner.close();

            }
    }
}

