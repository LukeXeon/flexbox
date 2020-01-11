package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

object If : Declaration() {

    override val attributeSet: AttributeSet by create {
        bool("test")
    }

    override fun onBuild(
            bindings: BuildUtils,
            attrs: Map<String, Any>,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Any> {
        if (attrs.getValue("test") as Boolean) {
            return children.map {
                bindings.bindNode(
                        it,
                        pageContext,
                        data,
                        upperVisibility,
                        other
                )
            }.flatten()
        }
        return emptyList()
    }
}