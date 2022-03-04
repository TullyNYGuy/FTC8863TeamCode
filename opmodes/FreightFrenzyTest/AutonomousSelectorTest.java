package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.ListSelector;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousStateMachineFreightFrenzy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Autonomous Selector Test", group = "Test")
@Disabled
public class AutonomousSelectorTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();





        // Put your initializations here
        // create the robot and run the init for it

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);
        Map<String, AutonomousStateMachineFreightFrenzy> stateMachines = new HashMap<>();
        stateMachines.put("WarehouseDuckParkDepot", null);
        stateMachines.put("WarehouseDuckParkShipping", null);
        stateMachines.put("WarehouseDuckNoPark", null);
        ListSelector selector = new ListSelector(telemetry, gamepad1, new ArrayList<String>(stateMachines.keySet()));
        String selected = selector.getSelection();

        telemetry.addData("Selected:", selected);
        telemetry.update();
        // Wait for the start button
        waitForStart();
        // Put your calls here - they will not run in a loop


        while (opModeIsActive()) {
            idle();
            //autonomous.update();
        }

        // save the pose so we can use it to start out in teleop


        // save the shooter angle so we can use it later in teleop
        // the angle changer knows how to do this. My opinion is that you should not be down in these
        // details
        //PersistantStorage.setMotorTicks(robot.shooter.getMotorTicks());


        telemetry.addData(">", "Done");
        telemetry.update();
    }

    public void enableBulkReads(HardwareMap hardwareMap, LynxModule.BulkCachingMode mode) {
        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(mode);
        }
    }
}
