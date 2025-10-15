package ois.cc.gravity.si.ucos;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import code.realm.fw.util.FWUtil;
import code.ua.events.Event;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import ois.cc.gravity.services.exceptions.GravityUnhandledRealMException;

public class UCOSInvoker
{

    public final Logger logger = LoggerFactory.getLogger(getClass());

    public String SendToUcosSerice(String url, String restmthd, JSONObject body, String token) throws GravityUnhandledException
    {
        String reqToken = token == null ? "" : token;
        return invokeAPI(url, body, restmthd, reqToken);
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
            request = reqBldr.addHeader("x-api-key", token).addHeader("Content-Type", "application/json").addHeader("req_id", UIDUtil.GenerateUniqueId()).build();

            Long timeout = 60L;
            OkHttpClient.Builder clntBldr = new OkHttpClient().newBuilder().connectTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout, TimeUnit.SECONDS);
            //ignore ssl.
            AllowUntrusted(clntBldr);

            OkHttpClient client = clntBldr.build();
            logger.debug("UCOSRequest >>" + JSONUtil.ToJSON(request).toString());
            Response response = client.newCall(request).execute();
            if (response.isSuccessful())
            {
                String respStr = response.body().string();
                logger.trace("UCOS Response Body << " + respStr);
                return respStr;

            }
            else
            {


                //TBD Through an appropeate exception
                String respStr = response.body().string();
                logger.trace("UCOS Response Body << " + respStr);
                throw new GravityUnhandledException(new Exception("Errorr Invoking UCOs Service >> " + respStr));
            }
        }
        catch (Exception ex)
        {
            logger.error("Errorr Invoking REALM Service>> " + ex.getMessage(), ex);
            throw new GravityUnhandledException(ex);
        }
    }

    public String SendToUcosSericeForAuth(String url, String restmthd, JSONObject body, String token) throws GravityUnhandledRealMException
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
            logger.trace("INVOKE UCOs API\nURL>> " + url + "\nPOST Body>> " + reqStr);

            MediaType mediaType = MediaType.parse("application/json");

            RequestBody reqbody = RequestBody.create(mediaType, body.toString());
            reqBldr.method(restmthd, reqbody);
            okhttp3.Request request = reqBldr.addHeader("Content-Type", "application/json")
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

            logger.debug("UCOSRequest >>" + JSONUtil.ToJSON(request).toString());
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
                logger.trace(" UCOS Response Body << " + respStr);
                return respStr;
            }
            else
            {


                //TBD Through an appropeate exception
                String respStr = response.body().string();
                logger.debug("UCOS Response <<" + respStr);
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
            public void checkServerTrusted(final X509Certificate[] chain, final String authType)
            {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType)
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
