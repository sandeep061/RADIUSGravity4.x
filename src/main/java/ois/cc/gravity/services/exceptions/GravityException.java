/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.exceptions;

/**
 *
 * @author Deepak
 */
public class GravityException extends Throwable
{

    public GravityException()
    {
    }

    public GravityException(Throwable cause)
    {
        super(cause);
    }

    public GravityException(String message)
    {
        super(message);
    }

    public GravityException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
