/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  com.thoughtworks.xstream.XStream
 *  com.thoughtworks.xstream.io.HierarchicalStreamDriver
 *  com.thoughtworks.xstream.io.xml.DomDriver
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 *  org.joda.time.DateTime
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.config;

import bitzero.server.config.CoreSettings;
import bitzero.server.config.IConfigurator;
import bitzero.server.config.ServerSettings;
import bitzero.server.config.ZoneSettings;
import bitzero.server.exceptions.BZException;
import bitzero.server.exceptions.BZRuntimeException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vinplay.vbee.common.config.VBeePath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BZConfigurator
implements IConfigurator {
    private final String BACKUP_FOLDER = "_backups";
    private volatile CoreSettings coreSettings;
    private volatile ServerSettings serverSettings;
    private volatile List<ZoneSettings> zonesSettings;
    private final Logger log;

    public BZConfigurator() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void loadConfiguration() throws FileNotFoundException {
        this.coreSettings = this.loadCoreSettings();
        this.serverSettings = this.loadServerSettings();
        if (this.serverSettings.webSocket == null) {
            this.serverSettings.webSocket = new ServerSettings.WebSocketEngineSettings();
        }
    }

    @Override
    public CoreSettings getCoreSettings() {
        return this.coreSettings;
    }

    @Override
    public synchronized ServerSettings getServerSettings() {
        return this.serverSettings;
    }

    @Override
    public synchronized List getZoneSettings() {
        return this.zonesSettings;
    }

    @Override
    public synchronized ZoneSettings getZoneSetting(String zoneName) {
        if (this.zonesSettings == null) {
            throw new IllegalStateException("No Zone configuration has been loaded yet!");
        }
        ZoneSettings settings = null;
        for (ZoneSettings item : this.zonesSettings) {
            if (!item.name.equals(zoneName)) continue;
            settings = item;
            break;
        }
        return settings;
    }

    @Override
    public synchronized ZoneSettings getZoneSetting(int id) {
        if (this.zonesSettings == null) {
            throw new IllegalStateException("No Zone configuration has been loaded yet!");
        }
        ZoneSettings settings = null;
        for (ZoneSettings item : this.zonesSettings) {
            if (item.getId() != id) continue;
            settings = item;
            break;
        }
        return settings;
    }

    @Override
    public synchronized void removeZoneSetting(String name) throws IOException {
        ZoneSettings settings = this.getZoneSetting(name);
        if (settings != null) {
            String path = FilenameUtils.concat((String)"zones/", (String)(String.valueOf(settings.name) + ".zone.xml"));
            this.makeBackup(path);
            FileUtils.forceDelete((File)new File(path));
            this.zonesSettings.remove(settings);
        }
    }

    @Override
    public synchronized List loadZonesConfiguration() throws BZException {
        this.zonesSettings = new ArrayList();
        List<File> zoneDefinitionFiles = this.getZoneDefinitionFiles("zones/");
        for (File file : zoneDefinitionFiles) {
            try {
                FileInputStream inStream = new FileInputStream(file);
                this.log.info("Loading: " + file.toString());
                this.zonesSettings.add((ZoneSettings)this.getZonesXStreamDefinitions().fromXML((InputStream)inStream));
                continue;
            }
            catch (FileNotFoundException e) {
                throw new BZRuntimeException("Could not locate Zone definition file: " + file.getAbsolutePath());
            }
        }
        return this.zonesSettings;
    }

    @Override
    public synchronized void saveServerSettings(boolean makeBackup) throws IOException {
        if (makeBackup) {
            this.makeBackup("config/server.xml");
        }
        FileOutputStream outStream = new FileOutputStream("config/server.xml");
        this.getServerXStreamDefinitions().toXML((Object)this.serverSettings, (OutputStream)outStream);
    }

    @Override
    public synchronized void saveZoneSettings(ZoneSettings settings, boolean makeBackup) throws IOException {
        String filePath = FilenameUtils.concat((String)"zones/", (String)(String.valueOf(settings.name) + ".zone.xml"));
        if (makeBackup) {
            this.makeBackup(filePath);
        }
        FileOutputStream outStream = new FileOutputStream(filePath);
        this.getZonesXStreamDefinitions().toXML((Object)settings, (OutputStream)outStream);
    }

    @Override
    public synchronized void saveNewZoneSettings(ZoneSettings settings) throws IOException {
        if (this.getZoneSetting(settings.name) != null) {
            throw new IllegalArgumentException("Save request failed. The new Zone name is already in use: " + settings.name);
        }
        this.saveZoneSettings(settings, false);
        this.zonesSettings.add(settings);
    }

    @Override
    public synchronized void saveZoneSettings(ZoneSettings zoneSettings, boolean makeBackup, String oldZoneName) throws IOException {
        String newFilePath = FilenameUtils.concat((String)"zones/", (String)(String.valueOf(zoneSettings.name) + ".zone.xml"));
        String oldFilePath = FilenameUtils.concat((String)"zones/", (String)(String.valueOf(oldZoneName) + ".zone.xml"));
        if (makeBackup) {
            this.makeBackup(oldFilePath);
        }
        FileOutputStream outStream = new FileOutputStream(newFilePath);
        this.getZonesXStreamDefinitions().toXML((Object)zoneSettings, (OutputStream)outStream);
        FileUtils.forceDelete((File)new File(oldFilePath));
    }

    private CoreSettings loadCoreSettings() throws FileNotFoundException {
        FileInputStream inStream = new FileInputStream(VBeePath.basePath.concat("config/core.xml"));
        XStream xstream = new XStream((HierarchicalStreamDriver)new DomDriver());
        xstream.alias("coreSettings", CoreSettings.class);
        return (CoreSettings)xstream.fromXML((InputStream)inStream);
    }

    private ServerSettings loadServerSettings() throws FileNotFoundException {
        FileInputStream inStream = new FileInputStream(VBeePath.basePath.concat("config/server.xml"));
        return (ServerSettings)this.getServerXStreamDefinitions().fromXML((InputStream)inStream);
    }

    private XStream getServerXStreamDefinitions() {
        XStream xstream = new XStream((HierarchicalStreamDriver)new DomDriver());
        xstream.alias("serverSettings", ServerSettings.class);
        xstream.alias("socket", ServerSettings.SocketAddress.class);
        xstream.useAttributeFor(ServerSettings.SocketAddress.class, "address");
        xstream.useAttributeFor(ServerSettings.SocketAddress.class, "port");
        xstream.useAttributeFor(ServerSettings.SocketAddress.class, "type");
        xstream.alias("ipFilter", ServerSettings.IpFilterSettings.class);
        xstream.alias("flashCrossdomainPolicy", ServerSettings.FlashCrossDomainPolicySettings.class);
        xstream.alias("remoteAdmin", ServerSettings.RemoteAdminSettings.class);
        xstream.alias("adminUser", ServerSettings.AdminUser.class);
        xstream.alias("mailer", ServerSettings.MailerSettings.class);
        xstream.alias("webServer", ServerSettings.WebServerSettings.class);
        xstream.alias("bannedUserManager", ServerSettings.BannedUserManagerSettings.class);
        return xstream;
    }

    private XStream getZonesXStreamDefinitions() {
        XStream xstream = new XStream((HierarchicalStreamDriver)new DomDriver());
        return xstream;
    }

    private List getZoneDefinitionFiles(String path) throws BZException {
        ArrayList<File> files = new ArrayList<File>();
        File currDir = new File(path);
        if (currDir.isDirectory()) {
            for (File f : currDir.listFiles()) {
                if (!f.getName().endsWith(".zone.xml")) continue;
                files.add(f);
            }
        } else {
            throw new BZException("Invalid zones definition folder: " + currDir);
        }
        return files;
    }

    private void makeBackup(String filePath) throws IOException {
        String basePath = FilenameUtils.getPath((String)filePath);
        String backupBasePath = FilenameUtils.concat((String)basePath, (String)"_backups");
        DateTimeFormatter fmt = DateTimeFormat.forPattern((String)"yyyy-MM-dd-HH-mm-ss");
        String backupId = new DateTime().toString(fmt);
        String backupFileName = FilenameUtils.concat((String)backupBasePath, (String)(String.valueOf(backupId) + "__" + FilenameUtils.getName((String)filePath)));
        File sourceFile = new File(filePath);
        File backupFile = new File(backupFileName);
        File backupDir = new File(backupBasePath);
        if (!backupDir.isDirectory()) {
            FileUtils.forceMkdir((File)backupDir);
        }
        FileUtils.copyFile((File)sourceFile, (File)backupFile);
    }
}

