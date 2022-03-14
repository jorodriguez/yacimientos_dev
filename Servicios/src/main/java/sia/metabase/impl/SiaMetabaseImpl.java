/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.metabase.impl;

import io.jsonwebtoken.Jwts;
import java.util.Base64;
import java.util.Date;
import javax.ejb.LocalBean;
import org.bouncycastle.crypto.tls.SignatureAlgorithm;
import org.jose4j.json.internal.json_simple.JSONObject;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class SiaMetabaseImpl  {
    
    private static final String BI_SITE_URL = "https://sia.ihsa.mx/bi";


    
    public String getTokenUrl(Integer dashboard, String secretKey) throws Exception {
        int round = Math.round(System.currentTimeMillis() / 1000) + 10 * 60; // 10 minute expiration
        
        JSONObject resource = new JSONObject();
        resource.put("question", dashboard);
        
        JSONObject payload = new JSONObject();
        payload.put("resource", resource);
        payload.put("params", new JSONObject());
        payload.put("exp", round);
        
        String token = createJWT(payload, secretKey);
    
        return BI_SITE_URL + "/embed/question/" + token + "#bordered=false&titled=false";
    }
    
    
    public String getTokenUrlDash(Integer dashboard, String secretKey) throws Exception {
        int round = Math.round(System.currentTimeMillis() / 1000) + 10 * 60; // 10 minute expiration
        
        JSONObject resource = new JSONObject();
        resource.put("dashboard", dashboard);
        
        JSONObject payload = new JSONObject();
        payload.put("resource", resource);
        payload.put("params", new JSONObject());
        payload.put("exp", round);
        
        String token = createJWT(payload, secretKey);
    
        return BI_SITE_URL + "/embed/dashboard/" + token + "#bordered=false&titled=false";
    }

    
    private String createJWT(JSONObject payload, String secretKey) {
        String retVal = null;
        
        try {
            String metaBaseEncodedSecretKey = 
                    Base64.getEncoder().encodeToString(secretKey.getBytes());
        
            retVal = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setClaims(payload)
                    .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, metaBaseEncodedSecretKey)
                    .setIssuedAt(new Date())
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return retVal;
    }

}
