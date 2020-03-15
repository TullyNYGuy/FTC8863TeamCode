package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TelemetryStreamer {

    FTCRobot robot;

    private class ConnectionListener extends Thread {

        final public int LISTEN_PORT = 2481;

        private ServerSocket serverSocket;

        Collection<ConnectionHandler> connections;

        private AtomicBoolean runningListener;

        private class ConnectionHandler implements Runnable {

            private Socket socket;
            private ScheduledExecutorService scheduler;
            private Poller pollerThread;
            private ScheduledFuture<?> pollerTask;
            private AtomicBoolean running;

            private class Poller implements Runnable {

                RobotPosition position;
                DataOutputStream stream;

                public Poller() {
                    position = new RobotPosition();
                    position.distanceUnit = DistanceUnit.CM;
                    position.angleUnit = AngleUnit.RADIANS;
                    try {
                        stream = new DataOutputStream(socket.getOutputStream());
                    } catch (IOException e) {
                        stream = null;
                    }
                }

                @Override
                public void run() {
                    if (stream == null) {
                        stopPoller();
                    }
                    robot.getCurrentRobotPosition(position);
                    byte[] data = String.format("{\"t\":%d,\"x\":%.2f,\"y\":%.2f,\"r\":%.2f}",
                            System.currentTimeMillis(),
                            position.x, position.y, position.rotation).getBytes();
                    try {
                        stream.writeInt(data.length);
                        stream.write(data);
                    } catch (IOException e) {
                        stopPoller();
                    }

                }
            }

            public ConnectionHandler(Socket socket) {
                this.socket = socket;
                pollerThread = new Poller();
                scheduler = Executors.newScheduledThreadPool(2);
                pollerTask = null;
                running = new AtomicBoolean(false);
            }

            @Override
            public void run() {
                running.set(true);
                try {
                    DataInputStream stream = new DataInputStream(socket.getInputStream());
                    while (running.get()) {
                        while (running.get() && stream.available() < Integer.BYTES) {
                            yield();
                        }
                        int size = stream.readInt();
                        byte[] buf = new byte[size];
                        stream.read(buf);
                        String s = new String(buf);
                        JSON command = JSON.fromString(buf.toString());
                        if (command != null) {
                            String cmd = command.getString("command");
                            String frequency = command.getString("frequency");
                            if ("start".equals(cmd)) {
                                long freqMs = 200;
                                try {
                                    freqMs = Long.valueOf(frequency);
                                } catch (NumberFormatException ex) {

                                }
                                startPoller(freqMs);
                            } else if ("stop".equals(cmd)) {
                                stopPoller();
                            }
                        }
                    }
                    socket.close();
                } catch (IOException e) {
                } finally {
                    stopPoller();
                    stopConnection(this);
                }
                running.set(false);
            }

            public void stop() {
                stopPoller();
                running.set(false);
            }

            private void stopPoller() {
                if (pollerTask != null) {
                    while (!pollerTask.isDone()) {
                        pollerTask.cancel(false);
                    }
                    pollerTask = null;
                }
            }

            private void startPoller(long intervalMillis) {
                if (pollerTask == null)
                    pollerTask = scheduler.scheduleAtFixedRate(pollerThread, 0, intervalMillis, TimeUnit.MILLISECONDS);
            }
        }

        public ConnectionListener() {
            connections = new CopyOnWriteArrayList<ConnectionHandler>();
            runningListener = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(LISTEN_PORT);
                while (runningListener.get()) {

                }
                serverSocket.close();
            } catch (IOException e) {

            } finally {
                for (ConnectionHandler handler : connections) {
                    handler.stop();
                }
                connections.clear();
            }
            runningListener.set(false);
        }

        public void startListener() {
            runningListener.set(true);
            start();
        }

        public void stopListener() {
            runningListener.set(false);
        }

        private void stopConnection(ConnectionHandler handler) {
            connections.remove(handler);
        }
    }

    ConnectionListener listener;
    public TelemetryStreamer(FTCRobot robot) {
        this.robot = robot;
        listener = new ConnectionListener();
    }

    public boolean start() {
        listener.startListener();
        return true;
    }

    public void stop() {
        listener.stopListener();
    }
}
