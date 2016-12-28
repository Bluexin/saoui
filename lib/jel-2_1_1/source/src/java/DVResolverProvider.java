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

// This class is both used to resolve the dynamic variable names and
// to provide their values. These two functions can also be
// implemented by two different classes.
public class DVResolverProvider extends gnu.jel.DVMap {

    private java.util.HashMap<String,Object> properties=
	new java.util.HashMap<String,Object>();

    // adds a new property
    protected void addProperty(String name,Object value) {
	properties.put(name,value);
    };

    // implements the method of DVResolver interface,
    // used by the compiler to query about available dynamic
    // variables
    public String getTypeName(String name) {
	Object val=properties.get(name);
	if (val==null) return null; // dynamic variable does not exist
	if (val instanceof Data) return "Data";
	if (val instanceof String) return "String";
	// the type is not supported we say the variable is not defined
	return null;
    };
    
    // Next we have those YYY getXXXProperty(String) methods described in
    // the manual

    public Data getDataProperty(String name) {
	return (Data)properties.get(name);
    };

    public String getStringProperty(String name) {
	return (String)properties.get(name);
    };

};
