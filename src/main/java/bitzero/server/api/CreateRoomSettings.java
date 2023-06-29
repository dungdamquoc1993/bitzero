/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.api;

import bitzero.server.entities.BZRoomRemoveMode;

public class CreateRoomSettings {
    private String name = null;
    private String groupId = "default";
    private String password = null;
    private int maxUsers = 4;
    private int maxSpectators = 0;
    private boolean isDynamic = true;
    private boolean isGame = false;
    private boolean isHidden = false;
    private BZRoomRemoveMode autoRemoveMode = BZRoomRemoveMode.WHEN_EMPTY;
    private Class customPlayerIdGeneratorClass;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxUsers() {
        return this.maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getMaxSpectators() {
        return this.maxSpectators;
    }

    public void setMaxSpectators(int maxSpectators) {
        this.maxSpectators = maxSpectators;
    }

    public boolean isDynamic() {
        return this.isDynamic;
    }

    public void setDynamic(boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public boolean isGame() {
        return this.isGame;
    }

    public void setGame(boolean isGame) {
        this.isGame = isGame;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public BZRoomRemoveMode getAutoRemoveMode() {
        return this.autoRemoveMode;
    }

    public void setAutoRemoveMode(BZRoomRemoveMode autoRemoveMode) {
        this.autoRemoveMode = autoRemoveMode;
    }

    public Class getCustomPlayerIdGeneratorClass() {
        return this.customPlayerIdGeneratorClass;
    }

    public void setCustomPlayerIdGeneratorClass(Class customPlayerIdGeneratorClass) {
        this.customPlayerIdGeneratorClass = customPlayerIdGeneratorClass;
    }
}

