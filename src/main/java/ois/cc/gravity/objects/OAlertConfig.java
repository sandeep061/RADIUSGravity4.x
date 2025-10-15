package ois.cc.gravity.objects;

import ois.radius.cc.entities.tenant.cc.XAlertID;

import java.util.ArrayList;

public class OAlertConfig extends AObject{

    private ArrayList<XAlertID> XAlertIDs;

    private ArrayList<OUser> Users = new ArrayList();

    private Boolean InApp;

    public ArrayList<XAlertID> getXAlertIDs() {
        return XAlertIDs;
    }

    public void setXAlertIDs(ArrayList<XAlertID> XAlertIDs) {
        this.XAlertIDs = XAlertIDs;
    }

    public ArrayList<OUser> getUsers() {
        return Users;
    }

    public void setUsers(ArrayList<OUser> users) {
        Users = users;
    }

    public Boolean getInApp() {
        return InApp;
    }

    public void setInApp(Boolean inApp) {
        InApp = inApp;
    }
}
