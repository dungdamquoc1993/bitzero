/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.util.monitor;

import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.IBZApi;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.util.monitor.IGhostUserHunter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhostUserHunter
implements IGhostUserHunter {
    private final BitZeroServer bz = BitZeroServer.getInstance();
    private final IBZApi api;
    private ISessionManager sm;
    private final Logger log;
    private static final String EOL = System.getProperty("line.separator");
    private static final int TRIGGER_INTERVAL_SEC = 900;
    private static final int TOT_CYCLES = 90;
    private int cycleCounter;

    public GhostUserHunter() {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.cycleCounter = 0;
        this.api = this.bz.getAPIManager().getBzApi();
    }

    @Override
    public void hunt() {
        if (this.sm == null) {
            this.sm = this.bz.getSessionManager();
        }
        if (++this.cycleCounter < 90) {
            return;
        }
        this.cycleCounter = 0;
        List<User> ghosts = this.searchGhosts();
        if (ghosts.size() > 0) {
            this.log.info(this.buildReport(ghosts));
        }
        for (User ghost : ghosts) {
            this.api.disconnectUser(ghost);
        }
    }

    private List searchGhosts() {
        List<User> allUsers = this.bz.getUserManager().getAllUsers();
        ArrayList<User> ghosts = new ArrayList<User>();
        for (User u : allUsers) {
            ISession sess;
            if (u.isNpc() || ((sess = u.getSession()) == null || this.sm.containsSession(sess)) && !sess.isIdle() && !sess.isMarkedForEviction()) continue;
            ghosts.add(u);
        }
        return ghosts;
    }

    private String buildReport(List<User> ghosts) {
        StringBuilder sb = new StringBuilder("GHOST REPORT");
        sb.append(EOL).append("Total ghosts: ").append(ghosts.size()).append(EOL);
        try {
            for (User ghost : ghosts) {
                sb.append(ghost.getId()).append(", ").append(ghost.getName()).append(", Connected: ").append(ghost.getSession().isConnected()).append(", Idle: ").append(ghost.getSession().isIdle()).append(", Marked: ").append(ghost.getSession().isMarkedForEviction()).append(", Frozen: ").append(ghost.getSession().isFrozen()).append(", sessById: ").append(this.sm.getSessionById(ghost.getId()));
            }
        }
        catch (Exception iterator) {
            // empty catch block
        }
        return sb.toString();
    }
}

