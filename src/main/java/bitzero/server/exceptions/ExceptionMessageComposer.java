/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.engine.util.Logging;
import java.util.ArrayList;
import java.util.List;

public class ExceptionMessageComposer {
    private static final String NEW_LINE = System.getProperty("line.separator");
    public static volatile boolean globalPrintStackTrace = true;
    public static volatile boolean useExtendedMessages = true;
    private String mainErrorMessage;
    private String exceptionType;
    private String description;
    private String possibleCauses;
    private String stackTrace;
    private List<String> additionalInfos;
    private StringBuilder buf;

    public ExceptionMessageComposer(Throwable t) {
        this(t, globalPrintStackTrace);
    }

    public ExceptionMessageComposer(Throwable t, boolean printStackTrace) {
        this.mainErrorMessage = t.getMessage();
        if (this.mainErrorMessage == null) {
            this.mainErrorMessage = "*** Null ***";
        }
        this.exceptionType = t.getClass().getName();
        this.buf = new StringBuilder();
        if (printStackTrace) {
            this.setStackTrace(t);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPossibleCauses(String possibleCauses) {
        this.possibleCauses = possibleCauses;
    }

    private void setStackTrace(Throwable t) {
        this.stackTrace = Logging.formatStackTrace(t.getStackTrace());
    }

    public void addInfo(String infoMessage) {
        if (this.additionalInfos == null) {
            this.additionalInfos = new ArrayList();
        }
        this.additionalInfos.add(infoMessage);
    }

    public String toString() {
        if (!useExtendedMessages) {
            this.buf.append(this.exceptionType).append(" ").append(this.mainErrorMessage);
            return this.buf.toString();
        }
        this.buf.append(NEW_LINE);
        this.buf.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::").append(NEW_LINE);
        this.buf.append("Exception: ").append(this.exceptionType).append(NEW_LINE);
        this.buf.append("Message: ").append(this.mainErrorMessage).append(NEW_LINE);
        if (this.description != null) {
            this.buf.append("Description: ").append(this.description).append(NEW_LINE);
        }
        if (this.possibleCauses != null) {
            this.buf.append("Possible Causes: ").append(this.possibleCauses).append(NEW_LINE);
        }
        if (this.additionalInfos != null) {
            for (String info : this.additionalInfos) {
                this.buf.append(info).append(NEW_LINE);
            }
        }
        if (this.stackTrace != null) {
            this.buf.append("+--- --- ---+").append(NEW_LINE);
            this.buf.append("Stack Trace:").append(NEW_LINE);
            this.buf.append("+--- --- ---+").append(NEW_LINE);
            this.buf.append(this.stackTrace);
            this.buf.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::").append(NEW_LINE);
        } else {
            this.buf.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::").append(NEW_LINE);
        }
        return this.buf.toString();
    }
}

