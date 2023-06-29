/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.cache;

import java.util.Collection;
import java.util.Map;

public interface KCache<K, V> {
    public void put(K var1, V var2, int var3);

    public V get(K var1);

    public Map<K, V> getAll(Collection<K> var1);

    public void clear();

    public boolean remove(K var1);

    public V removeAndGet(K var1);

    public int size();
}

