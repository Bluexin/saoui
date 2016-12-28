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
 * Specialized stack which works with integers.
 */
class IntegerStack {
  private int[] data;
  private int count=0;
  
  public IntegerStack(int initCapacity) {
    data=new int[initCapacity];
  };

  public IntegerStack() {
    this(30);
  };
  
  public IntegerStack copy() {
    IntegerStack res=new IntegerStack(data.length);
    res.count=count;
    for(int i=0;i<count;i++)
      res.data[i]=data[i];
    //    in most cases actually empty stacks are cloned in JEL
    //    System.arraycopy(data,0,res.data,0,count);
    return res;
  };

  public final void push(int what) {
    if (count>=data.length) incCap(count+1);
    data[count++]=what;
  };
  
  public final int peek() {
    return data[count-1];
  };

  public final int pop() {
    return data[--count];
  };

  public final int size() {
    return count;
  };

  // Swaps values above given limits in two stacks
  public static void swap(IntegerStack one,int oneLim,
                          IntegerStack other,int otherLim) {
    // this is used for swapping labels in logical expressions compilation
    // usually there are not so many labels to swap... and the element
    // by element copy should not incur significant peformance penalty
    // ordering of elements between limits is not important
    
    IntegerStack temp=null;
    if (one.size()>oneLim)
      temp=new IntegerStack();

    //    System.out.println("vgv  one.size()= "+one.size()+" ( "+oneLim+" )"+
    //                   "  other.size()= "+other.size()+" ( "+otherLim+" )");

    while (one.size()>oneLim)
      temp.push(one.pop());
    while (other.size()>otherLim)
      one.push(other.pop());
    while ((temp!=null) && (temp.size()>0))
      other.push(temp.pop());

// ----- faster version of the same
//      int copyFromOne=one.count-oneLim;
//      int copyFromOther=other.count-otherLim;
//      boolean cf_one=copyFromOne>0;
//      boolean cf_other=copyFromOther>0;
//      if ((cf_one) || (cf_other)) {
//        int nSizeOne=oneLim+copyFromOther;
//        int nSizeOther=otherLim+copyFromOne;
//        // ensure capacities
//        if (nSizeOne>one.data.length) one.incCap(nSizeOne);
//        if (nSizeOther>other.data.length) other.incCap(nSizeOther);
//        int[] temp=null;
//        if (cf_one) {
//          temp=new int[copyFromOne];
//          System.arraycopy(one.data,oneLim,temp,0,copyFromOne);
//        };
//        if (cf_other)
//         System.arraycopy(other.data,otherLim,one.data,oneLim,copyFromOther);
//        if (cf_one)
//          System.arraycopy(temp,0,other.data,otherLim,copyFromOne);
//        one.count=nSizeOne;
//        other.count=nSizeOther;
//      };
// ----- end of faster version of the same

  };

  private void incCap(int minCapacity) {
    int[] old_data=data;
    int oldSize=data.length;
    int newSize=oldSize*2;
    if (newSize<minCapacity) newSize=minCapacity;
    data=new int[newSize];
    System.arraycopy(old_data,0,data,0,count);
  };

};
