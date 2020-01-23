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

    public String getProperty(String key, String defaultValue, Boolean found) {
        if (found != null)
            found = Boolean.FALSE;
        String propVal = getProperty(key);
        if (propVal != null) {
            if (found != null)
                found = Boolean.TRUE;
            return propVal;
        } else {
            return defaultValue;
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
        return getPropertyDouble(key, defaultValue, null);
    }

    public Double getPropertyDouble(String key, Double defaultValue, Boolean found) {
        if (found != null)
            found = Boolean.FALSE;
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                Double res = Double.valueOf(propVal);
                if (found != null)
                    found = Boolean.TRUE;
                return res;
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
        return getPropertyInteger(key, defaultValue, null);
    }

    public Integer getPropertyInteger(String key, Integer defaultValue, Boolean found) {
        if (found != null)
            found = Boolean.FALSE;
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                Integer res = Integer.valueOf(propVal);
                if (found != null)
                    found = Boolean.TRUE;
                return res;
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

    public Long getPropertyLong(String key, Long defaultValue) {
        return getPropertyLong(key, defaultValue, null);
    }

    public Long getPropertyLong(String key, Long defaultValue, Boolean found) {
        if (found != null)
            found = Boolean.FALSE;
        String propVal = getProperty(key);
        if (propVal != null) {
            try {
                Long res = Long.valueOf(propVal);
                if (found != null)
                    found = Boolean.TRUE;
                return res;
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
        return getPropertyBoolean(key, defaultValue, null);
    }

    public Boolean getPropertyBoolean(String key, Boolean defaultValue, Boolean found) {
        if (found != null)
            found = Boolean.FALSE;
        String propVal = getProperty(key);
        if (propVal != null) {
            if (found != null)
                found = Boolean.TRUE;
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
        return getPropertyByte(key, defaultValue, null);
    }

    public Byte getPropertyByte(String key, Byte defaultValue, Boolean found) {
        if (found != null)
            found = Boolean.FALSE;
        String propVal = getProperty(key);
        if (propVal != null) {
            if (found != null)
                found = Boolean.TRUE;
            return Byte.valueOf(propVal);
        } else {
            return defaultValue;
        }
    }

}
