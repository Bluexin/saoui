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

/**
 * Represents a method local to the class being compiled.
 */
public class LocalMethod extends LocalField {
  private Class<?>[] paramTypes;
  private Class<?>[] exceptions;
    
  /**
   * Constructs a new local method.
   * @param modifiers sum of one or more of <TT>PUBLIC</TT>, <TT>PRIVATE</TT>,
   *                  <TT>PROTECTED</TT>,<TT>STATIC</TT>, <TT>FINAL</TT>,
   *                  <TT>SYNCHRONIZED</TT>, <TT>NATIVE</TT>, <TT>ABSTRACT</TT>
   *                  constants of java.lang.reflect.Modifier .
   * @param type type of the return value.
   * @param name name of the method
   * @param paramTypes array of types of formal parameters excluding "this"
   *                   (null means no parameters).
   * @param exceptions checked exceptions thrown
   */
  public LocalMethod(int modifiers, Class<?> type, java.lang.String name, 
                     Class<?>[] paramTypes,Class<?>[] exceptions) {
	super(modifiers,type,name,null);

    if (paramTypes!=null)
      this.paramTypes=paramTypes;
    else
      this.paramTypes=new Class<?>[0];
 
    if (exceptions!=null)
      this.exceptions=exceptions;
    else
      this.exceptions=new Class<?>[0];
  };

  /**
   * Used to obtain types of formal parameters of this method.
   * @return array of classes representing formal parameters of the
   *         method except "this"
   */
  public Class<?>[] getParameterTypes() {
	return paramTypes;
  };

  /**
   * Used to get checked exceptions thrown by this method
   * @return array of checked exceptions
   */
  public Class<?>[] getExceptionTypes() {
    return exceptions;
  };

};

