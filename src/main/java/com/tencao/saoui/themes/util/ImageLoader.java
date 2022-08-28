package com.tencao.saoui.themes.util;

/**
 * Loads JEL-generated classes into Java VM.
 * <P> Specifics of JEL generated classes is that the class name UTF8 is always
 * the first entry in the constant pool. This loader will not load
 * other classes.
 */
public class ImageLoader extends ClassLoader {
    String name;
    byte[] bytes;
    Class<?> c;
    ClassLoader parent;

    private ImageLoader(String name, byte[] bytes) {
        this.name=name;
        this.bytes=bytes;
        this.c=null;
        this.parent=this.getClass().getClassLoader();
    };


    /**
     * Loads given JEL-generated image under its own name.
     * @param image to load
     * @return the class object for the new class or <TT>null</TT>
     *         if unsuccessful.
     */
    public static Class<?> load(byte[] image) {
        // JEL generated class file never have UNICODE class name
        // although they might reference UNICODE methods, etc...
        int len=(image[11]<<8)+image[12];
        char[] nameChr=new char[len];
        for(int i=0;i<len;i++) nameChr[i]=(char)image[13+i];
        String name=String.copyValueOf(nameChr);

        try {
            // load class
            ImageLoader il=new ImageLoader(name,image);
            return il.loadClass(name);
        } catch(Exception ignored) {
        }
        return null;
    };


//    // Please uncomment the code below and use it for loading expressions
//    // (e.g. by commenting the "load" method above and renaming
//    // "loadRenamed"->"load" below) if You have an older version
//    // of JDK (below 1.1.7, AFAIK), which does not allow classes with
//    // the _same_ names to be loaded by _different_ classloaders.
//    //
//    // Number of loaded expressions in this session
//    private transient volatile static long expressionUID=0;

//    /**
//     * Prefix of the expression classname
//     * <P>Example: "gnu.jel.generated.E_"
//     */
//    public static String classNamePrefix="gnu.jel.generated.E_";

//    /**
//     * Loads given JEL-generated image under unique name.
//     * <P>The unique name is generated by appending a steadily incremented
//     * number to the <TT>classNamePrefix</TT>.
//     * @param image to load
//     * @return the class object for the new class or <TT>null</TT>
//     *         if unsuccessful.
//     * @see gnu.jel.ImageLoader#classNamePrefix
//     */
//    public static Class<?> loadRenamed(byte[] image) {
//      if (Debug.enabled)
//        Debug.check(image[10]==1,
//                     "Attempt to load non-JEL generated class file");

//      String newname=classNamePrefix+Long.toString(expressionUID++);
//      String newnameHF=TypesStack.toHistoricalForm(newname);

//      // JEL generated class file never have UNICODE class name
//      // although they might reference UNICODE methods, etc...
//      int len=(image[11]<<8)+image[12];
//      int newlen=newname.length();

//      byte[] newimage=new byte[image.length-len+newlen];

//      System.arraycopy(image,0,newimage,0,11);
//      newimage[11]=(byte)(newlen>>8);
//      newimage[12]=(byte)newlen;
//      for(int i=0;i<newlen;i++)
//        newimage[13+i]=(byte)newnameHF.charAt(i);
//      System.arraycopy(image,13+len,newimage,13+newlen,image.length-13-len);

//      try {
//        // load class
//        ImageLoader il=new ImageLoader(newname,newimage);
//        return il.loadClass(newname);
//      } catch(Exception e) {
//        if (Debug.enabled)
//          Debug.reportThrowable(e);
//      };

//      return null;
//    };

    protected Class<?> loadClass(String name, boolean resolve) throws
            ClassNotFoundException  {
        if (!name.equals(this.name)) {
            if (parent!=null)
                return parent.loadClass(name);
            else
                return findSystemClass(name);
        } else {
            synchronized (name) {
                if(c==null) {
                    c = defineClass(name,bytes, 0, bytes.length,this.getClass().getProtectionDomain());
                    if (resolve) resolveClass(c);
                }
            }
            return c;
        }
    };
};
