package com.tencao.saoui.themes.util.xml

import com.tencao.saoui.themes.util.CValue
import com.tencao.saoui.themes.util.NamedExpressionIntermediate
import com.tencao.saoui.themes.util.Variables
import java.util.LinkedList
import javax.xml.bind.annotation.adapters.XmlAdapter

class NamedExpressionXmlAdapter : XmlAdapter<Variables, Map<String, CValue<*>?>>() {
    override fun unmarshal(v: Variables) = v.variable.associate {
        it.key to it.type.expressionAdapter.compile(it)
    }

    override fun marshal(v: Map<String, CValue<*>?>) = Variables().apply {
        v.map { (key, value) -> NamedExpressionIntermediate().apply {
            this.key = key
            val nei = value?.value?.expressionIntermediate as? NamedExpressionIntermediate
            if (nei != null) {
                this.type = nei.type
                this.expression = nei.expression
                this.cacheType = nei.cacheType
            }
        } }
    }
}