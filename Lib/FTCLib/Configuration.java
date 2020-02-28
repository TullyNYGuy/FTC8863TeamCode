package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import android.os.Environment;

import java.io.File;
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
        String fileName = Environment.getExternalStorageDirectory() + "/" + CONFIG_DIRECTORY + "/" + configFile;
        try {
            super.load(new FileInputStream(fileName));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
public boolean delete(String configFile){
    String fileName = Environment.getExternalStorageDirectory() + "/" + CONFIG_DIRECTORY + "/" + configFile;


            File f = new File(fileName);


            return f.delete();



}
    public boolean delete(){
        return delete(CONFIG_FILE);
    }
    public boolean store() {
        return store(CONFIG_FILE);
    }

    public boolean store(String configFile) {
        String fileName = Environment.getExternalStorageDirectory() + "/" + CONFIG_DIRECTORY + "/" + configFile;
        try {
            super.store(new FileOutputStream(fileName), "Robot Configuration");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String getPropertyString(String key) {
        return getProperty(key);
    }

    public String getPropertyString(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }

    public Pair<String, Boolean> getPropertyStringCheck(String key, String defaultValue) {
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

    public Pair<Double, Boolean> getPropertyDoubleCheck(String key, Double defaultValue) {
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

    public Pair<Integer, Boolean> getPropertyIntegerCheck(String key, Integer defaultValue) {
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

    public Long getPropertyLong(String key, Long defaultValue) {
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

    public Pair<Long, Boolean> getPropertyLongCheck(String key, Long defaultValue) {
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

    public Boolean getPropertyBoolean(String key, Boolean defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return Boolean.valueOf(propVal);
        } else {
            return defaultValue;
        }
    }

    public Pair<Boolean, Boolean> getPropertyBooleanCheck(String key, Boolean defaultValue) {
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

    public Byte getPropertyByte(String key, Byte defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return Byte.valueOf(propVal);
        } else {
            return defaultValue;
        }
    }

    public Pair<Byte, Boolean> getPropertyByteCheck(String key, Byte defaultValue) {
        String propVal = getProperty(key);
        if (propVal != null) {
            return new Pair<Byte, Boolean>(Byte.valueOf(propVal), true);
        } else {
            return new Pair<Byte, Boolean>(defaultValue, false);
        }
    }

}
