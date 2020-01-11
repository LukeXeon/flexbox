package com.guet.flexbox.litho

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout.Alignment
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.Text
import com.facebook.litho.widget.VerticalGravity


internal object ToText : ToComponent<Text.Builder>(Common) {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    override val attributeSet: AttributeSet<Text.Builder> by create {
        register("verticalGravity") { _, _, value: VerticalGravity ->
            verticalGravity(value)
        }
        register("horizontalGravity") { _, _, value: Alignment ->
            textAlignment(value)
        }
        register("text") { display, _, value: String ->
            text(value)
            if (!display) {
                textColor(Color.TRANSPARENT)
                textColorStateList(invisibleColor)
            }
        }
        register("clipToBounds") { _, _, value: Boolean ->
            clipToBounds(value)
        }
        register("maxLines") { _, _, value: Double ->
            maxLines(value.toInt())
        }
        register("minLines") { _, _, value: Double ->
            minLines(value.toInt())
        }
        register("textSize") { _, _, value: Double ->
            textSizePx(value.toPx())
        }
        register("textStyle") { _, _, value: Int ->
            typeface(Typeface.defaultFromStyle(value))
        }
        register("ellipsize") { _, _, value: TruncateAt ->
            ellipsize(value)
        }
        register("textColor") { display, _, value: Int ->
            if (display) {
                textColor(value)
            } else {
                textColor(Color.TRANSPARENT)
                textColorStateList(invisibleColor)
            }
        }
    }

    override fun create(c: ComponentContext, visibility: Boolean, attrs: Map<String, Any>): Text.Builder {
        return Text.create(c)
    }
}