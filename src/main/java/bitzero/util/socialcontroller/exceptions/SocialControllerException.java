/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.socialcontroller.exceptions;

public class SocialControllerException
extends Exception {
    public int error = 0;
    public String message = "";

    public SocialControllerException(int error, String message) {
        this.error = error;
        this.message = message;
    }
}

