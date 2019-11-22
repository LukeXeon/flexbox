package com.guet.flexbox.widget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.drawable.ComparableDrawable
import java.util.concurrent.atomic.AtomicBoolean

internal class ComparableLazyLoadDrawable(
        private val c: Context,
        private val model: Any,
        target: (Target<Drawable>) = DelegateTarget()
) : ComparableDrawableWrapper<Drawable>(NoOpDrawable()), Target<Drawable> by target {

    private val config = Configuration(c.resources.configuration)

    private val hasDrawTask = AtomicBoolean(false)

    override fun draw(canvas: Canvas) {
        if (hasDrawTask.compareAndSet(false, true)) {
            Glide.with(c).load(model).into(this)
        } else {
            super.draw(canvas)
        }
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(bounds.width(), bounds.height())
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other == this) {
            return true
        }
        if (other is ComparableLazyLoadDrawable) {
            return config == other.config && model == other.model
        }
        return false
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        resource.bounds = bounds
        wrappedDrawable = DelegateTarget.transition(null, resource)
        invalidateSelf()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        hasDrawTask.set(false)
    }
}