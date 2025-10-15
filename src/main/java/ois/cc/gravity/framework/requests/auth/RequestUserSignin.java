/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.framework.requests.auth;

import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

/**
 *
 * @author Deepak
 */
public class RequestUserSignin extends RequestUserAbase
{
    private String LoginId;

    private String Password;

    public RequestUserSignin(String requestid, GReqType type, GReqCode code)
    {
        super(requestid, type,code);
    }

    public String getLoginId()
    {
        return LoginId;
    }

    public void setLoginId(String LoginId)
    {
        this.LoginId = LoginId;
    }

    public String getPassword()
    {
        return Password;
    }

    public void setPassword(String Password)
    {
        this.Password = Password;
    }



}
