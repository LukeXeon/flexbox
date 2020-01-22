package com.guet.flexbox.litho

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.Layout.Alignment
import android.text.TextUtils
import android.util.ArrayMap
import android.widget.ImageView
import com.facebook.litho.Component
import com.facebook.litho.drawable.ComparableGradientDrawable
import com.facebook.litho.widget.VerticalGravity
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaWrap
import com.guet.flexbox.enums.*
import java.util.*

internal typealias AttributeAssignSet<C> = Map<String, Assignment<C, *>>

private val pt = Resources.getSystem().displayMetrics.widthPixels / 360f

fun <T : Number> T.toPx(): Int {
    return (this.toDouble() * pt).toInt()
}

internal inline fun <T : Component.Builder<*>> create(
        crossinline action: AttrsAssignRegistry<T>.() -> Unit
): Lazy<AttributeAssignSet<T>> {
    return lazy {
        AttrsAssignRegistry<T>().apply(action).value
    }
}

internal typealias ChildComponent = Component

typealias Assignment<C, V> = C.(display: Boolean, other: Map<String, Any>, value: V) -> Unit

private val orientations: Map<String, GradientDrawable.Orientation> = mapOf(
        "t2b" to GradientDrawable.Orientation.TOP_BOTTOM,
        "tr2bl" to GradientDrawable.Orientation.TR_BL,
        "l2r" to GradientDrawable.Orientation.LEFT_RIGHT,
        "br2tl" to GradientDrawable.Orientation.BR_TL,
        "b2t" to GradientDrawable.Orientation.BOTTOM_TOP,
        "r2l" to GradientDrawable.Orientation.RIGHT_LEFT,
        "tl2br" to GradientDrawable.Orientation.TL_BR
)

private fun String.toOrientation(): GradientDrawable.Orientation {
    return orientations.getValue(this)
}

internal fun parseUrl(c: Context, url: CharSequence): Any? {
    when {
        TextUtils.isEmpty(url) -> {
            return null
        }
        url.startsWith("res://") -> {
            val uri = Uri.parse(url.toString())
            when (uri.host) {
                "gradient" -> {
                    val type = uri.getQueryParameter(
                            "orientation"
                    )?.toOrientation()
                    val colors = uri.getQueryParameters("color")?.map {
                        Color.parseColor(it)
                    }?.toIntArray()
                    return if (type != null && colors != null && colors.isNotEmpty()) {
                        ComparableGradientDrawable(type, colors)
                    } else {
                        null
                    }
                }
                "drawable" -> {
                    val name = uri.getQueryParameter("name")
                    if (name != null) {
                        val id = c.resources.getIdentifier(
                                name,
                                "drawable",
                                c.packageName
                        )
                        if (id != 0) {
                            return id
                        }
                    }
                    return null
                }
                else -> {
                    return null
                }
            }
        }
        else -> {
            return url
        }
    }
}

private val mapToLithoValues = ArrayMap<Class<*>, Map<*, Any>>()
        .apply {
            registerToLitho<FlexAlign> {
                for (value in enumValues<FlexAlign>()) {
                    it[value] = YogaAlign.valueOf(value.name)
                }
            }
            registerToLitho<FlexJustify> {
                for (value in enumValues<FlexJustify>()) {
                    it[value] = YogaJustify.valueOf(value.name)
                }
            }
            registerToLitho<FlexWrap> {
                for (value in enumValues<FlexWrap>()) {
                    it[value] = YogaWrap.valueOf(value.name)
                }
            }
            registerToLitho<Horizontal> {
                it[Horizontal.CENTER] = Alignment.ALIGN_CENTER
                it[Horizontal.LEFT] = Alignment.valueOf("ALIGN_LEFT")
                it[Horizontal.RIGHT] = Alignment.valueOf("ALIGN_RIGHT")
            }
            registerToLitho<ScaleType> {
                for (value in enumValues<ScaleType>()) {
                    it[value] = ImageView.ScaleType.valueOf(value.name)
                }
            }
            registerToLitho<TextStyle> {
                it[TextStyle.BOLD] = Typeface.BOLD
                it[TextStyle.NORMAL] = Typeface.NORMAL
            }
            registerToLitho<Vertical> {
                for (value in enumValues<Vertical>()) {
                    it[value] = VerticalGravity.valueOf(value.name)
                }
            }
        }

private inline fun <reified T : Enum<T>>
        ArrayMap<Class<*>, Map<*, Any>>.registerToLitho(
        action: (EnumMap<T, Any>) -> Unit
) {
    val map = EnumMap<T, Any>(T::class.java)
    action(map)
    this[T::class.java] = map
}

internal inline fun <reified T> Enum<*>.mapToLithoValue(): T {
    return mapToLithoValues.getValue(this.javaClass)[this] as T
}
