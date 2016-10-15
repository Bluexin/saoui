package com.saomc.saoui.themes.elements;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
class HexArrayAdapter extends XmlAdapter<String[], int[]> {
    @Override
    public int[] unmarshal(String[] v) throws Exception {
        int[] t = new int[v.length];
        for (int i = 0; i < v.length; i++) t[i] = Integer.parseUnsignedInt(v[i].toUpperCase(), 16);
        return t;
    }

    @Override
    public String[] marshal(int[] v) throws Exception {
        String[] t = new String[v.length];
        for (int i = 0; i < v.length; i++) t[i] = Integer.toHexString(v[i]).toUpperCase();
        return t;
    }
}
