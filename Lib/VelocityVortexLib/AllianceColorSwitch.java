package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

public class AllianceColorSwitch {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum AllianceColor {
        BLUE,
        RED,
        NONE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Switch redSwitch;
    private Switch blueSwitch;
    Telemetry telemetry;

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
    public AllianceColorSwitch(HardwareMap hardwareMap, Telemetry telemetry) {
        redSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getRedAllianceSwitchName(), Switch.SwitchType.NORMALLY_CLOSED);
        blueSwitch = new Switch(hardwareMap, RobotConfigMappingForGenericTest.getBlueAllianceSwitchName(), Switch.SwitchType.NORMALLY_CLOSED);
        this.telemetry = telemetry;
        telemetry.addData("Color Switches Initialized", "!");
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
    public AllianceColor getAllianceColor() {
        AllianceColor result = AllianceColor.BLUE;
        if (redSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE) && blueSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE)) {
            telemetry.addData ("Both alliance switches are set", "!!");
        }
        if (!redSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE) && !blueSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE)) {
            telemetry.addData("None of the alliance switches are set", "!!");
        }
        if (redSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE)) {
            result = AllianceColor.RED;
    }
        if (blueSwitch.isPressed(Switch.Debounce.NO_DEBOUNCE)) {
            result = AllianceColor.BLUE;
        }
    return result;
    }

    public void displayAllianceSwitch(Telemetry telemetry) {
        telemetry.addData("Alliance = ", getAllianceColor().toString());
    }
}
