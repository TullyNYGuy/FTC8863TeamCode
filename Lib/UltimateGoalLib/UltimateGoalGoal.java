package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;


//         top = new UltimateGoalGoal(35.5 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));
//         powerShotLeft = new UltimateGoalGoal(30 * 0.0254, new Pose2d(71 * 0.0254, 20 * 0.0254, 0));
//         powerShotMid = new UltimateGoalGoal(30 * 0.0254, new Pose2d(71 * 0.0254, 12 * 0.0254, 0));
//         powerShotRight = new UltimateGoalGoal(30 * 0.0254, new Pose2d(71 * 0.0254, 4.25 * 0.0254, 0));
//         middle = new UltimateGoalGoal(26 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));
//         bottom = new UltimateGoalGoal(16.5 * 0.0254, new Pose2d(71 * 0.0254, -12 * 0.0254, 0));

public interface UltimateGoalGoal {

    /**
     * Returns the goal height in meters.
     * @return
     */
    public double getHeight();

    /**
     * returns the goal position in (x (inches), y (inches), heading (radians))
     * @return
     */
    public Pose2d getPosition();
}
