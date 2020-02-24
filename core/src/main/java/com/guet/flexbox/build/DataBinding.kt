package com.guet.flexbox.build

import android.graphics.Color
import android.util.ArrayMap
import com.guet.flexbox.PageContext
import com.guet.flexbox.el.ScopeContext
import com.guet.flexbox.event.EventHandler
import com.guet.flexbox.event.ScriptHandler
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

internal class DataBinding(
        private val parent: DataBinding? = null,
        private val data: Map<String, TextToAttribute<*>>
) {

    private fun find(name: String): TextToAttribute<*>? {
        val v = data[name]
        if (v != null) {
            return v
        }
        if (parent != null) {
            return parent.find(name)
        }
        return null
    }

    fun bind(
            engine: JexlEngine,
            dataContext: JexlContext,
            pageContext: PageContext,
            map: Map<String, String>
    ): Map<String, Any> {
        val output = ArrayMap<String, Any>(map.size)
        map.forEach {
            val binder = find(it.key)
            if (it.value.isNotEmpty()) {
                val o = binder?.cast(
                        engine,
                        dataContext,
                        pageContext,
                        it.value
                )
                if (o != null) {
                    output[it.key] = o
                }
            }
        }
        return output
    }

    companion object {

        val empty = DataBinding(null, emptyMap())

        inline fun create(
                parent: DataBinding? = null,
                crossinline action: Builder.() -> Unit
        ): Lazy<DataBinding> {
            return lazy {
                Builder().apply(action).build(parent)
            }
        }
    }

    class Builder {

        private val value = ArrayMap<String, TextToAttribute<*>>()

        fun text(
                name: String,
                scope: (Map<String, String>) = emptyMap(),
                fallback: String = ""
        ) {
            value[name] = object : TextToAttribute<String> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        pageContext: PageContext,
                        raw: String
                ): String? {
                    return if (raw.isExpr) {
                        try {
                            val expr = engine.createExpression(raw.innerExpr)
                            val o = expr.evaluate(ScopeContext(scope, dataContext))
                            o?.toString() ?: fallback
                        } catch (e: Throwable) {
                            raw
                        }
                    } else {
                        raw
                    }
                }
            }
        }

        fun bool(
                name: String,
                scope: Map<String, Boolean> = emptyMap(),
                fallback: Boolean = false
        ) {
            value[name] = object : TextToAttribute<Boolean> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        pageContext: PageContext,
                        raw: String
                ): Boolean? {
                    return if (raw.isExpr) {
                        val expr = engine.createExpression(raw.innerExpr)
                        val o = expr.evaluate(ScopeContext(scope, dataContext))
                        o as? Boolean ?: fallback
                    } else {
                        raw.toBoolean()
                    }
                }
            }
        }

        fun value(
                name: String,
                scope: Map<String, Float> = emptyMap(),
                fallback: Float = 0f
        ) {
            value[name] = object : TextToAttribute<Float> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        pageContext: PageContext,
                        raw: String
                ): Float? {
                    return if (raw.isExpr) {
                        val expr = engine.createExpression(raw.innerExpr)
                        val o = expr.evaluate(ScopeContext(scope, dataContext))
                        (o as? Number)?.toFloat() ?: fallback
                    } else {
                        raw.toFloatOrNull() ?: fallback
                    }
                }
            }
        }

        fun color(
                name: String,
                scope: Map<String, Int> = emptyMap(),
                fallback: Int = Color.TRANSPARENT
        ) {
            value[name] = object : TextToAttribute<Int> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        pageContext: PageContext,
                        raw: String
                ): Int? {
                    return if (raw.isExpr) {
                        val expr = engine.createExpression(raw.innerExpr)
                        val o = expr.evaluate(ScopeContext(colorScope,
                                ScopeContext(scope, dataContext)
                        ))
                        try {
                            Color.parseColor((o as? String) ?: "")
                        } catch (e: Throwable) {
                            fallback
                        }
                    } else {
                        try {
                            Color.parseColor(raw)
                        } catch (e: Throwable) {
                            fallback
                        }
                    }
                }
            }
        }


        fun <T : Any> typed(
                name: String,
                obj: TextToAttribute<T>
        ) {
            value[name] = obj
        }

        inline fun <reified V : Enum<V>> enum(
                name: String,
                scope: Map<String, V>,
                fallback: V = enumValues<V>().first()
        ) {
            typed(name, object : TextToAttribute<V> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        pageContext: PageContext,
                        raw: String
                ): V? {
                    return if (raw.isExpr) {
                        val expr = engine.createExpression(raw.innerExpr)
                        val o = expr.evaluate(ScopeContext(scope, dataContext))
                        o as? V ?: fallback
                    } else {
                        scope[raw] ?: fallback
                    }
                }
            })
        }

        fun event(
                name: String
        ) {
            value[name] = object : TextToAttribute<EventHandler> {
                override fun cast(
                        engine: JexlEngine,
                        dataContext: JexlContext,
                        pageContext: PageContext,
                        raw: String
                ): EventHandler? {
                    return if (raw.isExpr) {
                        ScriptHandler(
                                pageContext,
                                engine.createScript(raw.innerExpr),
                                dataContext
                        )
                    } else {
                        null
                    }
                }
            }
        }

        internal fun build(
                parent: DataBinding? = null
        ): DataBinding {
            return DataBinding(parent, value)
        }
    }
}