/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 2 -*-
*   This file is part of the Java Expressions Library (JEL).
*   For more information about JEL visit : http://fti.dn.ua/JEL/
*
*   Copyright (C) 1998, 1999, 2000, 2001, 2003, 2006, 2007, 2009 Konstantin L. Metlov
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package gnu.jel;

import java.util.*;
import java.io.*;
import gnu.jel.debug.Debug;

public class TableKeeper {
  private static final Hashtable<String,Object> tables;
  private static final ResourceBundle msgs;

  static {
    Hashtable<String,Object> temp=new Hashtable<String,Object>();
    PropertyResourceBundle resB=null;
    
	try {
      Class<?> c=Class.forName("gnu.jel.DVMap");
      // Read messages
      resB=
        new PropertyResourceBundle(c.getResourceAsStream("JEL.properties"));

      // Read tables
      ObjectInputStream ios=
        new ObjectInputStream(c.getResourceAsStream("tables.dat"));
      @SuppressWarnings("unchecked") 
        Hashtable<String,Object> temp1=(Hashtable<String,Object>)ios.readObject();
      temp=temp1;

      // work around the serialization bug that classes representing the
      // primitive types can be serialized but can not be deserealized
      String[] specialTypesStr=(String[]) temp.get("specialTypes");
      String[] specialClassesAddStr=(String[]) temp.get("specialClasses");
      
      Class<?>[] specialTypes=new Class<?>[specialTypesStr.length];
      Class<?>[] specialClasses=new Class<?>[specialTypesStr.length+
                                               specialClassesAddStr.length];
      for(int i=10; i<specialTypesStr.length;i++)
        specialClasses[i]=specialTypes[i]=Class.forName(specialTypesStr[i]);

      for(int i=0;i<8;i++)
        specialClasses[i]=specialTypes[i]=(Class<?>)
          Class.forName(specialTypesStr[i+20]).getField("TYPE").get(null);

      //      specialTypes[8]=null, // Generic reference         //  8
      specialClasses[9]=specialTypes[9]=Void.TYPE;               //  9
      temp.put("specialTypes",specialTypes);
      
      Class<?>[] specialClassesAdd=new Class<?>[specialClassesAddStr.length];
      for (int i=0; i<specialClassesAddStr.length;i++)
        specialClasses[specialTypesStr.length+i]=
          Class.forName(specialClassesAddStr[i]);
      temp.put("specialClasses",specialClasses);

	} catch (Exception exc) {
      if (Debug.enabled) {
		Debug.println("Exception when reading tables:");
		Debug.reportThrowable(exc);
      };
    };
    tables=temp;
    msgs=resB;
  };

  /**
   * Used to get a reference to the named int[][] table.
   * @return reference to the table
   */
  public static Object getTable(String name) {
    return tables.get(name);
  };

  public static String getMsg(int code,Object[] params) {
    return java.text.MessageFormat.format(msgs.getString("e"+code),params);
  };

}

