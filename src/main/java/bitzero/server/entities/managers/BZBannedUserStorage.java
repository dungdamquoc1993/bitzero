/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities.managers;

import bitzero.server.entities.managers.BanUserData;
import bitzero.server.entities.managers.IBannedUserStorage;
import bitzero.server.exceptions.ExceptionMessageComposer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZBannedUserStorage
implements IBannedUserStorage {
    private static final String DATA_FOLDER = "data/bannedusers/";
    private static final String DATA_FILE = "data/bannedusers/users.bin";
    private final Logger log;
    private volatile boolean isProperlyInited;

    public BZBannedUserStorage() {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.isProperlyInited = false;
    }

    @Override
    public void init() {
        try {
            this.checkFolderStructure();
            this.isProperlyInited = true;
            this.log.info("BanUserStorage initialized");
        }
        catch (IOException err) {
            ExceptionMessageComposer emc = new ExceptionMessageComposer(err);
            emc.setDescription("Unable to initialize the Banned User Manage storage.");
            File fullPath = new File("data/bannedusers/");
            String thePath = fullPath == null ? "Unable to obtain path. Your installation might be corrupted." : fullPath.getAbsolutePath();
            emc.setPossibleCauses("Write permissions are probably not available in: " + thePath);
            if (fullPath == null) {
                emc.addInfo("An additional problem was found: BitZeroServer is not able to determine the absolute path of the storage folder. Should be {your-bitzero-folder}/data/bannedusers/");
            }
            this.log.warn(emc.toString());
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public BanUserData load() throws Exception {
        this.checkInited();
        FileInputStream fileInStream = new FileInputStream(DATA_FILE);
        ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
        BanUserData banUserData = (BanUserData)objInStream.readObject();
        objInStream.close();
        return banUserData;
    }

    @Override
    public synchronized void save(BanUserData data) throws IOException {
        this.checkInited();
        FileOutputStream fileOStream = new FileOutputStream(DATA_FILE);
        ObjectOutputStream oStream = new ObjectOutputStream(fileOStream);
        oStream.writeObject(data);
        oStream.flush();
        oStream.close();
    }

    private void checkFolderStructure() throws IOException {
        File targetFolder = new File(DATA_FOLDER);
        if (!targetFolder.isDirectory()) {
            FileUtils.forceMkdir((File)targetFolder);
        }
    }

    private void checkInited() {
        if (!this.isProperlyInited) {
            throw new IllegalStateException("Banned User storage class cannot operate correctly because initialization failed. Please check your startup logs. ");
        }
    }
}

