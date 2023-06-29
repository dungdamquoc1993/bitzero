/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.exception.ExceptionUtils
 *  org.apache.scribe.LogEntry
 *  org.apache.scribe.ResultCode
 *  org.apache.scribe.scribe
 *  org.apache.scribe.scribe$Client
 *  org.apache.thrift.TException
 *  org.apache.thrift.protocol.TBinaryProtocol
 *  org.apache.thrift.protocol.TProtocol
 *  org.apache.thrift.transport.TFramedTransport
 *  org.apache.thrift.transport.TSocket
 *  org.apache.thrift.transport.TTransport
 *  org.apache.thrift.transport.TTransportException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.util.logcontroller.business.scribe;

import bitzero.server.config.ConfigHandle;
import bitzero.util.common.business.CommonHandle;
import bitzero.util.common.business.Debug;
import bitzero.util.config.bean.ConstantMercury;
import bitzero.util.logcontroller.business.ILogController;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.scribe.LogEntry;
import org.apache.scribe.ResultCode;
import org.apache.scribe.scribe;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScribeLogController
implements ILogController,
Runnable {
    public static final boolean IS_METRICLOG = ConfigHandle.instance().getLong("isMetriclog") == 1;
    protected TSocket socket = new TSocket(ConfigHandle.instance().get("lservers"), ConstantMercury.SCRIBE_PORT, 30);
    protected TTransport transport = new TFramedTransport((TTransport)this.socket);
    protected TProtocol protocol = new TBinaryProtocol(this.transport, false, false);
    protected scribe.Client client = new scribe.Client(this.protocol, this.protocol);
    protected List<LogEntry> entrys = new ArrayList<LogEntry>();
    protected BlockingQueue requestQueue = new LinkedBlockingQueue();
    protected ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private final Logger logger = LoggerFactory.getLogger((String)"scriber");
    private volatile boolean isActive = IS_METRICLOG;
    private volatile int Num = 0;

    public ScribeLogController() {
        this.threadPool.execute(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeLog(ILogController.LogMode mode, String data) {
        if (!IS_METRICLOG) {
            return;
        }
        this.requestQueue.add(new LogEntry(ConfigHandle.instance().get(mode.value() + "_log_category"), data));
        if (this.Num > 36000) {
            List<LogEntry> list = this.entrys;
            synchronized (list) {
                this.requestQueue.clear();
            }
            this.Num = 0;
        }
    }

    public Boolean flushAll_backup() {
        if (this.entrys.isEmpty()) {
            return true;
        }
        try {
            if (!this.transport.isOpen()) {
                this.transport.open();
            }
            this.client.Log(this.entrys);
            this.entrys.clear();
            this.transport.close();
            this.Num = 0;
            return true;
        }
        catch (TTransportException e) {
            CommonHandle.writeWarnLog((Throwable)e);
            ++this.Num;
            return false;
        }
        catch (TException e) {
            CommonHandle.writeWarnLog((Throwable)e);
            ++this.Num;
            return false;
        }
        catch (Exception e) {
            CommonHandle.writeWarnLog(e);
            ++this.Num;
            return false;
        }
    }

    public Boolean flushAll() {
        if (this.entrys.isEmpty()) {
            return true;
        }
        try {
            if (!this.transport.isOpen()) {
                this.transport.open();
            }
            this.client.Log(this.entrys);
            this.entrys.clear();
            this.transport.close();
            this.Num = 0;
            return true;
        }
        catch (Exception e) {
            Debug.warn(ExceptionUtils.getStackTrace((Throwable)e));
            ++this.Num;
            String category = ConfigHandle.instance().get("local_category");
            if (category != null && !category.equals("")) {
                this.writeLocalLog(category, this.entrys);
            }
            this.entrys.clear();
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void writeLocalLog(String category, List<LogEntry> entrys) {
        BufferedWriter writer = null;
        try {
            String timeLog = new SimpleDateFormat("yyyyMMdd_HH").format(Calendar.getInstance().getTime());
            for (int i = 0; i < entrys.size(); ++i) {
                new File(category + entrys.get(i).getCategory() + "/").mkdirs();
                File logFile = new File(category + entrys.get(i).getCategory() + "/log-" + timeLog + ".txt");
                writer = new BufferedWriter(new FileWriter(logFile, true));
                writer.write(entrys.get(i).getMessage());
                writer.newLine();
                writer.close();
            }
        }
        catch (Exception e) {
            Debug.warn("WRITE LOCAL LOG EXCEPTION");
            Debug.warn(ExceptionUtils.getStackTrace((Throwable)e));
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {}
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Scriber Log");
        while (this.isActive) {
            try {
                LogEntry entry = (LogEntry)this.requestQueue.take();
                this.entrys.add(entry);
                this.flushAll();
            }
            catch (Throwable t) {
                this.logger.warn("Problems in Scriber Log main loop: " + t + ", Thread: " + Thread.currentThread());
            }
        }
        System.out.print("ScribeLogController threadpool shutting down.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeLog(String mode, String data) {
        if (!IS_METRICLOG) {
            return;
        }
        String catalog = ConfigHandle.instance().get(mode);
        if (catalog == null || catalog.length() < 2) {
            catalog = mode;
        }
        this.requestQueue.add(new LogEntry(catalog, data));
        if (this.Num > 36000) {
            List<LogEntry> list = this.entrys;
            synchronized (list) {
                this.requestQueue.clear();
            }
            this.Num = 0;
        }
    }

    @Override
    public void writeLog(ILogController.LogMode mode, String data, String folderName) {
    }
}

