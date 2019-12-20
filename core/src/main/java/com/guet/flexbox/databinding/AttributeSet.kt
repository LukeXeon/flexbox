package com.guet.flexbox.databinding

import android.graphics.Color

internal inline val CharSequence.isExpr: Boolean
    get() = length > 3 && startsWith("\${") && endsWith('}')

internal typealias AttributeSet = Map<String, AttributeInfo<*>>

internal inline fun create(crossinline a: HashMap<String, AttributeInfo<*>>.() -> Unit): Lazy<AttributeSet> {
    return lazy {
        HashMap<String, AttributeInfo<*>>().apply(a)
    }
}

internal fun HashMap<String, AttributeInfo<*>>.text(
        name: String,
        scope: (Map<String, String>) = emptyMap(),
        fallback: String = ""
) {
    this[name] = TextAttribute(scope, fallback)
}

internal fun HashMap<String, AttributeInfo<*>>.bool(
        name: String,
        scope: (Map<String, Boolean>) = emptyMap(),
        fallback: Boolean = false
) {
    this[name] = BoolAttribute(scope, fallback)
}

internal fun HashMap<String, AttributeInfo<*>>.value(
        name: String,
        scope: (Map<String, Double>) = emptyMap(),
        fallback: Double = 0.0
) {
    this[name] = ValueAttribute(scope, fallback)
}

internal fun HashMap<String, AttributeInfo<*>>.color(
        name: String,
        scope: (Map<String, Int>) = emptyMap(),
        fallback: Int = Color.TRANSPARENT
) {
    this[name] = ColorAttribute(scope, fallback)
}

internal inline fun <reified V : Enum<V>> HashMap<String, AttributeInfo<*>>.enum(
        name: String,
        scope: Map<String, V>,
        fallback: V = enumValues<V>().first()
) {
    this[name] = EnumAttribute(scope, fallback)
}