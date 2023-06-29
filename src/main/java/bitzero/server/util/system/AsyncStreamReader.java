/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util.system;

import bitzero.server.util.system.ILogDevice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

class AsyncStreamReader
extends Thread {
    private StringBuffer fBuffer = null;
    private InputStream fInputStream = null;
    private String fThreadId = null;
    private boolean fStop = false;
    private ILogDevice fLogDevice = null;
    private String fNewLine = null;

    public AsyncStreamReader(InputStream inputStream, StringBuffer buffer, ILogDevice logDevice, String threadId) {
        this.fInputStream = inputStream;
        this.fBuffer = buffer;
        this.fThreadId = threadId;
        this.fLogDevice = logDevice;
        this.fNewLine = System.getProperty("line.separator");
    }

    public String getBuffer() {
        return this.fBuffer.toString();
    }

    @Override
    public void run() {
        try {
            this.readCommandOutput();
        }
        catch (Exception var1_1) {
            // empty catch block
        }
    }

    private void readCommandOutput() throws IOException {
        BufferedReader bufOut = new BufferedReader(new InputStreamReader(this.fInputStream));
        String line = null;
        while (!this.fStop && (line = bufOut.readLine()) != null) {
            this.fBuffer.append(line + this.fNewLine);
            this.printToDisplayDevice(line);
        }
        bufOut.close();
    }

    public void stopReading() {
        this.fStop = true;
    }

    private void printToDisplayDevice(String line) {
        if (this.fLogDevice != null) {
            this.fLogDevice.log(line);
        }
    }

    private synchronized void printToConsole(String line) {
        System.out.println(line);
    }
}

