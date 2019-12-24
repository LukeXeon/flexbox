package com.guet.flexbox.build

import android.widget.ImageView.ScaleType
import com.facebook.litho.ComponentContext
import com.guet.flexbox.widget.AsyncImage

internal object ImageAdapt : ComponentAdapt<AsyncImage.Builder>(CommonAdapt) {

    override val attributeSet: AttributeSet<AsyncImage.Builder> by create {
        this["scaleType"] = object : Assignment<AsyncImage.Builder, ScaleType>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: ScaleType) {
                scaleType(value)
            }
        }
        this["blurRadius"] = object : Assignment<AsyncImage.Builder, Double>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                blurRadius(value.toFloat())
            }
        }
        this["blurSampling"] = object : Assignment<AsyncImage.Builder, Double>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                blurSampling(value.toFloat())
            }
        }
        this["url"] = object : Assignment<AsyncImage.Builder, String>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: String) {
                if (display) {
                    url(value)
                } else {
                    url("")
                }
            }
        }
    }

    override fun onCreate(c: ComponentContext, type: String, visibility: Boolean, attrs: Map<String, Any>): AsyncImage.Builder {
        return AsyncImage.create(c)
    }
}