/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.socialcontroller.bean;

import bitzero.server.util.random.RandomAccess;
import java.io.Serializable;

public class UserInfo
implements Serializable {
    private static final long serialVersionUID = -2146846609376921755L;
    protected String userid = "";
    protected String username = "";
    protected String firstname = "";
    protected String gender = "";
    protected String lastname = "";
    protected String displayname = "";
    protected String tinyurl = "";
    protected String headurl = "";
    protected String profile_url = "";
    protected String dob = "";
    protected String status = "";
    private String trackingSource = "";
    private String openId = "";
    private String platform = "";

    public void randomData() {
        int i = RandomAccess.randomInt() % 100000;
        if (i < 0) {
            i = - i;
        }
        this.userid = String.valueOf(i);
        this.username = "gsn_" + this.userid;
    }

    public void setUserId(String userid) {
        this.userid = userid;
    }

    public String getUserId() {
        return this.userid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return this.gender;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getDisplayname() {
        return this.displayname;
    }

    public void setTinyurl(String tinyurl) {
        this.tinyurl = tinyurl;
    }

    public String getTinyurl() {
        return this.tinyurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getHeadurl() {
        return this.headurl;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getProfile_url() {
        return this.profile_url;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDob() {
        return this.dob;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public String getUsername() {
        return this.username;
    }

    public String getTrackingSource() {
        return this.trackingSource;
    }

    public void setTrackingSource(String trackingSource) {
        this.trackingSource = trackingSource;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return this.platform;
    }
}

