package ois.cc.gravity.si.nucleus;

import CrsCde.CODE.Common.Utils.JSONUtil;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import ois.cc.gravity.AppProps;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.services.exceptions.GravityException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NucleusInvoker
{

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private Integer retryCount = 0;
    private final Integer retryLimit = 3;

    public String SendToNucleusSerice(String url, String restmthd, JSONObject body, String token) throws GravityUnhandledException
    {
        String reqToken = token == null ? "" : token;
        Request request = null;
        Request.Builder reqBldr = new Request.Builder().url(url);
        logger.trace("INVOKE NUCLEUS API\nURL>> " + url + "\nPOST Body>> " + body);
        MediaType mediaType = MediaType.parse("application/json");

        String reqBody = body == null ? "" : body.toString();
        RequestBody reqbody = RequestBody.create(mediaType, reqBody);
        if (!restmthd.equals("GET"))
        {
            reqBldr.method(restmthd, reqbody);
        }
        request = reqBldr.addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        return invokeAPI(url,request, body, restmthd, reqToken);
    }

    public String SendToNucleusForGetToken(String url, String restmthd, String tntcode, JSONObject body) throws GravityUnhandledException
    {
        Request request = null;
        Request.Builder reqBldr = new Request.Builder().url(url);

        String username = AppProps.RAD_App_Secret_Id;
        String password = AppProps.RAD_App_Secret_Pwd;

        // Encode in Base64
        String credentials = username + ":" + password;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        logger.trace("INVOKE NUCLEUS API\nURL>> " + url + "\nPOST Body>> " + body);

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
        if (!restmthd.equals("GET"))
        {
            reqBldr.method(restmthd, reqbody);
        }

        request = reqBldr.addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", basicAuth)
                .build();
        return invokeAPI(url,request, body, restmthd, tntcode);
    }

    public String SendToNucleusForAuth(String url, String restmthd, String tntcode, JSONObject body) throws GravityUnhandledException
    {

        Request request = null;
        Request.Builder reqBldr = new Request.Builder().url(url);

        String username = AppProps.RAD_App_Secret_Id;
        String password = AppProps.RAD_App_Secret_Pwd;

        // Encode in Base64
        String credentials = username + ":" + password;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        logger.trace("INVOKE NUCLEUS API\nURL>> " + url + "\nPOST Body>> " + body);
        MediaType mediaType = MediaType.parse("application/json");

        String reqBody = body == null ? "" : body.toString();
        RequestBody reqbody = RequestBody.create(mediaType, reqBody);
        if (!restmthd.equals("GET"))
        {
            reqBldr.method(restmthd, reqbody);
        }

        request = reqBldr.addHeader("Content-Type", "application/json")
                .addHeader("Authorization", basicAuth)
                .build();

        return invokeAPI(url,request, body, restmthd, tntcode);

    }

    private String invokeAPI(String url,Request request, JSONObject body, String restmthd, String token) throws GravityUnhandledException
    {
        try
        {

            Long timeout = 60L;
            OkHttpClient.Builder clntBldr = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS);
            //ignore ssl.
            AllowUntrusted(clntBldr);

            OkHttpClient client = clntBldr.build();

            logger.debug("NUCLEUSRequest >>" + JSONUtil.ToJSON(request).toString());
            Response response = client.newCall(request).execute();
            if (response.code() == 401)
            {
                logger.debug("Got 401 error from NUCLEUS so relogin and resending NUCLEUSRequest");

                if (retryCount < retryLimit)
                {
                    try
                    {
                        NucleusServerContext sctx = ServerContext.This().getNucleusCtx();
                        String newtoken = sctx.regenerateToken(token);
                        retryCount++;
                        invokeAPI(url,request, body, restmthd, newtoken);
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
                logger.trace("Nucleus Response Body  << " + respStr);
                return respStr;

            }
            else
            {
                logger.trace("Nucleus Response Body << " + response);

                //TBD Through an appropeate exception
                String respStr = response.body().string();

                throw new GravityUnhandledException(new Exception("Errorr Invoking NUCLEUS Service >> " + respStr));
            }
        }
        catch (Exception ex)
        {
            logger.error("Errorr Invoking NUCLEUS Service>> " + ex.getMessage(), ex);
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
