/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core.security;

import bitzero.engine.exceptions.RefusedAddressException;

public interface IConnectionFilter {
    public void addBannedAddress(String var1);

    public void removeBannedAddress(String var1);

    public String[] getBannedAddresses();

    public void validateAndAddAddress(String var1) throws RefusedAddressException;

    public String getGhostList();

    public void removeAddress(String var1);

    public void addWhiteListAddress(String var1);

    public void removeWhiteListAddress(String var1);

    public String[] getWhiteListAddresses();

    public int getMaxConnectionsPerIp();

    public void setMaxConnectionsPerIp(int var1);
}

