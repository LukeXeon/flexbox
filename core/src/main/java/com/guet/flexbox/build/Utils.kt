@file:JvmName("-Utils")

package com.guet.flexbox.build

import android.content.res.Resources
import androidx.annotation.ColorInt
import com.guet.flexbox.el.ELException
import lite.beans.Introspector
import java.io.*
import java.lang.reflect.Type

internal fun Number.toPx(): Int {
    return (this.toDouble() * Resources.getSystem().displayMetrics.widthPixels / 360.0).toInt()
}

private typealias FromJson<T> = (T, Type) -> Any

private object GsonMirror : HashMap<Class<*>, FromJson<*>>(5) {

    init {
        try {
            val gsonType = Class.forName("com.google.gson.Gson")
            val gson = gsonType.newInstance()
            val readerMethod = gsonType.getMethod(
                    "fromJson",
                    Reader::class.java,
                    Type::class.java
            )
            val stringMethod = gsonType.getMethod(
                    "fromJson",
                    String::class.java,
                    Type::class.java
            )
            val map = this
            val converter: FromJson<InputStream> = { data, type ->
                readerMethod.invoke(gson, InputStreamReader(data), type)
            }
            map.add<Reader> { data, type ->
                readerMethod.invoke(gson, data, type)
            }
            map.add(converter)
            map.add<ByteArray> { data, type ->
                converter(ByteArrayInputStream(data), type)
            }
            map.add<File> { data, type ->
                converter(FileInputStream(data), type)
            }
            map.add<String> { data, type ->
                stringMethod.invoke(gson, data, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal fun <T> fromJson(data: Any, type: Type): T? {
        return this[data::class.java]?.let {
            @Suppress("UNCHECKED_CAST")
            return@let (it as FromJson<Any>).invoke(data, type) as T
        }
    }

    private inline fun <reified T> add(noinline action: FromJson<T>) {
        this[T::class.java] = action
    }
}

internal inline fun <reified T:Any> BuildContext.tryGetValue(expr: String?, fallback: T): T {
    if (expr == null) {
        return fallback
    }
    return try {
        getValue(expr)
    } catch (e: ELException) {
        fallback
    }
}

internal inline fun <T> BuildContext.scope(scope: Map<String, Any>, action: () -> T): T {
    enterScope(scope)
    try {
        return action()
    } finally {
        exitScope()
    }
}

internal inline fun <reified T : Enum<T>> BuildContext.tryGetEnum(
        expr: String?,
        scope: Map<String, T>,
        fallback: T = T::class.java.enumConstants[0]): T {
    return when {
        expr == null -> fallback
        expr.isExpr -> scope(scope) {
            tryGetValue(expr, fallback)
        }
        else -> scope[expr] ?: fallback
    }
}

@ColorInt
internal fun BuildContext.tryGetColor(expr: String?, @ColorInt fallback: Int): Int {
    if (expr == null) {
        return fallback
    }
    return try {
        getColor(expr)
    } catch (e: ELException) {
        fallback
    }
}

internal inline val CharSequence?.isExpr: Boolean
    get() = this != null && length > 3 && startsWith("\${") && endsWith('}')

internal fun tryToMap(o: Any): Map<String, Any> {
    return if (o is Map<*, *> && o.keys.all { it is String }) {
        @Suppress("UNCHECKED_CAST")
        return o as Map<String, Any>
    } else {
        @Suppress("UNCHECKED_CAST")
        GsonMirror.fromJson(o, Map::class.java) ?: if (o.javaClass.declaredMethods.isEmpty()) {
            o.javaClass.declaredFields.map {
                it.name to it[o]
            }.toMap()
        } else {
            Introspector.getBeanInfo(o.javaClass)
                    .propertyDescriptors
                    .filter {
                        it.propertyType != Class::class.java
                    }.map {
                        it.name to it.readMethod.invoke(o)
                    }.toMap()
        }
    }
}

internal inline fun <reified N : Number> Number.safeCast(): N {
    return when (N::class) {
        Byte::class -> this.toByte() as N
        Char::class -> this.toChar() as N
        Int::class -> this.toInt() as N
        Short::class -> this.toShort() as N
        Long::class -> this.toLong() as N
        Float::class -> this.toFloat() as N
        Double::class -> this.toDouble() as N
        else -> error("no match number type ${N::class.java.name}")
    }
}