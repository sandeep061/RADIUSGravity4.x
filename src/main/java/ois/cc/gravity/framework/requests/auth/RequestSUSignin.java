package ois.cc.gravity.framework.requests.auth;

import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestSUSignin extends RequestSUAbase
{
	public RequestSUSignin(String requestid, GReqType type, GReqCode code)
    {
        super(requestid, GReqType.System, GReqCode.SUSignin);
    }

	private String LoginId;

	private String Password;

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
