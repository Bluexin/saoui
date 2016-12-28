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

import gnu.jel.Evaluator;
import gnu.jel.CompiledExpression;
import gnu.jel.Library;
import gnu.jel.CompilationException;

public class Calculator {
 
  static final String[] help=
  {" This is a simple calculator, based on JEL.",
   "",
   " to use this calculator issue the following command :",
   " java Calculator \"expression\"",
   "",
   "Don't forget to use quotes around the expression if Your shell requires",
   " it. For example \"1&3\" will fail in WinNT cmd without quotes.",
   " The expression language is intuitively simple. You can use binary ",
   " operations : '+','-','*','/','%'(remainder),'&'(bitwise and),",
   " '|'(bitwise or), '^'(bitwise xor) , unary negation '-'. Also all ",
   " standard static functions of java.lang.Math are at Your disposal.",
   "Examples of expressions : \"1+1\", \"sin(1)\", \"random()\" .",
   "",
   "Of course the use of the compiler (what JEL is actually is) is crazy for ",
   "calculating expressions only once. This has a HUGE performance loss, but,",
   "this is just the demo. Enjoy !!!",
   "",
   "(c) 2009 by Konstantin L. Metlov (metlov@fti.dn.ua)",
   "    This  program is the free software  and  was distributed to You under",
   "    terms  of GNU General Public License. You should have the text of the",
   "    license together with  the source code of this sample and JEL itself.",
   "    If You don't have the source code or license- contact me immediately."
  };
  
  
  public static void main(String[] args) {
    if (args.length==0) {
      for(int i=0;i<help.length;i++)
	System.err.println(help[i]);
      return;
    };
    
    // Assemble the expression
    StringBuffer expr_sb=new StringBuffer();
    for(int i=0;i<args.length;i++) {
      expr_sb.append(args[i]);
      expr_sb.append(' ');
    };
    String expr=expr_sb.toString();

    // Set up library
    Class[] staticLib=new Class<?>[1];
    try {
      staticLib[0]=Class.forName("java.lang.Math");
    } catch(ClassNotFoundException e) {
      // Can't be ;)) ...... in java ... ;)
    };
    Library lib=new Library(staticLib,null,null,null,null);
    try {
    lib.markStateDependent("random",null);
    } catch (CompilationException e) {
      // Can't be also
    };

    // Compile
    CompiledExpression expr_c=null;
    try {
      expr_c=Evaluator.compile(expr,lib);
    } catch (CompilationException ce) {
      System.err.print("--- COMPILATION ERROR :");
      System.err.println(ce.getMessage());
      System.err.print("                       ");
      System.err.println(expr);
      int column=ce.getColumn(); // Column, where error was found
      for(int i=0;i<column+23-1;i++) System.err.print(' ');
      System.err.println('^');
    };

    if (expr_c !=null) {
      
      // Evaluate (Can do it now any number of times FAST !!!)
      Object result=null;
      try {
	result=expr_c.evaluate(null);
      } catch (Throwable e) {
	System.err.println("Exception emerged from JEL compiled"+
			   " code (IT'S OK) :");
	System.err.print(e);
      };
      
      // Print result
      if (result==null) 
	System.out.println("void");
      else
	System.out.println(result.toString());
    };
    
    // Done
  };
};


