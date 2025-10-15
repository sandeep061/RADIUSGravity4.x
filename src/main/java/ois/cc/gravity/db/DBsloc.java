/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Prakasha.prusty
 * 1 Aug, 2024
 */
public class DBsloc
{

    private static final Logger logger = LoggerFactory.getLogger(DBsloc.class);

    private DBsloc()
    {

    }

    public static String getCoreDBName(String tenantcode)
    {
        return "gravitydt_" + tenantcode.toLowerCase();
    }

    public static String getSysDBName()
    {
        return "gravitydsys";
    }
}
