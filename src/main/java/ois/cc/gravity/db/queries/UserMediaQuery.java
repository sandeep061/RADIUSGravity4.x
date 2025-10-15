package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;
import ois.radius.ca.enums.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserMediaQuery extends EntityQuery
{
    public UserMediaQuery()
    {
        super(EN.UserMedia);
    }

    public UserMediaQuery filterByUser(String uid)
    {
        AppendWhere("And UserMedia.User.UserId =: uid");
        _params.put("uid", uid);

        return this;
    }
    public UserMediaQuery filterByChannel(Channel channel)
    {

        AppendWhere("And UserMedia.Channel=: chn");
        _params.put("chn", channel);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case"byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byuser":
                    filterByUser(filters.get(name).get(0));
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
            }
        }
    }
        @Override protected void doApplyOrderBy (ArrayList < HashMap < String, Boolean >> orderby) throws
        CODEException, GravityIllegalArgumentException {


    }

}
