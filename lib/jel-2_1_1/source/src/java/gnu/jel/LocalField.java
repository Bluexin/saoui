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

import gnu.jel.debug.Debug;
import java.lang.reflect.Member;

/**
 * Represents a field local to the class being compiled.
 */
public class LocalField implements Member {
  private int modifiers;
  private java.lang.String name;
  private Class<?> type;
  private Object constValue;

  /**
   * Constructs a new local field.
   * @param modifiers field modifiers, a sum of one or more of <TT>PUBLIC</TT>,
   *                <TT>PRIVATE</TT>,<TT>PROTECTED</TT>, <TT>STATIC</TT>,
   *                <TT>FINAL</TT>,<TT>VOLATILE</TT>, <TT>TRANSIENT</TT> 
   *                constants defined in java.lang.reflect.Modifier
   * @param type is a class representing the type of this field.
   * @param name is the name of this field.
   * @param constValue is the value of this field if it is static final,
   *                   <TT>null</TT> otherwise.
   */
  public LocalField(int modifiers, Class<?> type, java.lang.String name, Object constValue){
    if (Debug.enabled)
      Debug.check((constValue==null) || ((modifiers & 0x0018) ==0x0018));

	this.type=type;
	this.name=name;
	this.modifiers=modifiers;
    this.constValue=constValue;
  };

  public Class<?> getDeclaringClass() {
  	return null; // means local field
  };

  public java.lang.String getName() {
	return name;
  };

  public int getModifiers() {
	return modifiers;
  };
    
  public Class<?> getType() {
	return type;
  };

  public boolean isSynthetic() {
    return true;
  };

  /**
   * Returns a value of the public static final field.
   * <P>Fails assertion if called on the field which is not public
   *  static final.
   * @return value of the field, object of wrapped primitive type or string.
   */
  public Object getConstValue() {
    if (Debug.enabled)
      Debug.check(constValue!=null);
    return constValue;
  };

};
