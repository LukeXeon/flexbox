package com.guet.flexbox.litho.drawable.rounded

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.facebook.litho.drawable.ComparableDrawable
import com.guet.flexbox.litho.drawable.LazyImageDrawable

class RoundedLazyImageDrawable(
        context: Context,
        model: Any,
        override val leftTop: Float,
        override val rightTop: Float,
        override val rightBottom: Float,
        override val leftBottom: Float
) : LazyImageDrawable(context, model), RoundedRadius {

    override fun buildRequest(
            builder: RequestBuilder<Drawable>
    ): RequestBuilder<Drawable> {
        val requestBuilder = super.buildRequest(builder)
        return if (hasRounded) {
            requestBuilder.transform(GranularRoundedCorners(
                    leftTop,
                    rightTop,
                    rightBottom,
                    leftBottom
            ))
        } else {
            requestBuilder
        }
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is RoundedLazyImageDrawable
                && super.isEquivalentTo(other)
                && RoundedRadius.equals(this, other)
    }
}