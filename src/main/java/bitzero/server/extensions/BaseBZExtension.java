/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.extensions;

import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.IBZApi;
import bitzero.server.api.response.ResponseApi;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventListener;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.extensions.ExtensionLevel;
import bitzero.server.extensions.ExtensionLogLevel;
import bitzero.server.extensions.ExtensionReloadMode;
import bitzero.server.extensions.ExtensionType;
import bitzero.server.extensions.IBZExtension;
import bitzero.server.extensions.data.BaseMsg;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseBZExtension
implements IBZExtension,
IBZEventListener {
    private String name;
    private String fileName;
    private String configFileName;
    private ExtensionLevel level;
    private ExtensionType type;
    private volatile boolean active = true;
    protected final BitZeroServer bz = BitZeroServer.getInstance();
    private Properties configProperties;
    private ExtensionReloadMode reloadMode;
    private String currentPath;
    protected volatile int lagSimulationMillis = 0;
    private final Logger logger = LoggerFactory.getLogger((String)"Extensions");
    protected final ResponseApi resApi = this.bz.getAPIManager().getResApi();
    protected final IBZApi bzApi = this.bz.getAPIManager().getBzApi();

    public String getCurrentFolder() {
        return this.currentPath;
    }

    @Override
    public void monitor() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (this.name != null) {
            throw new BZRuntimeException("Cannot redefine name of extension: " + this.toString());
        }
        this.name = name;
        this.currentPath = "extensions/" + name + "/";
    }

    public IBZApi getApi() {
        return this.bzApi;
    }

    @Override
    public String getExtensionFileName() {
        return this.fileName;
    }

    @Override
    public Properties getConfigProperties() {
        return this.configProperties;
    }

    @Override
    public String getPropertiesFileName() {
        return this.configFileName;
    }

    @Override
    public void setPropertiesFileName(String fileName) throws IOException {
        if (this.configFileName != null) {
            throw new BZRuntimeException("Cannot redefine properties file name of an extension: " + this.toString());
        }
        boolean isDefault = false;
        if (fileName == null || fileName.length() == 0 || fileName.equals("config.properties")) {
            isDefault = true;
            this.configFileName = "config.properties";
        } else {
            this.configFileName = fileName;
        }
        String fileToLoad = "extensions/" + this.name + "/" + this.configFileName;
        if (isDefault) {
            this.loadDefaultConfigFile(fileToLoad);
        } else {
            this.loadCustomConfigFile(fileToLoad);
        }
    }

    @Override
    public void handleServerEvent(IBZEvent ibzevent) throws Exception {
    }

    @Override
    public Object handleInternalMessage(String cmdName, Object params) {
        return null;
    }

    private void loadDefaultConfigFile(String fileName) {
        this.configProperties = new Properties();
        try {
            this.configProperties.load(new FileInputStream(fileName));
        }
        catch (IOException var2_2) {
            // empty catch block
        }
    }

    private void loadCustomConfigFile(String fileName) throws IOException {
        this.configProperties = new Properties();
        this.configProperties.load(new FileInputStream(fileName));
    }

    @Override
    public void setExtensionFileName(String fileName) {
        if (this.fileName != null) {
            throw new BZRuntimeException("Cannot redefine file name of an extension: " + this.toString());
        }
        this.fileName = fileName;
    }

    @Override
    public void addEventListener(IBZEventType eventType, IBZEventListener listener) {
        this.bz.getEventManager().addEventListener(eventType, listener);
    }

    @Override
    public void removeEventListener(IBZEventType eventType, IBZEventListener listener) {
        this.bz.getEventManager().removeEventListener(eventType, listener);
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean flag) {
        this.active = flag;
    }

    public ExtensionLevel getLevel() {
        return this.level;
    }

    public void setLevel(ExtensionLevel level) {
        if (this.level != null) {
            throw new BZRuntimeException("Cannot change level for extension: " + this.toString());
        }
        this.level = level;
    }

    public void sendExceptMe(BaseMsg bmsg, List<User> listUser, User u) {
        listUser.remove(u);
        ArrayList<ISession> recipients = null;
        recipients = new ArrayList<ISession>();
        Iterator<User> iterator = listUser.iterator();
        while (iterator.hasNext()) {
            ISession session = iterator.next().getSession();
            recipients.add(session);
        }
        this.resApi.sendExtResponse(bmsg, recipients);
    }

    public void sendExceptMe(BaseMsg bmsg, List<ISession> listSS, ISession ss) {
        listSS.remove(ss);
        this.resApi.sendExtResponse(bmsg, listSS);
    }

    public void sendUsers(BaseMsg bmsg, List<User> listUser) {
        ArrayList<ISession> recipients = null;
        recipients = new ArrayList<ISession>();
        Iterator<User> iterator = listUser.iterator();
        while (iterator.hasNext()) {
            ISession session = iterator.next().getSession();
            recipients.add(session);
        }
        this.resApi.sendExtResponse(bmsg, recipients);
    }

    @Override
    public void send(BaseMsg bmsg, Collection<ISession> recipients) {
        this.resApi.sendExtResponse(bmsg, recipients);
    }

    @Override
    public void send(BaseMsg bmsg, User recipient) {
        this.resApi.sendExtResponse(bmsg, recipient);
    }

    public void send(BaseMsg bmsg, ISession recipient) {
        this.resApi.sendExtResponse(bmsg, recipient);
    }

    public String toString() {
        return String.format("{ Ext: %s, Type: %s, Lev: %s, %s, %s }", new Object[]{this.name, this.type, this.level, "{}"});
    }

    public /* varargs */ void trace(Object ... args) {
        this.logger.info(this.getTraceMessage(args));
    }

    public /* varargs */ void trace(ExtensionLogLevel level, Object ... args) {
        if (level == ExtensionLogLevel.DEBUG) {
            this.logger.debug(this.getTraceMessage(args));
        } else if (level == ExtensionLogLevel.INFO) {
            this.logger.info(this.getTraceMessage(args));
        } else if (level == ExtensionLogLevel.WARN) {
            this.logger.warn(this.getTraceMessage(args));
        } else if (level == ExtensionLogLevel.ERROR) {
            this.logger.error(this.getTraceMessage(args));
        }
    }

    private /* varargs */ String getTraceMessage(Object ... args) {
        StringBuilder traceMsg = new StringBuilder().append("{").append(this.name).append("}: ");
        for (Object o : args) {
            traceMsg.append(o.toString()).append(" ");
        }
        return traceMsg.toString();
    }

    protected void removeEventsForListener(IBZEventListener listener) {
    }

    private void checkLagSimulation() {
        if (this.lagSimulationMillis > 0) {
            try {
                this.logger.debug("Lag simulation, sleeping for: " + this.lagSimulationMillis + "ms.");
                Thread.sleep(this.lagSimulationMillis);
            }
            catch (InterruptedException e) {
                this.logger.warn("Interruption during lag simulation: " + e);
            }
        }
    }
}

