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

    public boolean load() {
        return load(CONFIG_FILE);
    }

    public boolean load(String configFile) {
        String fileName = Environment.getExternalStorageDirectory() + CONFIG_DIRECTORY + "/" + configFile;
        try {
            super.load(new FileInputStream(fileName));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean store() {
        return store(CONFIG_FILE);
    }

    public boolean store(String configFile) {
        String fileName = Environment.getExternalStorageDirectory() + CONFIG_DIRECTORY + "/" + configFile;
        try {
            super.store(new FileOutputStream(fileName), "Robot Configuration");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Pair<String, Boolean> getPropertyString(String key, String defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return new Pair<String, Boolean>(propVal, true);
        } else {
            return new Pair<String, Boolean>(defaultValue, false);
        }
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

    public Pair<Double, Boolean> getPropertyDouble(String key, Double defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return new Pair<Double, Boolean>(Double.valueOf(propVal), true);
            } catch (NumberFormatException ex) {
                return new Pair<Double, Boolean>(defaultValue, false);
            }
        } else {
            return new Pair<Double, Boolean>(defaultValue, false);
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

    public Pair<Integer, Boolean> getPropertyInteger(String key, Integer defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return new Pair<Integer, Boolean>(Integer.valueOf(propVal), true);
            } catch (NumberFormatException ex) {
                return new Pair<Integer, Boolean>(defaultValue, false);
            }
        } else {
            return new Pair<Integer, Boolean>(defaultValue, false);
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

    public Pair<Long, Boolean> getPropertyLong(String key, Long defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                return new Pair<Long, Boolean>(Long.valueOf(propVal), true);
            } catch (NumberFormatException ex) {
                return new Pair<Long, Boolean>(defaultValue, false);
            }
        } else {
            return new Pair<Long, Boolean>(defaultValue, false);
        }
    }
    public Boolean getPropertyBoolean(String key) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return Boolean.valueOf(propVal);
        }
        return null;
    }

    public Pair<Boolean, Boolean> getPropertyBoolean(String key, Boolean defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return new Pair<Boolean, Boolean>(Boolean.valueOf(propVal), true);
        } else {
            return new Pair<Boolean, Boolean>(defaultValue, false);
        }
    }

    public Byte getPropertyByte(String key) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return Byte.valueOf(propVal);
        }
        return null;
    }

    public Pair<Byte, Boolean> getPropertyByte(String key, Byte defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return new Pair<Byte, Boolean>(Byte.valueOf(propVal), true);
        } else {
            return new Pair<Byte, Boolean>(defaultValue, false);
        }
    }

}
