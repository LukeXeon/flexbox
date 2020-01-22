package com.guet.flexbox.litho

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.HorizontalScroll
import com.facebook.litho.widget.VerticalScroll
import com.guet.flexbox.enums.Orientation
import com.guet.flexbox.build.AttributeSet

internal object ToScroller : ToComponent<Component.Builder<*>>(Common) {

    override val attributeAssignSet: AttributeAssignSet<Component.Builder<*>> by create {
        register("scrollBarEnable") { _, _, value: Boolean ->
            if (this is HorizontalScroll.Builder) {
                scrollbarEnabled(value)
            } else if (this is VerticalScroll.Builder) {
                scrollbarEnabled(value)
            }
        }
        register("fillViewport") { _, _, value: Boolean ->
            if (this is HorizontalScroll.Builder) {
                fillViewport(value)
            } else if (this is VerticalScroll.Builder) {
                fillViewport(value)
            }
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Component.Builder<*> {
        return when (attrs.getOrElse("orientation") { Orientation.HORIZONTAL }) {
            Orientation.HORIZONTAL -> {
                HorizontalScroll.create(c)
            }
            else -> {
                VerticalScroll.create(c)
            }
        }
    }

    override fun onInstallChildren(
            owner: Component.Builder<*>,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
    ) {
        if (children.isNullOrEmpty()) {
            return
        }
        if (owner is HorizontalScroll.Builder) {
            owner.contentProps(children.single())
        } else if (owner is VerticalScroll.Builder) {
            owner.childComponent(children.single())
        }
    }
}