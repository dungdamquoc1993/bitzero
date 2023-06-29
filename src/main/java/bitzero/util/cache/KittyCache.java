package bitzero.util.cache;

import bitzero.util.common.business.CommonHandle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class KittyCache implements KCache {
    private Map cache;
    private Queue queue;
    private int maxSize;
    private AtomicInteger cacheSize = new AtomicInteger();

    public KittyCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new ConcurrentHashMap(maxSize);
        this.queue = new ConcurrentLinkedQueue();
    }

    public Object get(Object key) {
        if (key == null) {
            return null;
        } else {
            CacheEntry entry = (CacheEntry)this.cache.get(key);
            if (entry == null) {
                return null;
            } else {
                long timestamp = entry.getExpireBy();
                if (timestamp != -1L && System.currentTimeMillis() > timestamp) {
                    this.remove(key);
                    return null;
                } else {
                    return entry.getEntry();
                }
            }
        }
    }

    public Object removeAndGet(Object key) {
        if (key == null) {
            return null;
        } else {
            CacheEntry entry = (CacheEntry)this.cache.get(key);
            if (entry != null) {
                this.cacheSize.decrementAndGet();
                return ((CacheEntry)this.cache.remove(key)).getEntry();
            } else {
                return null;
            }
        }
    }

    public void put(Object key, Object value, int secondsToLive) {
        if (key != null) {
            if (value != null) {
                long expireBy = secondsToLive != -1 ? System.currentTimeMillis() + (long)(secondsToLive * 1000) : (long)secondsToLive;
                boolean exists = this.cache.containsKey(key);
                if (!exists) {
                    this.cacheSize.incrementAndGet();
                    if (this.cacheSize.get() > this.maxSize) {
                        this.checkExpireAllKey();
                    }
                }

                this.cache.put(key, new CacheEntry(expireBy, value));
            }
        }
    }

    public void checkExpireAllKey() {
        try {
            System.out.println("checkExpireAllKey : cacheSize :" + this.cacheSize.get() + " cache : " + this.cache.size() + " queue: " + this.queue.size());
            Set keySet = this.cache.keySet();
            Iterator keySetIterator = keySet.iterator();

            while(keySetIterator.hasNext()) {
                Object key = keySetIterator.next();
                this.get(key);
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            System.out.println("Time :" + dateFormat.format(Calendar.getInstance().getTime()) + " End checkExpireAllKey : cacheSize :" + this.cacheSize.get() + "cache : " + this.cache.size() + " queue: " + this.queue.size());
        } catch (Exception var4) {
            CommonHandle.writeErrLog((Throwable)var4);
        }

    }

    public boolean remove(Object key) {
        return this.removeAndGet(key) != null;
    }

    public int size() {
        return this.cacheSize.get();
    }

    public Map getAll(Collection collection) {
        Map ret = new HashMap();
        Iterator var3 = collection.iterator();

        while(var3.hasNext()) {
            Object o = var3.next();
            ret.put(o, this.get(o));
        }

        return ret;
    }

    public void clear() {
        this.cache.clear();
    }

    public int mapSize() {
        return this.cache.size();
    }

    public int queueSize() {
        return this.queue.size();
    }

    private class CacheEntry {
        private long expireBy;
        private Object entry;

        public CacheEntry(long expireBy, Object entry) {
            this.expireBy = expireBy;
            this.entry = entry;
        }

        public long getExpireBy() {
            return this.expireBy;
        }

        public Object getEntry() {
            return this.entry;
        }
    }
}
