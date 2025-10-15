package ois.cc.gravity.services.sys;

import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.common.EventVersionInfoFetched;
import ois.cc.gravity.framework.requests.sys.RequestVersionInfoFetch;
import ois.cc.gravity.services.ARequestCmdService;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RequestVersionInfoFetchService extends ARequestCmdService
{

    @Override
    protected Event ProcessCmdRequest(Request request) throws Throwable
    {
        RequestVersionInfoFetch req = (RequestVersionInfoFetch) request;

        String ver = "";
        ClassPathResource resource = new ClassPathResource("version.txt");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)))
        {
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1)
            {
                sb.append((char) i);
            }
            ver = sb.toString().trim();
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }

        EventVersionInfoFetched ev = new EventVersionInfoFetched(req, ver);
        return ev;
    }

}
