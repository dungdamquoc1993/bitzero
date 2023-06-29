/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.core.security;

import bitzero.engine.core.security.EngineThread;
import bitzero.engine.core.security.IAllowedThread;
import bitzero.engine.core.security.ISecurityManager;
import bitzero.engine.core.security.ThreadComparisonType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSecurityManager
implements ISecurityManager {
    private final List<IAllowedThread> secureThreads = new ArrayList();
    private String name;
    private final Logger bootLogger = LoggerFactory.getLogger((String)"bootLogger");

    @Override
    public void init(Object o) {
        this.secureThreads.add(new EngineThread("bitzero.engine.controllers", ThreadComparisonType.STARTSWITH));
        this.bootLogger.info("Security Manager started");
    }

    @Override
    public void destroy(Object o) {
        this.bootLogger.info("Security Manager stopped");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void handleMessage(Object obj) {
    }

    @Override
    public boolean isEngineThread(Thread thread) {
        boolean okay = false;
        String currThreadName = thread.getName();
        for (IAllowedThread allowedThread : this.secureThreads) {
            if (allowedThread.getComparisonType() == ThreadComparisonType.STARTSWITH) {
                if (!currThreadName.startsWith(allowedThread.getName())) continue;
                okay = true;
                break;
            }
            if (allowedThread.getComparisonType() == ThreadComparisonType.EXACT) {
                if (!currThreadName.equals(currThreadName)) continue;
                okay = true;
                break;
            }
            if (allowedThread.getComparisonType() != ThreadComparisonType.ENDSWITH || !currThreadName.endsWith(allowedThread.getName())) continue;
            okay = true;
            break;
        }
        return okay;
    }
}

