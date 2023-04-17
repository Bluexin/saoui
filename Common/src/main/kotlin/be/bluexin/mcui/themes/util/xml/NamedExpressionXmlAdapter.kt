package be.bluexin.mcui.themes.util.xml

import be.bluexin.mcui.themes.util.CValue
import be.bluexin.mcui.themes.util.NamedExpressionIntermediate
import be.bluexin.mcui.themes.util.Variables
import javax.xml.bind.annotation.adapters.XmlAdapter

class NamedExpressionXmlAdapter : XmlAdapter<Variables, Map<String, CValue<*>?>>() {
    override fun unmarshal(v: Variables) = v.variable.associate {
        it.key to if (it.expression.isEmpty()) null else it.type.expressionAdapter.compile(it)
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