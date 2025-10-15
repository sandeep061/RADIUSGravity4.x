/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ois.cc.gravity.framework.events.aops;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OCrossCXContactMap;

/**
 *
 * @author Sandeepkumar.Sahoo
 * @since Jul 4, 2025
 */
public class EventCrossCXContactMapFetched extends EventOK
{
    
    private OCrossCXContactMap CrossCXContactMap;

    public EventCrossCXContactMapFetched(Request request)
    {
        super(request, EventCode.CrossCXContactMapFetched);
    }

    public OCrossCXContactMap getCrossCXContactMap()
    {
        return CrossCXContactMap;
    }

    public void setCrossCXContactMap(OCrossCXContactMap CrossCXContactMap)
    {
        this.CrossCXContactMap = CrossCXContactMap;
    }

    

}
