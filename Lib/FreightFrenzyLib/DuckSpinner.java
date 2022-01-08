package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import com.qualcomm.robotcore.hardware.CRServo;

public class DuckSpinner {
    private CRServo duckSpinner;
     void DuckSpinner(CRServo duckSpinner, FreightFrenzyRobot.HardwareName Name){
        this.duckSpinner = duckSpinner;

    }
    //duckSpinner = new CRServo();

    public void TurnOn() {
        duckSpinner.setPower(1);
    }
}
