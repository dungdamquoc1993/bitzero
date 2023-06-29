/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.util.EntityUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.util.socialcontroller.business.zm;

import bitzero.util.common.business.CommonHandle;
import bitzero.util.socialcontroller.business.zm.ZM_Config;
import bitzero.util.socialcontroller.exceptions.SocialControllerException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ZM_API_Common {
    private String subApi = "";
    protected String api = "";
    protected String secret = "";
    private final String VERSION = "2.0";
    private ZM_Config config;
    private final Logger logger;

    public ZM_API_Common(ZM_Config config, String subApi) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.config = config;
        this.subApi = subApi;
        this.secret = config.secret;
        this.setAPI(config.apiURL);
    }

    public String getAPI() {
        return this.api;
    }

    public void setAPI(String api) {
        this.api = api + this.subApi;
    }

    public String callMethod(String method, Map<String, String> args) throws SocialControllerException {
        this.updateArgs(method, args);
        String respone = this.sendRequest(args);
        String result = this.parseRespone(respone);
        return result;
    }

    protected static String encryptMD5(String input) {
        byte[] defaultBytes = input.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte[] messageDigest = algorithm.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; ++i) {
                String hex = Integer.toHexString(255 & messageDigest[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString + "";
        }
        catch (NoSuchAlgorithmException nsae) {
            CommonHandle.writeErrLog(nsae);
            return null;
        }
    }

    public void updateArgs(String method, Map<String, String> args) {
        args.put("api_key", this.config.apiKey);
        args.put("v", "2.0");
        args.put("method", method);
        long now = System.nanoTime();
        long ms = now / 1000;
        long nano = now % 1000;
        args.put("call_id", "" + ms + "." + nano);
        this.signRequest(args);
    }

    public void signRequest(Map<String, String> args) {
        if (args.containsKey("sig")) {
            args.remove("sig");
        }
        String sig = "";
        Object[] arrKey = args.keySet().toArray();
        Arrays.sort(arrKey);
        for (int i = 0; i < arrKey.length; ++i) {
            String k = (String)arrKey[i];
            sig = sig + k + "=" + args.get(k);
        }
        sig = sig + this.secret;
        args.put("sig", ZM_API_Common.encryptMD5(sig));
    }

    public String sendRequest(Map<String, String> args) throws SocialControllerException {
        String urlcoded = null;
        String result = null;
        try {
            urlcoded = ZM_API_Common.encode(args);
            this.logger.info("url link : " + this.api + "?" + urlcoded);
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(this.api + "?" + urlcoded);
            HttpResponse response = httpclient.execute((HttpUriRequest)httppost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString((HttpEntity)entity, (String)"UTF-8");
            this.logger.info(" response from Zing Me : " + result);
            return result;
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            throw new SocialControllerException(-1, " loi ");
        }
    }

    protected static String encode(Map<String, String> args) {
        String result = "";
        Object[] arrKey = args.keySet().toArray();
        Arrays.sort(arrKey);
        for (int i = 0; i < arrKey.length; ++i) {
            String k = (String)arrKey[i];
            try {
                result = result + k + "=" + URLEncoder.encode(args.get(k), "UTF-8") + "&";
                continue;
            }
            catch (UnsupportedEncodingException e) {
                CommonHandle.writeErrLog(e);
            }
        }
        result = ZM_API_Common.removeCharAt(result, result.length() - 1);
        return result;
    }

    private static String removeCharAt(String s, int pos) {
        StringBuilder buf = new StringBuilder(s.length() - 1);
        buf.append(s.substring(0, pos)).append(s.substring(pos + 1));
        return buf.toString();
    }

    private String parseRespone(String respone) throws SocialControllerException {
        try {
            JSONObject data = new JSONObject(respone);
            return data.getString("data");
        }
        catch (JSONException e) {
            throw new SocialControllerException(-2, " loi ");
        }
    }
}

