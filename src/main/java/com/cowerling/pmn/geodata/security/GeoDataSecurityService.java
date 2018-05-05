package com.cowerling.pmn.geodata.security;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class GeoDataSecurityService {
    public static boolean login(String name, String password) {
        try {
            HttpClient httpClient = HttpClientBuilder
                    .create()
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .build();

            HttpPost httpPost = new HttpPost("http://127.0.0.1:8081/geoserver/j_spring_security_check");

            httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("username", "admin"));
            urlParameters.add(new BasicNameValuePair("password", "Cowerling2010"));
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpGet httpGet = new HttpGet("http://127.0.0.1:8081/geoserver/pmn/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=pmn:city_point&maxFeatures=50");
            HttpResponse httpResponse2 = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse2.getEntity();
            String en = EntityUtils.toString(entity, "UTF-8");

            return httpResponse.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
