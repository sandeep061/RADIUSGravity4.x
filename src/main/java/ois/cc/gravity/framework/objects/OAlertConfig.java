package ois.cc.gravity.framework.objects;

import ois.radius.cc.entities.tenant.cc.XAlertID;

import java.util.ArrayList;

public class OAlertConfig extends AObject{

    private ArrayList<Long> XAlertIDs;

    private ArrayList<String> Users = new ArrayList();

    private Boolean InApp;

    public ArrayList<Long> getXAlertIDs() {
        return XAlertIDs;
    }

    public void setXAlertIDs(ArrayList<Long> XAlertIDs) {
        this.XAlertIDs = XAlertIDs;
    }

    public ArrayList<String> getUsers() {
        return Users;
    }

    public void setUsers(ArrayList<String> users) {
        Users = users;
    }

    public Boolean getInApp() {
        return InApp;
    }

    public void setInApp(Boolean inApp) {
        InApp = inApp;
    }
}
