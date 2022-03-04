package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

public class ListSelector {
    private Telemetry telemetry;
    private Gamepad gamepad;
    private List<String> list;
    private boolean dpad_up_pressed = false;
    private boolean dpad_down_pressed = false;
    private boolean a_pressed = false;
    private boolean up_event = false;
    private boolean down_event = false;
    private boolean select_event = false;


    public ListSelector(Telemetry telemetry, Gamepad gamepad, List<String> list) {
        this.gamepad = gamepad;
        this.list = list;
        this.telemetry = telemetry;
    }

    private void processEvents() {
        boolean val = gamepad.dpad_up;
        up_event = val && !dpad_up_pressed;
        dpad_up_pressed = val;
        val = gamepad.dpad_down;
        down_event = val && !dpad_down_pressed;
        dpad_down_pressed = val;
        val = gamepad.a;
        select_event = val && !a_pressed;
        a_pressed = val;
    }

    public String getSelection() {
        if(list.size() == 0)
            return null;
        int currentSelection = 0;
        do {
            telemetry.addLine("Choose option - (DPad U/D. A - select)");
            telemetry.addLine(list.get(currentSelection));
            telemetry.update();
            processEvents();
            if (up_event && currentSelection > 0)
                currentSelection--;
            if(down_event && currentSelection < list.size() - 1)
                currentSelection++;
            Thread.yield();
        } while (!select_event);
        return list.get(currentSelection);
    }

}
