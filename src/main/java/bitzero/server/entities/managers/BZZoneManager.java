/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities.managers;

import bitzero.server.BitZeroServer;
import bitzero.server.config.IConfigurator;
import bitzero.server.config.ZoneSettings;
import bitzero.server.core.BaseCoreService;
import bitzero.server.entities.Zone;
import bitzero.server.entities.managers.IZoneManager;
import bitzero.server.exceptions.BZException;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.util.stats.ITrafficMeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZZoneManager
extends BaseCoreService
implements IZoneManager {
    private Logger logger;
    private ConcurrentMap<Integer,Zone> zones;
    private BitZeroServer bz;
    private IConfigurator configurator;
    private final ConcurrentMap trafficMonitors = new ConcurrentHashMap();
    private final TrafficMeterExecutor trafficMeterExecutor;

    @Override
    public Zone getZoneByName(String s) {
        return null;
    }

    public BZZoneManager() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        if (this.zones == null) {
            this.zones = new ConcurrentHashMap();
        }
        this.trafficMeterExecutor = new TrafficMeterExecutor(this.trafficMonitors.values());
        Runtime.getRuntime().addShutdownHook(new ShutDownHandler(null));
    }

    @Override
    public void init(Object o) {
        super.init(o);
        this.bz = BitZeroServer.getInstance();
        this.configurator = this.bz.getConfigurator();
    }

    @Override
    public void addZone(Zone zone) {
        if (this.zones.containsKey(zone.getId())) {
            throw new BZRuntimeException("Zone already exists: " + zone.getName() + ". Can't add the same zone more than once.");
        }
        this.zones.put(zone.getId(), zone);
    }

    @Override
    public Zone getZoneById(int id) {
        return (Zone)this.zones.get(id);
    }

    public List getZoneList() {
        return new ArrayList(this.zones.values());
    }

    @Override
    public synchronized void initializeZones() throws BZException {
        if (this.zones.size() > 0) {
            this.logger.info(String.valueOf(this.zones.size()) + " Zones found in cluster: ");
            for (Zone zone : this.zones.values()) {
                this.logger.info(zone.toString());
            }
            return;
        }
    }

    @Override
    public void toggleZone(String name, boolean isActive) {
        Zone theZone = this.getZoneByName(name);
        theZone.setActive(isActive);
    }

    public ITrafficMeter getZoneTrafficMeter(String zoneName) {
        return (ITrafficMeter)this.trafficMonitors.get(zoneName);
    }

    private void activateTrafficMonitors() {
    }

    @Override
    public Zone createZone(ZoneSettings settings) throws BZException {
        Zone zone = new Zone(settings.name, settings.id);
        zone.setCustomLogin(settings.isCustomLogin);
        zone.setMaxAllowedRooms(settings.maxRooms);
        zone.setMaxAllowedUsers(settings.maxUsers);
        zone.setMinRoomNameChars(settings.minRoomNameChars);
        zone.setMaxRoomNameChars(settings.maxRoomNameChars);
        zone.setDefaultPlayerIdGeneratorClassName(settings.defaultPlayerIdGeneratorClass);
        zone.setUserCountChangeUpdateInterval(settings.userCountChangeUpdateInterval);
        zone.setUserReconnectionSeconds(settings.userReconnectionSeconds);
        zone.setMaxUserIdleTime(0);
        ArrayList<String> defaultRoomGroups = new ArrayList<String>();
        if (defaultRoomGroups.size() == 0) {
            defaultRoomGroups.add("default");
        }
        zone.setDefaultGroups(defaultRoomGroups);
        ArrayList<String> publicRoomGroups = new ArrayList<String>();
        if (publicRoomGroups.size() == 0) {
            publicRoomGroups.add("default");
        }
        zone.setPublicGroups(publicRoomGroups);
        zone.setZoneManager(this);
        zone.setActive(true);
        this.addZone(zone);
        return zone;
    }

    private void populateTransientFields() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.bz = BitZeroServer.getInstance();
        this.configurator = this.bz.getConfigurator();
    }

    private static class TrafficMeterExecutor
    implements Runnable {
        private final Logger log;
        private final Collection<ITrafficMeter> trafficMonitors;

        @Override
        public void run() {
            try {
                long t1 = System.nanoTime();
                for (ITrafficMeter monitor : this.trafficMonitors) {
                    monitor.onTick();
                }
                long t2 = System.nanoTime();
                this.log.debug("Traffic Monitor update: " + (double)(t2 - t1) / 1000000.0 + "ms.");
            }
            catch (Exception e) {
                this.log.warn("Unexpected exception: " + e + ". Task will not be interrupted.");
            }
        }

        public TrafficMeterExecutor(Collection trafficMonitors) {
            this.log = LoggerFactory.getLogger(this.getClass());
            this.trafficMonitors = trafficMonitors;
        }
    }

    private final class ShutDownHandler
    extends Thread {
        @Override
        public void run() {
        }

        private ShutDownHandler() {
        }

        ShutDownHandler(ShutDownHandler shutdownhandler) {
            this();
        }
    }

}

