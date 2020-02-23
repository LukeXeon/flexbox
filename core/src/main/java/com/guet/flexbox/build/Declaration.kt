package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.enums.Visibility
import org.apache.commons.jexl3.JexlContext

abstract class Declaration {

    internal abstract val dataBinding: DataBinding

    open fun onBuildWidget(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            dataContext: JexlContext,
            pageContext: PageContext,
            other: Any?,
            upperVisibility: Boolean = true
    ): List<Any> {
        if (factory == null) {
            return emptyList()
        }
        val selfVisibility = attrs["visibility"]
                ?: Visibility.VISIBLE
        if (selfVisibility == Visibility.GONE) {
            return emptyList()
        }
        val visibility = selfVisibility == Visibility.VISIBLE
                && upperVisibility
        val components = if (children.isEmpty()) {
            emptyList()
        } else {
            buildTool.buildAll(
                    children,
                    dataContext,
                    pageContext,
                    other,
                    visibility
            )
        }
        @Suppress("UNCHECKED_CAST")
        return listOf(factory.create(
                visibility,
                attrs,
                components as List<Nothing>,
                other
        ))
    }

    fun transform(
            bindings: BuildTool,
            rawAttrs: Map<String, String>,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            dataContext: JexlContext,
            pageContext: PageContext,
            other: Any?,
            upperVisibility: Boolean = true
    ): List<Any> {
        val attrs = if (rawAttrs.isNullOrEmpty()) {
            emptyMap()
        } else {
            dataBinding.bind(
                    bindings.engine,
                    dataContext,
                    pageContext,
                    rawAttrs
            )
        }
        return onBuildWidget(
                bindings,
                attrs,
                children,
                factory,
                dataContext,
                pageContext,
                other,
                upperVisibility
        )
    }
}