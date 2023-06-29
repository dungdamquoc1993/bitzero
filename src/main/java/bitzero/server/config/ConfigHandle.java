/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package bitzero.server.config;

import bitzero.server.exceptions.ExceptionMessageComposer;
import bitzero.server.util.DebugConsole;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.vinplay.vbee.common.config.VBeePath;
import org.slf4j.Logger;

public class ConfigHandle {
    private static ConfigHandle _instance;
    private static final Object lock;
    private Random r = new Random(System.currentTimeMillis());
    private Properties props;
    private ConcurrentHashMap<String, Long> longPropsCaching;
    private ConcurrentHashMap<String, String[]> listPropsCaching;
    private ConcurrentHashMap<String, Properties> propsByGames;

    private ConfigHandle() {
        try {
            this.longPropsCaching = new ConcurrentHashMap();
            this.listPropsCaching = new ConcurrentHashMap();
            this.props = new Properties();
            this.props.load(new FileInputStream(new File(VBeePath.basePath.concat("conf/cluster.properties"))));
            this.propsByGames = new ConcurrentHashMap();
            String[] games = this.props.getProperty("games").split(";");
            for (int i = 0; i < games.length; ++i) {
                Properties p = new Properties();
                File f = new File(VBeePath.basePath + "conf" + File.separator + games[i] + ".properties");
                if (f.exists()) {
                    p.load(new FileInputStream(f));
                }
                p.setProperty("defaultScore", p.getProperty("defaultScore", "0"));
                this.propsByGames.put(games[i], p);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("An error occurred during the Execution");
            DebugConsole.log.error(msg.toString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ConfigHandle instance() {
        if (_instance == null) {
            Object object = lock;
            synchronized (object) {
                if (_instance == null) {
                    _instance = new ConfigHandle();
                }
            }
        }
        return _instance;
    }

    public String get(String name) {
        if (this.props != null) {
            return this.props.getProperty(name);
        }
        return null;
    }

    public Long getLong(String name) {
        Long result = this.longPropsCaching.get(name);
        if (result == null) {
            result = new Long(0);
            try {
                result = Long.parseLong(this.props.getProperty(name));
            }
            catch (Exception e) {
                e.printStackTrace();
                ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
                msg.setDescription("An error occurred during the Execution");
                DebugConsole.log.error(msg.toString());
            }
            this.longPropsCaching.put(name, result);
        }
        return result;
    }

    public Boolean getBoolean(String name) {
        return this.getLong(name) == 1;
    }

    public Boolean getBoolean(String game, String name) {
        return this.getLong(game, name) == 1;
    }

    public String getRandom(String name) {
        String[] data = this.listPropsCaching.get(name);
        if (data == null) {
            data = new String[]{};
            try {
                data = this.props.getProperty(name).split(";");
            }
            catch (Exception e) {
                ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
                msg.setDescription("An error occurred during the Execution");
                DebugConsole.log.error(msg.toString());
            }
            this.listPropsCaching.put(name, data);
        }
        if (data.length > 0) {
            return data[this.r.nextInt(data.length)];
        }
        return null;
    }

    public String get(String game, String name) {
        if (this.propsByGames.get(game) != null) {
            return this.propsByGames.get(game).getProperty(name);
        }
        return null;
    }

    public Long getLong(String game, String name) {
        Long result = null;
        try {
            result = Long.parseLong(this.propsByGames.get(game).getProperty(name));
        }
        catch (Exception e) {
            e.printStackTrace();
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("An error occurred during the Execution");
            DebugConsole.log.error(msg.toString());
        }
        return result;
    }

    public int getInt(String game, String name) {
        int result = 0;
        try {
            result = Integer.parseInt(this.propsByGames.get(game).getProperty(name).trim());
        }
        catch (Exception e) {
            e.printStackTrace();
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("An error occurred during the Execution");
            DebugConsole.log.error(msg.toString());
        }
        return result;
    }

    static {
        lock = new Object();
    }
}

