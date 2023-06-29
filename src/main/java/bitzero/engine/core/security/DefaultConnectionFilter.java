/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core.security;

import bitzero.engine.core.security.IConnectionFilter;
import bitzero.engine.exceptions.RefusedAddressException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultConnectionFilter
implements IConnectionFilter {
    private final Set<String> addressWhiteList = new HashSet<>();
    private final Set<String> bannedAddresses = new HashSet();
    private final ConcurrentMap addressMap = new ConcurrentHashMap();
    private volatile int maxConnectionsPerIp = 10;
    private final Set ghostList = new HashSet();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addBannedAddress(String ipAddress) {
        Set set = this.bannedAddresses;
        synchronized (set) {
            this.bannedAddresses.add(ipAddress);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addWhiteListAddress(String ipAddress) {
        Set set = this.addressWhiteList;
        synchronized (set) {
            this.addressWhiteList.add(ipAddress);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getBannedAddresses() {
        String[] set = null;
        Set set2 = this.bannedAddresses;
        synchronized (set2) {
            set = new String[this.bannedAddresses.size()];
            set = this.bannedAddresses.toArray(set);
        }
        return set;
    }

    @Override
    public int getMaxConnectionsPerIp() {
        return this.maxConnectionsPerIp;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getWhiteListAddresses() {
        String[] set = null;
        Set set2 = this.addressWhiteList;
        synchronized (set2) {
            set = new String[this.addressWhiteList.size()];
            set = this.addressWhiteList.toArray(set);
        }
        return set;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAddress(String ipAddress) {
        ConcurrentMap concurrentMap = this.addressMap;
        synchronized (concurrentMap) {
            int value;
            AtomicInteger count = (AtomicInteger)this.addressMap.get(ipAddress);
            if (count != null && (value = count.decrementAndGet()) == 0) {
                this.addressMap.remove(ipAddress);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBannedAddress(String ipAddress) {
        Set set = this.bannedAddresses;
        synchronized (set) {
            this.bannedAddresses.remove(ipAddress);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWhiteListAddress(String ipAddress) {
        Set set = this.addressWhiteList;
        synchronized (set) {
            this.addressWhiteList.remove(ipAddress);
        }
    }

    @Override
    public void setMaxConnectionsPerIp(int max) {
        this.maxConnectionsPerIp = max;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void validateAndAddAddress(String ipAddress) throws RefusedAddressException {
        Object object = this.addressWhiteList;
        synchronized (object) {
            if (this.addressWhiteList.contains(ipAddress)) {
                return;
            }
        }
        if (this.isAddressBanned(ipAddress)) {
            throw new RefusedAddressException("Ip Address: " + ipAddress + " has banned.");
        }
        object = this.addressMap;
        synchronized (object) {
            AtomicInteger count = (AtomicInteger)this.addressMap.get(ipAddress);
            if (count != null && count.intValue() >= this.maxConnectionsPerIp) {
                this.ghostList.add(ipAddress);
                throw new RefusedAddressException("Ip Address: " + ipAddress + " has reached maximum allowed connections.");
            }
            if (count == null) {
                count = new AtomicInteger(1);
                this.addressMap.put(ipAddress, count);
            } else {
                count.incrementAndGet();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isAddressBanned(String ip) {
        boolean isBanned = false;
        Set set = this.bannedAddresses;
        synchronized (set) {
            isBanned = this.bannedAddresses.contains(ip);
        }
        return isBanned;
    }

    private boolean isValidIpCount(String ip) throws RefusedAddressException {
        boolean ok = false;
        AtomicInteger count = (AtomicInteger)this.addressMap.get(ip);
        if (count == null) {
            count = new AtomicInteger(0);
            ok = true;
        } else if (count.intValue() < this.maxConnectionsPerIp) {
            count.incrementAndGet();
            ok = true;
        }
        return ok;
    }

    @Override
    public String getGhostList() {
        return Arrays.toString(this.ghostList.toArray());
    }
}

