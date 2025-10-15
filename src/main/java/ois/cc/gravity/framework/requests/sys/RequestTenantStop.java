/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.framework.requests.sys;


import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.cc.gravity.framework.requests.auth.RequestSUAbase;

/**
 *
 * @author Deepak
 */
public class RequestTenantStop extends RequestSUAbase
{

	public RequestTenantStop(String requestid)
	{
    	super(requestid, GReqType.Control, GReqCode.TenantStop);
	}

}
