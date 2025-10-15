package ois.cc.gravity.ua.rest;

import code.ua.events.Event;

/**
 * This exception will be used to wrap an event object and pass the event to caller method.
 *
 * @author suman
 */
public class EventWrapperException extends Exception
{

    private Event Event;

    public EventWrapperException(Event event)
    {
        Event = event;
    }

    public Event getEvent()
    {
        return Event;
    }

    public void setEvent(Event event)
    {
        Event = event;
    }
}
