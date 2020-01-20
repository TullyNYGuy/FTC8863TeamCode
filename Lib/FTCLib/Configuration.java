package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import android.os.Environment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration extends Properties {
    final public String CONFIG_DIRECTORY = "FTC8863";
    final public String CONFIG_FILE = "configuration.properties";

    public Configuration() {
        super();
    }

    public void load() throws IOException {
        load(CONFIG_FILE);
    }

    public void load(String configFile) throws IOException {
        String fileName = Environment.getExternalStorageDirectory() + CONFIG_DIRECTORY + "/" + configFile;
        super.load(new FileInputStream(fileName));
    }

    public void store() throws IOException {
        store(CONFIG_FILE);
    }

    public void store(String configFile) throws IOException {
        String fileName = Environment.getExternalStorageDirectory() + CONFIG_DIRECTORY + "/" + configFile;
        super.store(new FileOutputStream(fileName), "Robot Configuration");
    }

}
