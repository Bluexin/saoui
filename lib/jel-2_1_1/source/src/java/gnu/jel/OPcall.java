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

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import gnu.jel.debug.Debug;
import java.util.Stack;

/**
 * A tree node, representing a method call (field/local variable load).
 */
public class OPcall extends OP {

  /** Holds method to be executed */
  public Member m; // member to eval (null for the local variable access)

  /**
   * local variable number (in case m=null), number of formal
   * parameters of the method to call otherwise.  */
  public int nplv=0;

  /**
   * if evaluation of the method will be attempted at compile-time */
  private boolean aEval=false;

  /**
   * if the varagrs expansion is used va is the number of the
   * parameters at the tail of the call to be converted into the
   * array.
   */
  private int va=0;

  /**
   * Prepares a new method/field call/get operation to be added to the code.
   * @param m method/field to call/get.
   * @param np number of actual formal parameters (not considering "this")
   * @param paramOPs stack holding the operands
   * @param aEval indicates if the method call should be attempted
   *                    at the compile time
   */
  public OPcall(Member m, int np, Stack<OP> paramOPs,  boolean aEval) 
    throws CompilationException {
    this.m=m;

    // if method is not static note "this"
    int thisIdx=((m.getModifiers() & 0x0008) == 0)?-1:0;
    
    Class<?>[] reqParamTypes=Library.getParameterTypes(m,-1);

    // determine if we need to do the varargs expansion
    // The Library had already made this decision, but we do it
    // in AST too because it can be used independently. 
    if (reqParamTypes.length>0) {
      Class<?> LA=reqParamTypes[reqParamTypes.length - 1];
      if (np>reqParamTypes.length)
        va=np-reqParamTypes.length+1;
      else if (LA.isArray()) {
        if (Debug.enabled)
          Debug.check(np == reqParamTypes.length);
        // if the argument number is matching exactly we analyze the last arg type
        OP cop=paramOPs.peek();
        if ((!isWidening(cop.resType,LA)) &&
            isWidening(cop.resType, LA.getComponentType()))
          va=1;
      }
    }
    if (va>0)
      reqParamTypes=Library.getParameterTypes(m, np);
    nplv=reqParamTypes.length;    
    if (thisIdx==-1) nplv++;             // method is not static

    this.chi=new OP[nplv];

    // convert formal and actual parameter types and "this", if needed    
    for(int i=reqParamTypes.length-1;i>=thisIdx;i--) {
      Class<?> cReq=(i>=0?reqParamTypes[i]:m.getDeclaringClass());

      OP cop=paramOPs.peek();

      // add convert type OP
      if ((cop.resID==10) || (cop.resType!=cReq))
        paramOPs.push(new OPunary(paramOPs,typeID(cReq),cReq,i<0));

      chi[i-thisIdx]=paramOPs.pop();
    };
    
    // push & store the result type
    resType=Library.getType(m);
    resID=typeID(resType);
    //    System.out.println("MAKING CALL TO "+m.getName()+
    //                       ClassFile.getSignature(m)+" returning "+
    //                       m.getType());

    // determine if compile-time evaluation should be attempted
    this.aEval=(aEval &&  // eval only if requested.
                ((m.getModifiers() & 0x0008)!=0) && // if static
                ((resID<=7) || (resID==11)) //  if can store result
                );
  };

  /**
   * Prepares access to the local variable (formal parameter) of method.
   * @param lvarn local variable number.
   * @param type local variable type.
   */
  public OPcall(int lvarn, Class<?> type) {
    this.m=null;
    this.nplv=lvarn;
    resID=typeID(type);
    resType=type;
  };

  /**
   * Attempts to evaluate this function.
   * @return the OPload, representing the function's result (if it can \
   *         be evaluated).
   * @throws Exception if the function can't be evaluated, or the evaluation
   *         results in error.
   */
  public Object eval() throws Exception {
    Object[] params=new Object[chi.length];
    boolean[] evaltd=new boolean[chi.length];

    Exception exception=null;
    for(int i=0;i<chi.length;i++) {
      try {
        params[i]=chi[i].eval();
        evaltd[i]=true;
      } catch (Exception exc) {
        exception=exc;
        evaltd[i]=false;
      };
    };
    
    Object res=null;
    
    if (exception==null) { // try to evaluate the method
      try {
        if (!aEval)
          throw new Exception();
        
        if (m instanceof Method) {
          if (va>0) { // convert varargs
            Object[] newparams=new Object[chi.length-va+1];
            Class<?>[] reqParamTypes=Library.getParameterTypes(m,-1);
            Class<?> arrpar=reqParamTypes[reqParamTypes.length-1];
            // copy the leading parameters
            for (int i=0;i<reqParamTypes.length-1;i++)
              newparams[i]=params[i];
            // create the array
            Object varray=Array.newInstance(arrpar.getComponentType(), va);
            // copy varargs into the array
            for (int i=0;i<va;i++)
              Array.set(varray,i,params[reqParamTypes.length-1+i]);            
            // pass the array as the last parameter
            newparams[reqParamTypes.length-1]=varray;
            // call the method
            res=((Method)m).invoke(null,newparams);
          } else
            res=((Method)m).invoke(null,params);
        }
        else if (m instanceof Field) res=((Field)m).get(null);
        else throw new Exception();       
      } catch (Exception exc) {
        exception=exc;
      };
    };
    
    if (exception!=null) {
      // didn't eval, replace at least evaluated children
      for(int i=0;i<chi.length;i++)
        if (evaltd[i])
          chi[i]=new OPload(chi[i],params[i]);
      throw exception;
    };
    
    return res;
  };

  protected final static byte[] arrayTypeCodes;
  protected final static int[] arrayStoreCodes;

  static {
    arrayTypeCodes=(byte[])TableKeeper.getTable("arrayTypeCodes");
    arrayStoreCodes=(int[])TableKeeper.getTable("arrayStoreCodes");
  }
  
  // compilation code
  public void compile(ClassFile cf) {
    if (m==null) {
      // load the local variable with a given number

      //  int[][] load={
      //    //wide  shrt  0    1    2    3
      //    {0x15c4,0x15,0x1a,0x1b,0x1c,0x1d}, // Z
      //    {0x15c4,0x15,0x1a,0x1b,0x1c,0x1d}, // B
      //    {0x15c4,0x15,0x1a,0x1b,0x1c,0x1d}, // C
      //    {0x15c4,0x15,0x1a,0x1b,0x1c,0x1d}, // S
      //    {0x15c4,0x15,0x1a,0x1b,0x1c,0x1d}, // I
      //    {0x16c4,0x16,0x1e,0x1f,0x20,0x21}, // J
      //    {0x17c4,0x17,0x22,0x23,0x24,0x25}, // F
      //    {0x18c4,0x18,0x26,0x27,0x28,0x29}, // D
      //    {0x19c4,0x19,0x2a,0x2b,0x2c,0x2d}  // L
      //  };

      int lvt=resID-4;
      if (lvt<0) lvt=0;

      int lvarn_translated=cf.paramsVars[nplv];
      
      if (lvarn_translated<4) 
        cf.code(0x1a+lvt*4+lvarn_translated);
      else if (lvarn_translated<=255)
        cf.code(0x15+lvt+(lvarn_translated<<8));
      else {
        cf.code(((0x15+lvt)<<8)+0xc4);
        cf.writeShort(lvarn_translated);
      };
    } else {
      cf.code(0xFB); // labels block;

      Class<?> LA=null;
      int tID=0;
      
      for(int i=0;i<chi.length;i++) {
        if (i==chi.length-va) {
          // here we create the array to hold the varargs
          Class<?>[] reqParamTypes=Library.getParameterTypes(m,-1);
          LA=reqParamTypes[reqParamTypes.length - 1].getComponentType();
          tID=typeID(LA);
          cf.codeLDC(new Integer(va),4);       // push item count to the stack
          if (tID<8) {
            // array of primitive types
            cf.code(0xbc);                     // |    newarray
            cf.code(arrayTypeCodes[tID]);      // |      <atype>
          } else {
            // array of references
            cf.code(0xbd);                     // |    anewarray
            cf.writeShort(cf.getIndex(LA,9));  // |      <CP ref>
          }
          cf.noteStk(4, 8);    // element count comes out, array ref comes in
        }
        if (i>=chi.length-va) {
          cf.code(0x59);                       // |    dup
          cf.noteStk(-1, 8);    // another array ref comes in
          cf.codeLDC(new Integer(i-chi.length+va),4); // load array index         
        }
        chi[i].compile(cf);  // compile the argument value
        cf.code(0xFA);       // ensure value in stack 
        if (i>=chi.length-va) {
          cf.code(arrayStoreCodes[tID>8?8:tID]);
          cf.noteStk(chi[i].resID, -1);      // value comes out
          cf.noteStk(4, -1);              // index comes out
          cf.noteStk(8, -1);              // arrayref comes out
        }
      }
      
      cf.code(0xF8); // labels unblock;

      for(int i=0;i<chi.length-va;i++)
        cf.noteStk(chi[i].resID,-1);   // non-varargs come out
      
      if (va>0)
        cf.noteStk(8,-1);              // array ref (in case of varargs) comes out

      cf.codeM(m); // call the method / get field
    };
    cf.noteStk(-1,resID);              // result of the call comes in
  };
  
};
