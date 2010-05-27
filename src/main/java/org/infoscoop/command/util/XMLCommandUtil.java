/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.command.util;


import org.apache.commons.logging.Log;
import org.infoscoop.command.CommandResult;

/**
 * The utility class for XML command.
 * @author nakata
 *
 */
public final class XMLCommandUtil {

    /**
     * Type of input string：boolean
     */
    public static final int VALUE_TYPE_BOOL = 0;

    /**
     * Type of input string：number
     */
    public static final int VALUE_TYPE_NUM = 1;

    /**
     * Type of input string：mode(Display mode of multi RSS reader. Each category／Time order)
     */
    public static final int VALUE_TYPE_MODE = 2;

    //Not allow to use default constructor.
    private XMLCommandUtil() {

    }

    /**
     * return the CommandResult object that shows execution result of command.
     * @param uid :userId that executed command
     * @param commandName :the name of executed command
     * @param logger :location to output log
     * @param id :Command id
     * @param isOK :Whether execution result of command is success or not.
     * @param message :String that shows the reason when the execution result of command is failure.
     * @return CommandResult object
     */
    public static CommandResult createResultElement(String uid, String commandName, Log logger,
            String id, boolean isOK, String message) {

        String status;
        if (isOK) {
            status = "ok";

            if(logger.isInfoEnabled())
            	logger.info("uid:[" + uid
                    + "]: " + commandName + ": OK");
        } else {
            status = "failed";
            if(logger.isInfoEnabled())
            	logger.info("uid:[" + uid
                    + "]: " + commandName + ": Failed:" + message);
        }
        CommandResult result = new CommandResult(id, status, message);

        return result;
    }

    /**
     * Confirm whether the value that given by character string is boolean value or not.
     * @param value: string 
     * @return the boolean value that whether character string is boolean value or not.(true or false： It is not distinguished between the capital letter and the small letter.)
     */
    public static boolean isBooleanValue(String value) {

        boolean result = false;

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            result = true;
        }

        return result;
    }

    /**
     * Confirm whether the value that given by character string is numerical value or not.
     * @param value: string
     * @return when the character string is possible to convert the numerical value, the return value is "true", the other is "false".
     */
    public static boolean isNumberValue(String value) {

        boolean result = false;

        try {
            Integer.parseInt(value);
            result = true;
        } catch (NumberFormatException e) {
            result = false;
        }

        return result;
    }
}
