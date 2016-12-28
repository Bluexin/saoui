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
import java.text.MessageFormat;

/**
 * Represents an error encountered during the compilation.
 * <P> The text of the messages can be changed/internationalized by
 * modification of JEL.properties file
 */
@SuppressWarnings("serial") public class CompilationException extends Exception {
  
  public  int col=-1;
  private int code=-1; // error code
  private Object[] params=null; // parameters to generate messages

  /**
   * Constructs new CompilationException with a single formatting parameter.
   * @param code is the error code (must correspond to a message in
   *             JEL.properties file).
   * @param param is the single Object parameter or an array of Objects to 
   *             be used in message formatting.
   */
  public CompilationException(int code, Object param) {
    if (Debug.enabled)
      Debug.check(code>=0);
    this.code=code;
    if (param!=null)
      if (param.getClass().isArray())
        this.params=(Object[])param;
      else {
        Object[] temp={param};
        this.params=temp;
      }
  }

  /**
   * Used to obtain the column, where error have occurred.
   * @return column, where error have occurred.
   */
  public int getColumn() {
    return col;
  };

  /**
   * Used to obtain the error code.
   * @return the error code, corresponding to one of the messages in
   *         JEL.properties file.
   */
  public int getType() {
    return code;
  };

  /**
   * Used to obtain the parameters for this error.
   * @return the parameters to be used in message formatting, they provide
   *         further information about the error.
   */
  public Object[] getParameters() {
    return params;
  };

  /**
   * Used to obtain the formatted error message.
   * @return the formatted error message.
   */
  public String getMessage() {
    if (Debug.enabled)
      Debug.check(col>=0);
    return TableKeeper.getMsg(code,params);
  };

};


