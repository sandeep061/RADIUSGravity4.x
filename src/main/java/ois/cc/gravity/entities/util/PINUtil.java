/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ois.cc.gravity.entities.util;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;

/**
 *
 * @author Manoj-PC
 * @since Jul 4, 2025
 */
public class PINUtil 
{
     private final Set<String> _existingPins;

    public PINUtil(Set<String> pins)
    {
        this._existingPins = pins;
    }

    public String GeneratePin(int length) throws GravityRuntimeCheckFailedException
    {
        if (length < 3 || length > 6)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange,"PIN length must be between 3 and 6 digits.");
        }

        int maxPins = (int) Math.pow(10, length);
        if (_existingPins.size() >= maxPins)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange,"PIN length must be between 3 and 6 digits.");
        }

        String pin;
        do
        {
            pin = generateRandomPin(length);
        }
        while (!_existingPins.add(pin));  // add() returns false if already present

        return pin;
    }

    private String generateRandomPin(int length)
    {
        int min = (int) Math.pow(10, length - 1); // e.g. 100 for 3-digit
        int max = (int) Math.pow(10, length) - 1; // e.g. 999 for 3-digit
        int pinNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return String.valueOf(pinNum);
    }

}
