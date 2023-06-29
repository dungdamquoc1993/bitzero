/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 */
package bitzero.server.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public final class FlashMasterSocketPolicyLoader {
    public String loadPolicy(String fileName) throws IOException {
        return FileUtils.readFileToString((File)new File(fileName));
    }
}

