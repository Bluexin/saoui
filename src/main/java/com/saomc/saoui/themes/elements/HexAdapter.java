package com.saomc.saoui.themes.elements;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
class HexAdapter extends XmlAdapter<String, Integer> {
    @Override
    public Integer unmarshal(String v) throws Exception {
        return Integer.parseUnsignedInt(v.toUpperCase(), 16);
    }

    @Override
    public String marshal(Integer v) throws Exception {
        return Integer.toHexString(v).toUpperCase();
    }
}
