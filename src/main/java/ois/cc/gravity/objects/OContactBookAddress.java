package ois.cc.gravity.objects;

import ois.radius.ca.enums.AddressType;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.cc.entities.tenant.cc.ContactBook;

public class OContactBookAddress extends AObject{

    private String Address;

    private AddressType AddressType;

    private Channel Channel;

    private XPlatformID XPlatformID;

    private ContactBook ContactBook;

    public OContactBookAddress()
    {
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public AddressType getAddressType() {
        return AddressType;
    }

    public void setAddressType(AddressType addressType) {
        AddressType = addressType;
    }

    public Channel getChannel() {
        return Channel;
    }

    public void setChannel(Channel channel) {
        Channel = channel;
    }

    public XPlatformID getXPlatformID() {
        return XPlatformID;
    }

    public void setXPlatformID(XPlatformID XPlatformID) {
        this.XPlatformID = XPlatformID;
    }

    public ContactBook getContactBook() {
        return ContactBook;
    }

    public void setContactBook(ContactBook contactBook) {
        ContactBook = contactBook;
    }
}
