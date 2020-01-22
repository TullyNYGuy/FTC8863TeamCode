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

    public Double getPropertyDouble(String key) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return Double.valueOf(propVal);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    public Double getPropertyDouble(String key, Double defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return Double.valueOf(propVal);
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public Integer getPropertyInteger(String key) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return Integer.valueOf(propVal);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    public Integer getPropertyInteger(String key, Integer defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return Integer.valueOf(propVal);
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public Long getPropertyLong(String key) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return Long.valueOf(propVal);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    public Long getPropertyInteger(String key, Long defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return Long.valueOf(propVal);
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public Boolean getPropertyBoolean(String key) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return Boolean.valueOf(propVal);
        }
        return null;
    }

    public Boolean getPropertyBoolean(String key, Boolean defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return Boolean.valueOf(propVal);
        } else {
            return defaultValue;
        }
    }

    public Byte getPropertyByte(String key) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return Byte.valueOf(propVal);
        }
        return null;
    }

    public Byte getPropertyByte(String key, Byte defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return Byte.valueOf(propVal);
        } else {
            return defaultValue;
        }
    }

}
