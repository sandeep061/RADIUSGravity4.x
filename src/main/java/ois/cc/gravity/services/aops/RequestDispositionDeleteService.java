package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Utils.JSONUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCSATConfQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCSATConf;
import ois.radius.cc.entities.tenant.cc.Disposition;
import ois.radius.cc.entities.tenant.cc.Disposition0;
import ois.radius.cc.entities.tenant.cc.Disposition1;
import org.json.JSONArray;
import org.vn.radius.cc.platform.exceptions.RADException;

import java.util.ArrayList;
import java.util.List;

public class RequestDispositionDeleteService extends ARequestEntityService
{

    public RequestDispositionDeleteService(UAClient uac)
    {
        super(uac);
    }

    private ArrayList<NameValuePair> entities = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;

        Disposition disp = _tctx.getDB().FindAssert(EN.Disposition.getEntityClass(), req.getEntityId());

        //Check this disposition is default disposition of any Campaign or not.
        if (disp instanceof Disposition0)
        {
            validateDefDispositions((Disposition0) disp);
        }

        entities.add(new NameValuePair(ENActionList.Action.Delete.name(), disp));
//        entities.addAll(assertSubDispositionToBeDelete(req, disp.getId()));
        assertSubDispositionToBeDelete(req, disp.getId());
        updateCSATConf(disp);
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        return new EventEntityDeleted(req, disp);
    }

    private void validateDefDispositions(Disposition0 disp) throws GravityRuntimeCheckFailedException
    {
        if (disp.getIsDefault())
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.Delete_Default_Disposition_NotAllowed);
        }
    }

    /**
     * This method will return all the sub-disposition of specified Disposition which will be delete.
     * <p>
     * // * @param disp // * @param enst
     *
     * @return
     * @throws RADException
     */
    private ArrayList<Disposition1> getChildDispositions(Long dispid) throws GravityException, CODEException, Exception
    {
        JPAQuery subqry = new JPAQuery("Select d from Disposition1 d Where d.Super.Id =: id And d.Deleted=: es");
        subqry.setParam("id", dispid);
        subqry.setParam("es", false);
        ArrayList<Disposition1> subDisps = _tctx.getDB().Select(EN.Disposition1, subqry);

        return subDisps;
    }

    private void assertSubDispositionToBeDelete(RequestEntityDelete req, Long dispid) throws GravityException, CODEException, Exception
    {

        List<Disposition1> subDisps = getChildDispositions(dispid);
        for (Disposition1 d : subDisps)
        {
            entities.add(new NameValuePair(ENActionList.Action.Delete.name(), d));
            assertSubDispositionToBeDelete(req, d.getId());
        }

    }

    private void updateCSATConf(Disposition dis) throws CODEException, GravityException, Exception
    {
        AOPsCSATConf csatConf = _tctx.getDB().Find(new AOPsCSATConfQuery().filterByAOPs(dis.getAOPs().getId()));
        if (csatConf != null)
        {

            String dispositionCode = csatConf.getDispositionCodes();
            if (dispositionCode.contains("*"))
            {
                return;
            }

            JSONArray array = new JSONArray(dispositionCode);
            ArrayList<String> aldiscodes = JSONUtil.FromJSON(array.toString(), ArrayList.class);
            if (aldiscodes.size() == 1 && aldiscodes.contains(dis.getCode()))
            {
                //throw an exception
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.MappedObjectFound, dis.getCode() + " Disposition mapped with this " + EN.AOPsCSATConf.name() + " Id " + csatConf.getId());
            }
            aldiscodes.remove(dis.getCode());
            csatConf.setDispositionCodes(new JSONArray(aldiscodes).toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), csatConf));
        }
    }
}
