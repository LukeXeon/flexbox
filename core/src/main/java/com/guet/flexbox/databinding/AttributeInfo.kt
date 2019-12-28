package com.guet.flexbox.databinding

import com.guet.flexbox.el.PropsELContext

internal abstract class AttributeInfo<T : Any>(
        protected val scope: (Map<String, T>) = emptyMap(),
        protected val fallback: T? = null
) {
    abstract fun cast(
            props: PropsELContext,
            raw: String
    ): T?
}
