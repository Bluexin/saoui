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

package gnu.jel.debug;

/** 
 * This class used for incorporating internal checks and
 * assertions into the code.  
 * <BR>None of these functions does anything if Debug.enabled is false.
 * <BR>If you really want to throw ALL debug messages from the final,
 * compiler generated, code -- wrap calls to Debug methods into the
 * <TT>if</TT> statement, checking <TT>Debug.enabled</TT> constant.
 * As shown in the example : 
 * <PRE>
 * import cz.fzu.metlov.jel.*;
 * ..... BLA BLA BLA ...
 * if (Debug.enabled) {
 *  Debug.println("I want this message to disappear in the optimized version");
 *  Debug.check(foo==superTimeConsumingFunction(bar), 
 * "I do not want to evaluate superTimeConsumingFunction(), when optimized."); 
 * }; 
 *</PRE> 
 */
public final class Debug {

  /**
   * Determines if debugging is enabled in current compilation.
   */
  public final static boolean enabled=@DEBUG@; // <-- AUTO GENERATED
  

  /**
   * Prints a line of the debug output.
   * The resulting line goes to System.err and is prefixed by "[DEBUG] ".
   * @param message message to print.
   */
  public final static void println(String message) {
    if (enabled) {
      System.err.print("[DEBUG] ");
      System.err.println(message);
    };
  };

  /**
   * Checks for the condition.
   * If condition is false this function prints a given message
   * to the System.err along with the stack trace.
   * @param condition is the condition to check.
   * @param message is the message to print if condition is false.
   */
  public final static void check(boolean condition, String message) {
    if (enabled && (!condition)) {
      System.err.print("Assertion failed :");
      System.err.println(message);
      Throwable tracer=new Throwable(message);
      tracer.printStackTrace();
    }; 
  };

  /**
   * Checks for the condition.
   * If condition is false this function prints a "Assertion failed."
   * to the System.err along with the stack trace.
   * @param condition is the condition to check.
   */
  public final static void check(boolean condition) {
    if (enabled && (!condition)) {
      Throwable tracer=new Throwable("Assertion failed.");
      tracer.printStackTrace();
    }; 
  };

  /**
   * Reports an exception, which should not occur(i.e. handled improperly).
   * @param t is what was thrown.
   * @param message is algorithm specific message.
   */
  public final static void reportThrowable(Throwable t,String message) {
    if (enabled) {
      System.err.println("Unexpected exception has occured :");
      System.err.println(message);
      t.printStackTrace();
    };
  };

  /**
   * Reports an exception, which should not occur(i.e. handled improperly).
   * @param t is what was thrown.
   */
  public final static void reportThrowable(Throwable t) {
    if (enabled) {
      System.err.println("Unexpected exception has occured :");
      t.printStackTrace();
    };
  };
};

