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

public class YourTestBed {
    public static void main(String[] args) throws Throwable {

	//**********************************************************
	//**** to use JEL we first have to define the namespace ****

	// we shall export the static methods of java.lang.Math
	Class[] stLib=new Class<?>[1];
	stLib[0]=java.lang.Math.class;

	// we shall enable access to methods of two classes
	Class[] dynLib=new Class<?>[2];
	// we export global context fields/methods
	dynLib[0]=GlobalContext.class;
	// we export YYY getXXXProperty() methods for dynamic variable access
	dynLib[1]=DVResolverProvider.class;

	// this initializes the resolver object
	DVResolverProvider resolver=new DVResolverProvider();

	// we shall allow dot operators on strings and data
	Class[] dotLib=new Class<?>[2];
	dotLib[0]=Data.class;
	dotLib[1]=java.lang.String.class;
	
	// finally, the namespace is defined by constructing the library class
	gnu.jel.Library lib=
	    new gnu.jel.Library(stLib,dynLib,dotLib,resolver,null);


	//**********************************************************
	//******** Now we setup the global context and data  *******

	Object[] context=new Object[2];
	GlobalContext gc=new GlobalContext(0.2,new Data(2),new Data(10));
	context[0]=gc;
	context[1]=resolver; // this pointer for YYY getXXXProperty() methos


	//**********************************************************
	//******** We are ready to compile some expressions  *******
	gnu.jel.CompiledExpression expr;

	// constant expression
	expr=gnu.jel.Evaluator.compile("2*2",lib);
	System.out.println("2*2="+expr.evaluate(context));

	// expression accessing the variables
	expr=gnu.jel.Evaluator.compile("x",lib);
	System.out.println("x="+expr.evaluate(context));

	// three expressions accessing the variables with dot operator
	expr=gnu.jel.Evaluator.compile("d1.value",lib);
	System.out.println("d1.value="+expr.evaluate(context));
	
	//
	expr=gnu.jel.Evaluator.compile("d2.value",lib);
	System.out.println("d2.value="+expr.evaluate(context));

	//
	expr=gnu.jel.Evaluator.compile("(d1.value+d2.value)*x*10",lib);
	System.out.println("(d1.value+d2.value)*x*10="+expr.evaluate(context));

	// also using static functions
	expr=gnu.jel.Evaluator.compile("round((d1.value+d2.value)*x*10)",lib);
	System.out.println("round((d1.value+d2.value)*x*10)="+expr.evaluate(context));

	// LET's try dynamic variables
	// First, we add few _DYNAMICALLY_, this can (and intended to) be
	// done after the gnu.jel.Library initialization
	resolver.addProperty("sDvar","str1");
	resolver.addProperty("dataDvar",new Data(3));
	
	// now we can access them
	expr=gnu.jel.Evaluator.compile("sDvar",lib);
	System.out.println("sDvar="+expr.evaluate(context));
	expr=gnu.jel.Evaluator.compile("dataDvar",lib);
	System.out.println("dataDvar="+expr.evaluate(context));
	
	// it is possible to have hierarchical name space
	// let's add a second level of hierarchy
	resolver.addProperty("sDvar.data1",new Data(5));
	resolver.addProperty("sDvar.data2",new Data(6));
	resolver.addProperty("sDvar.str","This is string");

	// we can access these also
	expr=gnu.jel.Evaluator.compile("sDvar.data1",lib);
	System.out.println("sDvar.data1="+expr.evaluate(context));

	expr=gnu.jel.Evaluator.compile("sDvar.str",lib);
	System.out.println("sDvar.str="+expr.evaluate(context));

	expr=gnu.jel.Evaluator.compile("sDvar.data2",lib);
	System.out.println("sDvar.data2="+expr.evaluate(context));

	// they are ready for calculations
	expr=gnu.jel.Evaluator.compile("sDvar.data1+sDvar.data2+1",lib);
	System.out.println("sDvar.data1+sDvar.data2+1="+expr.evaluate(context));

	// You can add more expressions here
    };
    
};
