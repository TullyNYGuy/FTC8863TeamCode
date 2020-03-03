package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.ArrayList;

public class PurePursuit {

    public static class ResultPosition {
        boolean reached;
        RobotPosition pos;

        public ResultPosition(RobotPosition pos, boolean reached) {
            this.pos = pos;
            this.reached = reached;
        }
    }

    public double lookAheadDistance;
    ArrayList<RobotPosition> waypoints;
    int currentItem;

    public PurePursuit(double lookAheadDistance, ArrayList<RobotPosition> waypoints) {
        this.lookAheadDistance = lookAheadDistance;
        this.waypoints = waypoints;
        currentItem = 0;
    }

    public ResultPosition getNextSegmentPosition(Vector2D current) {
        if (currentItem < waypoints.size() - 1) {
            Vector2D p0 = waypoints.get(currentItem);
            Vector2D p1 = waypoints.get(currentItem + 1);
            Vector2D a = p1.minus(p0);
            Vector2D b = current.minus(p0);
            double t = b.innerProduct(a) / a.innerProduct(a);
            if (t < 0)
                t = 0;
            else if (t > 1)
                t = 1;
            Vector2D q = a.scale(t);
            Vector2D p = q.plus(p0);
            Vector2D bper = b.minus(q);
            double distance = bper.size();

            if (distance >= lookAheadDistance) {
                RobotPosition m = new RobotPosition(p);
                m.rotation = Math.atan2(m.y - current.y, m.x - current.x);
                m.angleUnit = AngleUnit.RADIANS;
                return new ResultPosition(m, false);
            }
            Vector2D s = p1.minus(p);
            double n = Math.sqrt(lookAheadDistance * lookAheadDistance - distance * distance) / s.size();

            double m0 = t + (1 - t) * n;
            double m1 = t - (1 - t) * n;

            if (m1 > m0)
                m0 = m1;
            if (m0 > 1)
                m0 = 1;
            if (m0 < 0)
                m0 = 0;
            RobotPosition m = new RobotPosition(p0.plus(a.scale(m0)));
            m.rotation = Math.atan2(m.y - current.y, m.x - current.x);
            m.angleUnit = AngleUnit.RADIANS;
            return new ResultPosition(m, m0 >= 0.99);
        } else if (currentItem < waypoints.size()) {
            RobotPosition p = waypoints.get(currentItem);
            double distance = Math.hypot(current.x - p.x, current.y - p.y);
            return new ResultPosition(new RobotPosition(p), distance < lookAheadDistance);
        } else {
            return null;
        }
    }

    public ResultPosition getNextPosition(Vector2D current) {
        ResultPosition pos;
        do {
            pos = getNextSegmentPosition(current);
        } while (pos != null && pos.reached && ++currentItem < waypoints.size() - 1);
        return pos;
    }

}