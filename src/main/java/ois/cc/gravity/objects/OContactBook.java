package ois.cc.gravity.objects;

import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.User;

import java.util.ArrayList;
import java.util.Date;

public class OContactBook extends AObject{

    private Long CreatedBy;
    
    private Date CreatedOn;
    
    private Long EditedBy;
    
    private Date EditedOn;

    private String Name;

    private String Department;

    private String Designation;

    private String Organization;

    private String Location;

    private AOPs AOPs;

    private User User;

    private ArrayList<OContactBookAddress> ContactBookAddress;

    public OContactBook(){

    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getOrganization() {
        return Organization;
    }

    public void setOrganization(String organization) {
        Organization = organization;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public AOPs getAOPs() {
        return AOPs;
    }

    public void setAOPs(AOPs AOPs) {
        this.AOPs = AOPs;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }

    public ArrayList<OContactBookAddress> getContactBookAddress() {
        return ContactBookAddress;
    }

    public void setContactBookAddress(ArrayList<OContactBookAddress> contactBookAddress) {
        ContactBookAddress = contactBookAddress;
    }

    public Long getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(Long createdBy) {
        CreatedBy = createdBy;
    }

    public Date getCreatedOn()
    {
        return CreatedOn;
    }

    public void setCreatedOn(Date CreatedOn)
    {
        this.CreatedOn = CreatedOn;
    }

    public Long getEditedBy()
    {
        return EditedBy;
    }

    public void setEditedBy(Long EditedBy)
    {
        this.EditedBy = EditedBy;
    }

    public Date getEditedOn()
    {
        return EditedOn;
    }

    public void setEditedOn(Date EditedOn)
    {
        this.EditedOn = EditedOn;
    }
    
    
}
