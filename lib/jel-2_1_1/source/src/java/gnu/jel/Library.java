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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A namespace for JEL expressions.
 * <P> There are two types of members in the library, those which are stateless
 * (i.e. their value depends only on their arguments, if there are any) and
 * stateful (also called here dynamic). The result of evaluation of 
 * stateful members may depend on other factors besides their arguments.
 *
 * <P>Examples of possible stateless members are : <TT>Math.sin(double)</TT>,
 * <TT>Math.PI</TT>.
 * <P>Examples of possible stateful members are : <TT>Math.random()</TT>,
 * <TT>System.currentTimeMillis()</TT>.
 *
 * <P>Stateless members of this library are always static members of the 
 * classes, which define them. The inverse is generally not true. However,
 * this library goes as far as assuming that static members are stateless, 
 * if this assumption does not hold for some of Your members it is possible to 
 * mark them as stateful using the <TT>markStateDependent()</TT> method of
 * this class.
 *
 * <P>The most crucial difference between the two kind of members of this
 * library is that evaluation of stateless methods is attempted by JEL at
 * a compile time during the constants folding phase.
 */
public class Library {

  private HashMap<String,HashMap<String,Member>> names;
  private HashMap<Member,Integer> dynIDs;
  private HashMap<Member,Boolean> stateless;
  private HashMap<Class<?>,HashMap<String,HashMap<String,Member>>> dotClasses;
  private boolean noDotSecurity=false;

  public DVMap resolver;   // directly accessed from EC.jj

  public HashMap<String,Class<?>> cnmap;

  /**
   * Creates a library for JEL.
   * <P> The following should be kept in mind when constructing a library:
   * <OL>
   * <LI>This constructor may throw IllegalArgumentException if it does not
   * like something in Your library. The requirements to the method names
   * are somewhat more strict, than in Java because members of several
   * classes can be merged in root namespace.
   * <LI>When calling the 
   * <TT>CompiledExpression.evaluate(Object[] dynalib)</TT> of the
   * expression, using dynamic library methods it is needed to pass as
   *  <TT>dynalib</TT> parameter the array of objects, of the classes 
   * _exactly_ in the same order as they are appearing in the
   * <TT>dynamicLib</TT> parameter of this constructor. If You do not
   * allow to call dynamic methods (there is no sense, then, to use a compiler)
   * it is possible to pass <TT>null</TT> istead of <TT>dynalib</TT>.
   * <LI> Generally speaking, this class should not let You to create wrong
   * libraries. It's methods will throw exceptions, return <TT>false</TT>'s , 
   * ignore Your actions,... ;)
   * </OL>
   * If methods in the library classes conflict with each other, the last
   * conflicting method will be skipped. You will not get any messages unless
   * debugging is ON (see <TT>gnu.jel.debug.Debug.enabled</TT>). This is
   * done to avoid unnecessary error messages in the production code of the
   * compiler.
   * <P>The array (dotClasses), which is the third argument of 
   * this constructor determines how (and whether) to compile the 
   * dot operators encountered in expressions. These operators are
   * of the form &lt;object&gt;.(&lt;method&gt;|&lt;field&gt;), 
   * which means to call method (access field)
   * of an &lt;object&gt;. There can be three types of the behaviour: 
   * <P>1) dot operators are prohibited (<TT>dotClasses==null</TT>),
   * this is behaviour of older version of JEL.
   * <P>2) dot operators are allowed on all classes
   * (<tt>dotClasses==new Class<?>[0]</tt>, an empty array).
   * Depending on the types of objects returned by the static/dynamic library
   * classes this may pose a security risk.
   * <P>3) dot operators are allowed only on some classes. This is achieved 
   * by listing these classes in the dotClasses array.
   * @param staticLib is the array of classes, whose public static 
   *  methods are exported.
   * @param dynamicLib is the array of classes, whose public virutal
   *  methods are exported.
   * @param dotClasses is the array of classes on which the dot ('.')
   * operation is allowed.
   * @param resolver is the object used to resolve the names.
   * @param cnmap Maps class names into classes for non-primitive type casts.
   */
  public Library(Class<?>[] staticLib,Class<?>[] dynamicLib,Class<?>[] dotClasses,
                 DVMap resolver,HashMap<String,Class<?>> cnmap) {
    this.cnmap=cnmap;
    this.resolver=resolver;
    if (dotClasses==null) {
      this.dotClasses=null;
    } else {
      noDotSecurity=(dotClasses.length==0);
      this.dotClasses=
        new HashMap<Class<?>,HashMap<String,HashMap<String,Member>>>();
      // hash the names
      Class<?>[] temp=new Class<?>[1];
      for(int i=0;i<dotClasses.length;i++) 
        rehash(dotClasses[i]);
    };

    names = new HashMap<String,HashMap<String,Member>>();
    dynIDs = new HashMap<Member,Integer>();
    stateless=new HashMap<Member,Boolean>();

    if (staticLib!=null)
      rehash(staticLib,names,null,stateless);
    if (dynamicLib!=null)
      rehash(dynamicLib,names,dynIDs,null);
  };

  private void rehash(Class<?> cls) {
    HashMap<String,HashMap<String,Member>> tempNames=new HashMap<String,HashMap<String,Member>>();
    Class<?>[] temp=new Class<?>[1];
    temp[0]=cls;
    //    rehash(temp,tempNames,null,new HashMap());
    rehash(temp,tempNames,new HashMap<Member,Integer>(),null);
    dotClasses.put(cls,tempNames);
  };

  private static void rehash(Class<?>[] arr, 
                             HashMap<String,HashMap<String,Member>>hashedNames,
                             HashMap<Member,Integer> dynIDs,
                             HashMap<Member,Boolean> stateless) {
    for (int i=0; i<arr.length; i++) {
      Integer dynID=new Integer(i);

      Method[] marr=arr[i].getMethods();
      Field[] farr=arr[i].getFields();

      int totalMethods=marr.length;
      int totalMembers=totalMethods+farr.length;
      for(int j=0;j<totalMembers;j++) {
        Member m = j<totalMethods?marr[j]:farr[j-totalMethods];
        if ((m.getModifiers() & 0x0008)>0) { // static
          if ((stateless!=null) && rehash(hashedNames,m)) 
            stateless.put(m,Boolean.TRUE);
        } else { // not static
          if ((dynIDs!=null) && rehash(hashedNames,m))
            dynIDs.put(m,dynID);
        };
      };
    };
  };

  private static boolean rehash(HashMap<String,HashMap<String,Member>> hashedNames, Member m) {
    String name=m.getName();
    String signature=getSignature(m);

    // for the purpose of matching fields behave like methods with no
    // arguments
    if (isField(m)) signature="()"+signature;
    
    HashMap<String,Member> signatures=hashedNames.get(name);
    if (signatures==null) { 
      // No method with this name was added
      HashMap<String,Member> signatures_new=new HashMap<String,Member>();
      signatures_new.put(signature,m);
      hashedNames.put(name,signatures_new);
      return true;
    };
    // Name exists in the library, check for possible signature conflict.
    Object conflicting_method=signatures.get(signature);
    if (conflicting_method==null) { // No conflict
      signatures.put(signature,m);
      return true;
    };
//      if (Debug.enabled) {
//        Debug.println("Conflict was detected during the library "+
//                      "initialization."+
//                      " Conflicting "+"\""+name+signature+
//                      "\", conflicting :"+ conflicting_method+" and "+m+" .");
//      };
    // If no debug then the method is ignored.
    return false;
  };

  /**
   * This method marks a static member as having the internal state.
   * <P> If <TT>java.lang.Math</TT> is included into the library it is
   * necessary to mark <TT>java.lang.Math.random()</TT> as having the
   * state. This can be done by calling
   * <TT>markStateDependent("random",null)</TT>.
   * <P> Please specify parameters as close as possible, otherwise you can
   * accidentally mark another function.
   * @param name is the function name.
   * @param params are the possible invocation parameters of the function.
   * @exception CompilationException if the method can't be resolved   
   */
  public void markStateDependent(String name, Class<?>[] params) 
       throws CompilationException {
    Object m;
    try {
      m=getMember(null,name,params);
    } catch (CompilationException exc) {
      if (exc.col<0) exc.col=0;
      throw exc;
    }
    Object removed=stateless.remove(m);
    if (Debug.enabled)
      Debug.check(removed!=null,"State dependent methos \""+m+
		   "\"is made state dependend again.");
  };
  
  /**
   * Used to check if the given method is stateless.
   * @param o is method or field to check.
   * @return true if the method is stateless and can be invoked at
   *    compile time.
   */
  public boolean isStateless(Member o) {
    return stateless.containsKey(o);
  };

  /**
   * Searches the namespace defined by this library object for method or field.
   * <P> The method with the same name, and closest (convertible) parameter
   * types is returned. If there are several methods the most specific one
   * is used. 
   * <P>Ambiguities are detected. Example of detectable ambiguity:<BR>
   * you ask for a call <TT>someName(int, int)</TT>, but there are two
   * applicable methods <TT>someName(int, double)</TT> and
   * <TT>someName(double, int)</TT>. Requirements to parameter types
   * of both can be satisfied by _widening_ conversions. Thus, there
   * is no most specific method of these two in terms of Java Language
   * Specification (15.11.2.2). It means, that an ambiguity is present,
   * and null will be returned.
   * <P> Java compiler normally would not allow to define such ambiguous 
   * methods in the same class. However, as this library is assembled from
   * several Java classes, such ambiguities can happen, and should be
   * detected.
   * @param container the class to search the method within, if <TT>null</TT>
   *                  the root namespace is searched.
   * @param name is the name of the method to find.
   * @param params are the types of formal parameters in the method invocation.
   * @return the method/field object of the resolved method/field.
   * @exception CompilationException if the method can't be resolved
   */
  public Member getMember(Class<?> container,String name,Class<?>[] params) 
    throws CompilationException {
    HashMap<String,HashMap<String,Member>> hashedMembers=names;

    // dot operator security    
    if (container!=null) {
      if (dotClasses==null) // dot operator is prohibited
        throw new CompilationException(11,null);
      else if (! (noDotSecurity || dotClasses.containsKey(container))) {
        // dot is not allowed in this particular class
        Object[] paramsExc={container};
        throw new CompilationException(12,paramsExc);
      };
      if ((hashedMembers=dotClasses.get(container))==null) {
        rehash(container);
        hashedMembers=dotClasses.get(container);
      };
    };
    

    HashMap<String,Member> signatures=hashedMembers.get(name);

    //      System.out.print("RESOLVING: ");
    //      System.out.println(describe(name,params));

    if (signatures==null) { // name is not found
      Object[] paramsExc={name,container};
      throw new CompilationException(container==null?5:6,paramsExc);
    };
    
    // Choose applicable methods
    List<Member> applicable_methods=
      getApplicable(container, name, params, signatures,-1);
    
    if (applicable_methods.size()==0) {
      // try varargs match
      List<Member> applicable_methods_with_varargs=
        getApplicable(container, name, params, signatures,params.length);
      if (applicable_methods_with_varargs.size()==0) {
        Object[] paramsExc={name,describe(name,params),container};
        throw new CompilationException(container==null?7:8,paramsExc);
      } else {
        return getMostSpecific(container, name, params, 
                               applicable_methods_with_varargs,params.length);
      }
    } else {
      return getMostSpecific(container, name, params, applicable_methods,-1);
    }
  }

  /**
   * Returns applicable methods, for the call.
   * @param container class where the methods come from (if null the
   *        method is assumed to be from static and dynamic libraries).
   * @param name method name.
   * @param params parameter types in the call
   * @param signatures list of method signatires to choose from.
   * @param np number of parameters in the call (must be equal either to
   *        params.length for a varargs match or to -1 for usual match).
   */
  protected List<Member> getApplicable(Class<?> container,String name,Class<?>[] params, 
                                                   HashMap<String,Member> signatures, int np) {
    if (Debug.enabled)
      Debug.check((np==-1||(np==params.length)));    
    ArrayList<Member> applicable_methods=new ArrayList<Member>();
    for(Member cm: signatures.values()) {
      Class<?>[] cp=getParameterTypes(cm,np);
      
      boolean applicable=false;
      if (params!=null) { // if no parameters requested
        if (cp.length==params.length) {  // If number of arguments matches
          applicable=true;
          for(int i=0;((i<cp.length) && applicable);i++) {
            applicable=OP.isWidening(params[i],cp[i]);
          };
        };
      } else {
        applicable=(cp.length==0);
      };
      
      if (applicable) applicable_methods.add(cm);
    };
    return applicable_methods;
  }
  
  /**
   * Returns the most specific method, matching the call.
   * @param container class where the methods come from (if null the
   *        method is assumed to be from static and dynamic libraries).
   * @param name method name.
   * @param params parameter types in the call
   * @param applicable_methods list of applicable methods to choose from.
   * @param np number of parameters in the call (must be equal either to
   *        params.length for a varargs match or to -1 for usual match).
   */
  protected Member getMostSpecific(Class<?> container,String name,Class<?>[] params, 
                                   List<Member> applicable_methods, int np)
    throws CompilationException {
    if (Debug.enabled)
      Debug.check((np==-1||(np==params.length)));
    // Search for the most specific method
    Iterator<Member> e=applicable_methods.iterator();
    Member most_specific=e.next();
    Class<?>[] most_specific_params=getParameterTypes(most_specific,np);
    
    //    System.out.println("--- APPLICABLE METHODS ---");
    //    System.out.println(most_specific.getName()+
      //                       ClassFile.getSignature(most_specific));
    while (e.hasNext()) {
      Member cm= e.next();
      Class<?>[] cp=getParameterTypes(cm,np);
      boolean moreSpecific=true;
      boolean lessSpecific=true;
      
      //      System.out.println(cm.getName()+ClassFile.getSignature(cm));
        
      for(int i=0; i<cp.length; i++) {
        moreSpecific = moreSpecific && 
          OP.isWidening(cp[i],most_specific_params[i]);
        lessSpecific = lessSpecific &&
          OP.isWidening(most_specific_params[i],cp[i]);
      };
      
      if (moreSpecific && (!lessSpecific)) {
        most_specific=cm;
        most_specific_params=cp;
      };
      
      if (! (moreSpecific ^ lessSpecific)) {
        Object[] paramsExc={describe(name,most_specific_params),
                            describe(name,cp),
                            describe(name,params),
                            container};
        throw new CompilationException(container==null?9:10,paramsExc);        
      }
    }
    //    System.out.println("--- END APPLICABLE METHODS ---");
    return most_specific;
  }

  protected static String describe(String name,Class<?>[] params) {
    StringBuilder invp=new StringBuilder();
    invp.append(name); 
    invp.append('(');
    
    if (params!=null)
      for(int k=0;k<params.length;k++) {
        if (k!=0) invp.append(',');
        invp.append(params[k].toString());
      };
    invp.append(')');
    return invp.toString();
  };
  
  /**
   * Returns ID (position in the object array) of the dynamic method.
   * <P> ID's are used to locate the pointers to the objects, implementing
   * dynamic methods, in the array, argument of evaluate(Object[]) function.
   * @param m method to get an ID of.
   * @return the ID of the method or -1 if the method is static.
   * @exception NullPointerException if method is not a dynamic method of
   *            this library.
   */
  public int getDynamicMethodClassID(Member m) {
    Integer id=dynIDs.get(m);
    if (id==null) return -1;
    return id.intValue();
  };

    /**
   * Used to get return type of a class member.
   * <P>The type of a method is its return type, the type of a constructor is
   * void.
   * @param  m member whose type is to be determined
   * @return type of the member
   */
  public static Class<?> getType(Member m) {
    if (m instanceof Method) return ((Method)m).getReturnType();
    if (m instanceof Field) return ((Field)m).getType();
    if (m instanceof LocalField) return ((LocalField)m).getType();
    // otherwise it must be java.lang.reflect.Constructor
    if (Debug.enabled)
      Debug.check(m instanceof java.lang.reflect.Constructor);
    return OP.specialTypes[9]; // java.lang.reflect.Void.TYPE
  };

  /**
   * Used to get types of formal parameters of a member.
   * <P> The reference to the class instance "this" is not counted by
   * this method.
   * @param  m member whose formal parameters are to be obtained
   * @param  np numer of parameters for varargs expansion (if np==-1
   *         or the last argument is not an array, the expansion is not 
   *         performed). It is also an error to pass the np, smaller
   *         then the number of method parameters.
   * @return array of formal parameter types (empty array if none).
   */
  public static Class<?>[] getParameterTypes(Member m, int np) {
    Class<?>[] params=null;
    if (m instanceof Method) params=((Method)m).getParameterTypes();
    if (m instanceof LocalMethod) params=((LocalMethod)m).getParameterTypes();
    if (m instanceof Constructor) params=((Constructor)m).getParameterTypes();
    if (params!=null) {
      if ((np<params.length) || (params.length==0) || (!params[params.length-1].isArray()))
        return params;
      else { // varargs expansion
        Class<?>[] newparams=new Class<?>[np];
        for (int i=0; i<params.length-1; i++)
          newparams[i]=params[i];
        for (int i=params.length-1; i<np; i++)
          newparams[i]=params[params.length-1].getComponentType();
        return newparams;
      }
    } else {
      if (Debug.enabled)
        Debug.check((m instanceof Field)||(m instanceof LocalField));
      return new Class<?>[0];
    }
  };

  /**
   * Computes signature of the given member.
   * @param m the member to compute the sugnature of.
   * @return the signature.
   */
  public static String getSignature(Member m) {
    StringBuilder signature=new StringBuilder();
    if (!isField(m)) {
      Class<?> parameters[]=getParameterTypes(m,-1);
      signature.append('(');
      for(int i=0;i<parameters.length;i++) 
        appendSignature(signature,parameters[i]);
      signature.append(')');
    };
    appendSignature(signature,getType(m));
    return signature.toString();
  };

  public static boolean isField(Member m) {
    return (m instanceof Field) || ((m instanceof LocalField)
                                    && !(m instanceof LocalMethod));
  };

  /**
   * Computes the signature of the given class.
   * <P> The signature of the class (Field descriptor) is the string and 
   * it's format is described in the paragraph 4.3.2 of the Java VM 
   * specification (ISBN 0-201-63451-1).
   * <P>The same can be done using <TT>java.lang.Class.getName()</TT> by 
   * converting it's result into the "historical form".
   * <P> This utility method can be used outside of the JEL package
   * it does not involve any JEL specific assumptions and should follow
   * JVM Specification precisely.
   * @param cls is the class to compute the signature of. Can be primitive or
   *            array type.
   * @return the class signature.
   */
  public static String getSignature(Class<?> cls) {
    return appendSignature(new StringBuilder(),cls).toString();
  };

  private static StringBuilder appendSignature(StringBuilder buff, Class<?> cls) {
    if (cls.isPrimitive()) {
      int tid;
      buff.append((tid=OP.typeID(cls))>9?'L':"ZBCSIJFDLV".charAt(tid));
    } else if (cls.isArray()) {
      buff.append('[');
      appendSignature(buff,cls.getComponentType());
    } else { // just a class
      buff.append('L');
      appendHistoricalForm(buff,cls.getName());
      buff.append(';');
    };
    return buff;
  };

  public static String toHistoricalForm(String className) {
    return appendHistoricalForm(new StringBuilder(),className).toString();
  };

  private static StringBuilder appendHistoricalForm(StringBuilder buff,
                                                   String className) {
    int namelen=className.length();
    for(int i=0;i<className.length();i++) {
      char cch=className.charAt(i);
      if (cch=='.') cch='/';
      buff.append(cch);
    };
    return buff;
  };

  
};
