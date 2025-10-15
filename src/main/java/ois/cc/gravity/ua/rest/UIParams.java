package ois.cc.gravity.ua.rest;

public enum UIParams
{
    ReqId("req_id"),
    ReqType("req_type"),
    ReqCode("req_code"),
    Attributes("attributes"),
    EntityName("entity_name"),
    EntityId("entity_id"),
    Token("access_token");

    private String val;

    private UIParams(String val)
    {
        this.val = val;
    }

    public String getVal()
    {
        return val;
    }
}
