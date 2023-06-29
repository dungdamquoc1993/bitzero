/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.extensions;

import bitzero.engine.sessions.ISession;
import bitzero.server.core.IBZEventListener;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.User;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public interface IBZExtension {
    public void init();

    public void destroy();

    public void monitor();

    public String getName();

    public void setName(String var1);

    public String getExtensionFileName();

    public void setExtensionFileName(String var1);

    public String getPropertiesFileName();

    public void setPropertiesFileName(String var1) throws IOException;

    public Properties getConfigProperties();

    public boolean isActive();

    public void setActive(boolean var1);

    public void addEventListener(IBZEventType var1, IBZEventListener var2);

    public void removeEventListener(IBZEventType var1, IBZEventListener var2);

    public void handleClientRequest(short var1, User var2, DataCmd var3) throws BZException;

    public void handleClientRequest(String var1, User var2, ISFSObject var3) throws BZException;

    public void doLogin(short var1, ISession var2, DataCmd var3) throws BZException;

    public void doLogin(ISession var1, ISFSObject var2) throws Exception;

    public Object handleInternalMessage(String var1, Object var2);

    public void send(BaseMsg var1, User var2);

    public void send(BaseMsg var1, Collection<ISession> var2);
}

