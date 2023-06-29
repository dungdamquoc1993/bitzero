/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package extension;

import bitzero.engine.sessions.ISession;
import bitzero.server.config.ConfigHandle;
import bitzero.server.entities.User;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import bitzero.util.socialcontroller.bean.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExtension
extends BZExtension {
    private static final Logger logger = LoggerFactory.getLogger((String)"TestExtension");

    public TestExtension() {
        this.setName("TestExtension");
    }

    @Override
    public void init() {
        this.trace("***********************************************");
        this.trace("*   Starting TestExtension extension");
        this.trace("*  Config Init ");
        this.trace("*******");
        ConfigHandle.instance();
        this.initJson();
        this.trace("*  Service Handlers Init ");
        this.trace("*******");
        this.trace("*  Event Handlers Init ");
        this.trace("*******");
        this.trace("*  LoginSuccessHandler ");
        this.trace("*  TestExtension extension initilalization completed ");
        this.trace("***********************************************");
    }

    private void initJson() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doLogin(short cmdId, ISession session, DataCmd objData) throws BZException {
    }

    @Override
    public void doLogin(ISession session, ISFSObject reqObj) throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(Integer.toString(TestExtension.getRandomBetween(1, 100000)));
        String userName = reqObj.getUtfString("un");
        userInfo.setUsername(userName);
        User u = ExtensionUtility.instance().canLogin(userInfo, "", session);
    }

    public static int getRandomBetween(int min, int max) {
        if (max < min) {
            return min;
        }
        return min == max ? min : min + (int)(Math.random() * (double)(max - min + 1));
    }
}

