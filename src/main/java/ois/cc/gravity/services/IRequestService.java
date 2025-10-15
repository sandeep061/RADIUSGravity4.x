package ois.cc.gravity.services;



import code.ua.events.Event;
import code.ua.requests.Request;

public interface IRequestService
{
    public Event ProcessRequest(Request request) throws Throwable;
}
