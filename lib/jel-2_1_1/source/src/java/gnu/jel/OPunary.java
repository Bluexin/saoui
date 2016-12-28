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
import java.util.Stack;

/**
 * A tree node, representing unary operation.
 */
public class OPunary extends OP {

  /** code of this operation */
  public int code;

  private int uwrpCode=0; // what to code to unwrap the object
  private int implCode=0;
  private int uwrpsTo=-1;

  // The full list of codes is :
  // 0  -- negation (applicable to anything except boolean)
  // 1  -- bitwise not (applicable to all integral types)
  // 2  -- logical not (applicable to booleans only)
  // 3  -- return
  // 4  -- convert to boolean
  // 5  -- convert to byte
  // 6  -- convert to char
  // 7  -- convert to short
  // 8  -- convert to int
  // 9  -- convert to long
  // 10  -- convert to float
  // 11 -- convert to double
  // 12 -- convert to object (in this case the cls parameter gives class)
  // 13 -- convert to void (throw from stack)
  // 14 -- convert string to temporary string buffer (TSB)
  // 15 -- convert temporary string buffer (TSB) to string

  /** unary promotions of base types */
  protected final static byte[] unary_prmtns;
  
  private final static int[][] una;

  private final static String[] opNames;

  static {
    unary_prmtns=(byte[])TableKeeper.getTable("unary_prmtns");
    una=(int[][])TableKeeper.getTable("una");
    opNames=(String[])TableKeeper.getTable("opNames");

    if (Debug.enabled)
      Debug.check((opNames.length==una.length));
  };

  /**
   * Constructs a new unary operation.
   * <P>Codes are following:
   * <PRE>
   * 0  -- negation (applicable to anything except boolean)
   * 1  -- bitwise not (applicable to all integral types)
   * 2  -- logical not (applicable to booleans only)
   * 3  -- return the type in stack
   * </PRE>
   * @param paramOPs stack holding the operands
   * @param code operation code
   */
  public OPunary(Stack<OP> paramOPs, int code) 
    throws CompilationException {
    if (Debug.enabled)
      Debug.check((code>=0) && (code<=3));

    this.code=code;
    chi=new OP[1];
    chi[0]=paramOPs.pop();
    
    int opID=chi[0].resID;
    Class<?> opType=chi[0].resType;

    if (code==3) { // return
      // everything can be returned no checks/unwraps needed
      resID=(opID>9?8:opID);    // computes base type
      resType=opType;
      implCode=una[code][resID];
    } else {
      
      // unwrap object if can
      int unwrpID=unwrapType[opID];      
      if (unwrpID!=opID) {
        uwrpCode=((opID-12+11)<<8)+0x00FE;
        uwrpsTo=unwrpID;
      };
      
      if ((implCode=una[code][unwrpID])==0xFF) {
        // operation is not defined on types
        Object[] paramsExc={opNames[code],opType};
        throw new CompilationException(28,paramsExc);
      };
      
      resID=unary_prmtns[unwrpID];
      resType=specialTypes[resID];
    };
    
  };

  /**
   * Creates conversion operation to the given class.
   * @param paramOPs stack holding the operands
   * @param targetID ID of primitive type to convert to.
   * @param targetClass the class to convert to, in case cldID=8
   * @param allownarrowing if narrowing conversions are allowed.
   */
  public OPunary(Stack<OP> paramOPs, int targetID, Class<?> targetClass,
                 boolean allownarrowing) throws CompilationException {
    if (Debug.enabled) {
      // check for proper target type was identification
      Debug.check(((targetID==8)^(targetClass==null)) ||
                   ((targetID!=8)&&
            (specialTypes[targetID].isAssignableFrom(targetClass)))
                   );
      
      if (targetID==8) {
        Debug.check(typeID(targetClass)==targetID,
                     "The type was improperly identified for OPunary conv.");
      };
    };

    // set the result type
    resID=targetID;
    resType=(targetClass!=null?targetClass:specialTypes[targetID]);

    chi=new OP[1];
    chi[0]=paramOPs.pop();

    Class<?> currClass=chi[0].resType;
    int   currID=chi[0].resID;

    int unwrappedCurrID=unwrapType[currID];

    // there are folowing cases:
    // 1) unwrappable object to primitive
    // 2) string-like to string
    // 3) string or string-like to TSB
    // 4) TSB to string
    // 5) primitive (or unwrappable to primitive) to primitive
    // 6) primitive (or unwrappable to primitive) to wrapper object
    // 7) primitive (or onwrappable to primitive) of numeric type into java.lang.Number
    //    or java.lang.Object
    // 8) general object to object

    // object to object 
    // error (object -> primitive, primitive->object)

    code=4+resID;
    if ((resID>=8) && (resID!=10) && (currID!=10) && 
        (!((currID==28) && ((resID==10) || (resID==11))))) {
      // here we have the following cases:
      // 6) primitive (or unwrappable to primitive) to wrapper object
      // 8) general object to object
      boolean boxToSuperclass= 
        ((targetClass == java.lang.Number.class) && 
         (unwrappedCurrID!=0) && (unwrappedCurrID!=2)) ||
        (targetClass == java.lang.Object.class); // box to Number or Object
      if ((unwrappedCurrID<8) && (((resID>=20) && (resID<=27)) || boxToSuperclass)) {
        // 6) primitive (or unwrappable to primitive) to wrapper object (boxing)
        // 7) primitive (or onwrappable to primitive) of numeric type into java.lang.Number
        //    or java.lang.Object
        if (boxToSuperclass)
          resID = 20 + unwrappedCurrID;
        code = 4 + resID - 20; // perform the usual type conversion 
                               // (the object itself is created in compile and eval)
        if (unwrappedCurrID!=currID) {       // TODO (*) eliminate the duplicate block
          uwrpCode=((currID-12+11)<<8)+0x00FE;
          uwrpsTo=unwrappedCurrID;
        }
      } else {
        // 8) general object to object
        code=4+8;
        unwrappedCurrID=8;
      }
    } else {
      // 1) unwrappable object to primitive
      // 2) string-like to string
      // 3) string or string-like to TSB
      // 4) TSB to string
      // 5) primitive (or unwrappable to primitive) to primitive
      if (unwrappedCurrID!=currID) {    // TODO (*) eliminate the duplicate block
        uwrpCode=((currID-12+11)<<8)+0x00FE;
        uwrpsTo=unwrappedCurrID;
      };
    };

    if ((implCode=una[code][unwrappedCurrID])==0xFF) {
      //Debug.println("code="+code);
      // can't convert at all
      Object[] paramsExc={currClass,resType};
      throw new CompilationException(21,paramsExc);
    };

    if (!(allownarrowing || 
          isWidening(currID,currClass,resID,resType))) {
      // can't do narrowing conversions automatically
      Object[] paramsExc={currClass,resType};
      throw new CompilationException(22,paramsExc);
    };
  };

  public void compile(ClassFile cf) {
    if (code==2) cf.code(0xFB); // equivalent to cf.labels_block();
    if (code==14) {
      cf.code(0x0001FE591DFDBBL); // ANY => TSB
      //                      | new
      //                          <CP: java.lang.StringBuffer>
      //                      | dup
      //                      | invokespecial StringBuffer()
      cf.noteStk(-1,10); // pushes ref to TSB
    };
    int cvtResult=resID;
    if ((code>=4) && (code<=4+7) && (resID>=20) && (resID<=27)) {
      cvtResult=resID-20; // result of the type conversion
      // create new object on stack
      cf.code(0x5900FDBBL+((resID)<<16));
      //                      | new
      //                      |  <CP: java.lang.[Boolean, Char, Short, Integer, ...]>
      //                      | dup
      cf.noteStk(-1, 8);
      cf.noteStk(-1, 8); // pushes two refs to the created object
    };

    chi[0].compile(cf);
    
    cf.code(uwrpCode); // unwrap object if needed
    
    if (uwrpsTo>=0)
      cf.noteStk(chi[0].resID,uwrpsTo); // note the unwrapping on stack

    cf.code(implCode);

    if (code==12) // code CP index for conversion to reference
      cf.writeShort(cf.getIndex(resType,9));
    
    // get rid of this switch is the task for the NEXT major update
    switch(code) {
    case 2:  // logical inversion does not change stack
    case 4:  // conversion to boolean does not change stack as well
      //     if it was a jump, jump is preserved  
      break;
    case 3:
    case 13:
      cf.noteStk(resID,-1); // return and (void) throw one word, add nothing
      break;
    case 14:
      cf.noteStk(11,-1);
      break;
    case 1: // bitwise not may have excess item on stack
      cf.noteStk(-1,resID);
      cf.noteStk(resID,-1);
    default: // other ops throw one word replace it by another
      cf.noteStk(uwrpsTo>=0?uwrpsTo:chi[0].resID,cvtResult);
    };

    if ((code>=4) && (code<=4+7) && (resID>=20) && (resID<=27)) {
      // finish creating of the wrapper object by calling its constructor
      cf.code(0x00FEL+((29+resID-20)<<8));
      //                       | invokespecial java.lang.[Boolean, Char,...] ([boolean, char, ...])
      cf.noteStk(resID-20, 0); // the wrapped value is gone from the stack
      cf.noteStk(8,-1);        // one reference to the created wrapper object is gone
      // one reference to the created wrapper object remains
    }

  };

  public Object eval() throws Exception {
    Object operand=chi[0].eval();
    int operand_resID=chi[0].resID;

    if ((code==3) ||
        (code==13) ||
        (code==12)) { // do not evaluate, just replace operand
      chi[0]=new OPload(chi[0],operand);
      throw new Exception(); // bail out
    };

    if (code==2) { // logical not
      if (((Boolean)operand).booleanValue())
        operand=Boolean.FALSE;
      else
        operand=Boolean.TRUE;
    } else if (code<2) {
      Number val=widen(operand,operand_resID);
      switch(code) {
      case 0:  // negation
        if (operand_resID>5)
          val=new Double(-val.doubleValue());
        else
          val=new Long(-val.longValue());
        break;
      case 1:  // bitwise complement
        val=new Long(~val.longValue());
        break;
      default:
        if (Debug.enabled)
          Debug.check(code>=0,"Wrong unary opcode.");
      };
      operand=narrow(val,resID);
    } else {
      // conversion operations
      if (code==14) { // ANY->TSB
        operand=new StringBuffer(String.valueOf(operand));
      } else if (code==15) { // TSB->STR
        operand=operand.toString();
      } else {
        if (resID>=20)
          throw new Exception(); // do not evaluate boxing conversions (can't store result)
        operand=narrow(widen(operand,operand_resID),resID);
      };
    };
    return operand;
  };
};

