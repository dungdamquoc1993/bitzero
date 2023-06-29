/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.IErrorCode;
import java.util.ArrayList;
import java.util.List;

public class BZErrorData {
    IErrorCode code;
    List params;

    public BZErrorData(IErrorCode code) {
        this.code = code;
        this.params = new ArrayList();
    }

    public IErrorCode getCode() {
        return this.code;
    }

    public void setCode(IErrorCode code) {
        this.code = code;
    }

    public List getParams() {
        return this.params;
    }

    public void setParams(List params) {
        this.params = params;
    }

    public void addParameter(String parameter) {
        this.params.add(parameter);
    }
}

