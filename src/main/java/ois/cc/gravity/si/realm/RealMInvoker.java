/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.si.realm;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import code.realm.fw.events.EventRealmException;
import code.realm.fw.util.FWUtil;
import code.ua.events.Event;
import static code.ua.events.EventType.Error;
import static code.ua.events.EventType.Failed;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.services.exceptions.GravityUnhandledRealMException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Manoj-PC
 * @since May 24, 2024
 */
public class RealMInvoker
{

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private Integer retryCount = 0;
    private final Integer retryLimit = 3;

    public String SendToRealMSerice(String url, String restmthd, JSONObject body, String token) throws GravityUnhandledException
    {
        String reqToken = token == null ? "" : token;
        return invokeAPI(url, body, restmthd, reqToken);
    }

    public Event SendToRealMServiceRequest(String url, String restmthd, code.ua.requests.Request body, String token) throws GravityUnhandledException
    {
        String reqToken = token == null ? "" : token;
        return invokeAPIRequest(url, body, restmthd, reqToken);
    }

    public String SendToRealMSericeForAuth(String url, String restmthd, JSONObject body, String token) throws GravityUnhandledRealMException
    {
        String reqToken = token == null ? "" : token;
        try
        {
            okhttp3.Request.Builder reqBldr = new okhttp3.Request.Builder().url(url);
            String reqStr = null;
            if (logger.isDebugEnabled() && body != null)
            {
                reqStr = JSONUtil.ToJSON(body).toString();
            }
            logger.trace("INVOKE RealM API\nURL>> " + url + "\nPOST Body>> " + reqStr);

            // Convert JSONObject to x-www-form-urlencoded string
            StringBuilder formBody = new StringBuilder();
            for (String key : body.keySet())
            {
                if (formBody.length() > 0)
                {
                    formBody.append("&");
                }
                formBody.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
                formBody.append("=");
                formBody.append(URLEncoder.encode(String.valueOf(body.get(key)), StandardCharsets.UTF_8));
            }

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

            RequestBody reqbody = RequestBody.create(mediaType, formBody.toString());
            reqBldr.method(restmthd, reqbody);
            okhttp3.Request request = reqBldr.addHeader("Content-Type", "application/x-www-form-urlencoded")
                    //                    .addHeader("Authorization", basicAuth)
                    .build();

            Long timeout = 60L;
            OkHttpClient.Builder clntBldr = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS);
            //ignore ssl.
            AllowUntrusted(clntBldr);

            OkHttpClient client = clntBldr.build();
            logger.debug("RealmRequest >> " + JSONUtil.ToJSON(request).toString());
            Response response = client.newCall(request).execute();
//            if (response.code() == 401)
//            {
//                logger.debug("Got 401 error from REALM so relogin and resending REALMRequest");
//
//                if (retryCount < retryLimit)
//                {
//                    ALMServerContext sctx = ServerContext.This().getALMCtx();
//                    String newtoken = sctx.regenerateToken(token);
//                    retryCount++;
//                    invokeAPI(url, body, restmthd, newtoken);
//                }
//                retryCount = 0;
//
//            }
            if (response.isSuccessful())
            {
                String respStr = response.body().string();
                logger.trace("Realm Response Body << " + respStr);
                return respStr;
//                Event ev = (Event) FWUtil.Deserialize(respStr);
//                if (logger.isDebugEnabled())
//                {
//                    logger.trace("Response Body << " + JSONUtil.ToJSON(ev));
//                }
//                switch (ev.getEvType())
//                {
//                    case Error, Failed ->
//                        throw new RADUnhandledRealMException(ev);
//                }
//                if (ev.getEvTypeApp() != null && ev.getEvTypeApp().name().equals(code.realm.fw.events.EventType.Failed.name()))
//                {
//
//                    throw new RADUnhandledRealMException(ev);
//                }
//                if (ev.getEvCodeApp() != null && ev.getEvCodeApp().equals(code.realm.fw.events.EventCode.RealmException))
//                {
//                    EventRealmException evEx = (EventRealmException) ev;
//                    throw new RADUnhandledRealMException(evEx);
//                }
//                return ev;

            }
            else
            {
                //TBD Through an appropeate exception
                String respStr = response.body().string();
                logger.debug("Realm Response <<  : " + respStr);
                Event ev = (Event) FWUtil.Deserialize(respStr);
                throw new GravityUnhandledRealMException(ev);
            }
        }
        catch (Exception | GravityUnhandledRealMException ex)
        {
            logger.error("Errorr Invoking REALM Service>> " + ex.getMessage(), ex);
            throw new GravityUnhandledRealMException(ex);
        }
    }

    private String invokeAPI(String url, JSONObject body, String restmthd, String token) throws GravityUnhandledException
    {
        try
        {
            Request request = null;
            Request.Builder reqBldr = new Request.Builder().url(url);
            logger.trace("INVOKE COGNO API\nURL>> " + url + "\nPOST Body>> " + body);
            MediaType mediaType = MediaType.parse("application/json");

            String reqBody = body == null ? "" : body.toString();
            RequestBody reqbody = RequestBody.create(mediaType, reqBody);
            if (!restmthd.equals("GET"))
            {
                reqBldr.method(restmthd, reqbody);
            }
            request = reqBldr.addHeader("Authorization","Bearer "+token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("req_id", UIDUtil.GenerateUniqueId())
                    .build();

            Long timeout = 60L;
            OkHttpClient.Builder clntBldr = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS);
            //ignore ssl.
            AllowUntrusted(clntBldr);

            OkHttpClient client = clntBldr.build();
            logger.debug("RealmRequest >> " + JSONUtil.ToJSON(request).toString());
            
            Response response = client.newCall(request).execute();
            if (response.code() == 401)
            {
                logger.debug("Got 401 error from NUCLEUS so relogin and resending NUCLEUSRequest");

                if (retryCount < retryLimit)
                {
                    try
                    {
                        ALMServerContext sctx = ServerContext.This().getALMCtx();
                        String newtoken = sctx.regenerateToken(token);
                        retryCount++;
                        invokeAPI(url, body, restmthd, newtoken);
                    }
                    catch (GravityException ex)
                    {
                        logger.error(ex.getMessage(), ex);
                        throw new GravityUnhandledException(ex);
                    }
                }
                retryCount = 0;

            }
            if (response.isSuccessful())
            {
                String respStr = response.body().string();
                logger.trace("Response Body>> " + respStr);
                return respStr;

            }
            else
            {
                logger.trace("Response>> " + response);

                //TBD Through an appropeate exception
                String respStr = response.body().string();

                throw new GravityUnhandledException(new Exception("Errorr Invoking RealM Service >> " + respStr));
            }
        }
        catch (Exception ex)
        {
            logger.error("Errorr Invoking REALM Service>> " + ex.getMessage(), ex);
            throw new GravityUnhandledException(ex);
        }
    }

    private Event invokeAPIRequest(String url, code.ua.requests.Request req, String restmthd, String token) throws  GravityUnhandledException {
        try
        {
            okhttp3.Request.Builder reqBldr = new okhttp3.Request.Builder().url(url);
            String reqStr = null;
            if (logger.isDebugEnabled() && req != null)
            {
                reqStr = JSONUtil.ToJSON(req).toString();
            }
            logger.trace("INVOKE RealM API\nURL>> " + url + "\nPOST Body>> " + reqStr);
            MediaType mediaType = MediaType.parse("application/json");

            if (!restmthd.equals("GET"))
            {
                String reqBody = FWUtil.Serialize(req);
                RequestBody reqbody = RequestBody.create(mediaType, reqBody);

                reqBldr.method(restmthd, reqbody);
            }
            okhttp3.Request request = reqBldr.addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("req_mode", "Object")
                    .build();
            Long timeout = 60L;
            OkHttpClient.Builder clntBldr = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS);
            //ignore ssl.
            AllowUntrusted(clntBldr);

            OkHttpClient client = clntBldr.build();

            logger.debug("RealmRequest >> " + JSONUtil.ToJSON(request).toString());
            Response response = client.newCall(request).execute();

            if (response.isSuccessful())
            {
                String respStr = response.body().string();
                logger.trace("Response Body << " + respStr);
                Event ev = (Event) FWUtil.Deserialize(respStr);
                if (logger.isDebugEnabled())
                {
                    logger.trace("Response Body << " + JSONUtil.ToJSON(ev));
                }
                switch (ev.getEvType())
                {
                    case Error, Failed ->
                        throw new GravityUnhandledRealMException(ev);
                }
                if (ev.getEvCodeApp() != null && ev.getEvCodeApp().equals(code.realm.fw.events.EventCode.RealmException))
                {
                    EventRealmException evEx = (EventRealmException) ev;
                    throw new GravityUnhandledRealMException(evEx);
                }
                return ev;

            }
            else
            {
                logger.trace("Response << " + response);

                //TBD Through an appropeate exception
                String respStr = response.body().string();
                logger.debug("Response : " + respStr);
                Event ev = (Event) FWUtil.Deserialize(respStr);
                throw new GravityUnhandledRealMException(ev);
            }
        }
        catch (Exception ex)
        {
            logger.error("Errorr Invoking REALM Service>> " + ex.getMessage(), ex);
            throw new GravityUnhandledException(ex);
        }
    }

    private void AllowUntrusted(OkHttpClient.Builder clientBuilder) throws NoSuchAlgorithmException, KeyManagementException
    {
        X509TrustManager trustManager = new X509TrustManager()
        {
            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                X509Certificate[] cArrr = new X509Certificate[0];
                return cArrr;
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                    final String authType)
            {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                    final String authType)
            {
            }
        };
        final TrustManager[] trustAllCerts = new TrustManager[]
        {
            trustManager
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");

        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
        HostnameVerifier hostnameVerifier = new HostnameVerifier()
        {
            @Override
            public boolean verify(String hostname, SSLSession session)
            {
                return true;
            }
        };
        clientBuilder.hostnameVerifier(hostnameVerifier);
    }
}
