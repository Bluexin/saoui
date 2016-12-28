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
 * A tree node, representing loading of a constant.
 */
public class OPload extends OP {

  /** Holds an object to be loaded  */
  public Object what;

  /**
   * Creates an OP, loading a constant.
   * @param what is a constant wrapped into a reflection object. E.g 
   *             <TT>java.lang.Integer(1)</TT> to load <TT>1</TT> of
   *             primitive type <TT>int</TT>.
   */
  public OPload(Object what) {
    this.resID=typeIDObject(what);
    
    if (Debug.enabled)
      Debug.check((resID!=8));
    
    this.resType=specialTypes[resID];
    
    this.what=what;
  };

  /**
   * Creates an OP, loading a constant to be put instead of another OP.
   * <P>For private JEL usage in constants folding.
   * @param instead an OP, which will be raplaced by this OPload.
   * @param what is a constant wrapped into a reflection object. E.g 
   *             <TT>java.lang.Integer(1)</TT> to load <TT>1</TT> of
   *             primitive type <TT>int</TT>.
   */
  public OPload(OP instead,Object what) {
    if (Debug.enabled) {
      if (!(
            (
             (typeIDObject(what)==instead.resID) && 
             (instead.resID!=8)
             ) || 
            (
             (instead.resID==10) && 
             (what instanceof StringBuffer)
             )
            )
          ) {
        Debug.println("typeIDObject(what)="+
                      typeIDObject(what));
        Debug.println("instead.resID="+instead.resID);
        Debug.println("what="+what);
        Debug.println("what.getClass()="+what.getClass());
      };

      Debug.check((
                    (typeIDObject(what)==instead.resID) && 
                    (instead.resID!=8)
                    ) || 
                   (
                    (instead.resID==10) && 
                    (what instanceof StringBuffer)
                    )
                   );
    };

    this.resType=instead.resType;
    this.resID=instead.resID;
    this.what=what;
  };

  public Object eval() throws Exception {
    return what;
  };  

  public void compile(ClassFile cf) {
    cf.codeLDC(what,resID);
  };

};
