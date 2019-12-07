package com.luke.skywalker.build

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaFlexDirection
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaWrap
import com.luke.skywalker.el.PropsELContext

internal object FlexFactory : WidgetFactory<Component.ContainerBuilder<*>>(
        {
            enumAttr("flexWrap",
                    mapOf(
                            "wrap" to YogaWrap.WRAP,
                            "noWrap" to YogaWrap.NO_WRAP,
                            "wrapReverse" to YogaWrap.WRAP_REVERSE
                    )
            ) { _, _, it ->
                wrap(it)
            }
            enumAttr("justifyContent",
                    mapOf(
                            "flexStart" to YogaJustify.FLEX_START,
                            "flexEnd" to YogaJustify.FLEX_END,
                            "center" to YogaJustify.CENTER,
                            "spaceBetween" to YogaJustify.SPACE_BETWEEN,
                            "spaceAround" to YogaJustify.SPACE_AROUND
                    )
            ) { _, _, it ->
                justifyContent(it)
            }
            enumAttr("alignItems",
                    mapOf(
                            "auto" to YogaAlign.AUTO,
                            "flexStart" to YogaAlign.FLEX_START,
                            "flexEnd" to YogaAlign.FLEX_END,
                            "center" to YogaAlign.CENTER,
                            "baseline" to YogaAlign.BASELINE,
                            "stretch" to YogaAlign.STRETCH
                    )
            ) { _, _, it ->
                alignItems(it)
            }
            enumAttr("alignContent",
                    mapOf(
                            "auto" to YogaAlign.AUTO,
                            "flexStart" to YogaAlign.FLEX_START,
                            "flexEnd" to YogaAlign.FLEX_END,
                            "center" to YogaAlign.CENTER,
                            "baseline" to YogaAlign.BASELINE,
                            "stretch" to YogaAlign.STRETCH
                    )
            ) { _, _, it ->
                alignContent(it)
            }
        }
) {

    private val flexDirections = mapOf(
            "row" to YogaFlexDirection.ROW,
            "column" to YogaFlexDirection.COLUMN,
            "rowReverse" to YogaFlexDirection.ROW_REVERSE,
            "columnReverse" to YogaFlexDirection.COLUMN_REVERSE
    )

    override fun onCreateWidget(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): Component.ContainerBuilder<*> {
        val component: Component.ContainerBuilder<*>
        when (if (attrs != null) {
            data.tryGetEnum(attrs["flexDirection"], flexDirections, YogaFlexDirection.ROW)
        } else {
            YogaFlexDirection.ROW
        }) {
            YogaFlexDirection.COLUMN -> {
                component = Column.create(c)
            }
            YogaFlexDirection.ROW -> {
                component = Row.create(c)
            }
            YogaFlexDirection.COLUMN_REVERSE -> {
                component = Column.create(c)
                        .reverse(true)
            }
            YogaFlexDirection.ROW_REVERSE -> {
                component = Row.create(c)
                        .reverse(true)
            }
            else -> {
                component = Row.create(c)
            }
        }
        return component
    }

    override fun onInstallChildren(
            owner: Component.ContainerBuilder<*>,
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            children: List<Component>?,
            visibility: Int
    ) {
        children?.forEach {
            owner.child(it)
        }
    }

}