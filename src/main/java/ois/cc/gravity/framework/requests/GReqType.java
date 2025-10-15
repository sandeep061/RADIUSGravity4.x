/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.framework.requests;

/**
 *
 * @author Deepak
 */
public enum GReqType implements code.ua.requests.RequestType
{
    //suman - 5th Feb 2018
    //should be related or corresponding to EventType. Though not used yet. 

    //@since V:101120 - changelog
    /**
     * Authentication & Authorization requests.
     */
    Auth,
    /**
     * Request to create ER structure, mostly to create dynamic entities etc.
     */
    Define,
    /**
     * Select, Insert, Edit, Delete
     */
    Config,
    /**
     * Application logic. Control types can be sub divided into other categories based on requirement.
     */
    Control,

    System,

    User;
}
