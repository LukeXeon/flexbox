package com.guet.flexbox.playground.widget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.guet.flexbox.HostingView
import com.guet.flexbox.Page
import com.guet.flexbox.playground.R


class FlexBoxAdapter(
        private val onClick: (v: View, url: String) -> Unit
) : BaseQuickAdapter<Page, BaseViewHolder>(R.layout.feed_item), HostingView.EventHandler {

    override fun handleEvent(v: View, key: String, value: Array<out Any>) {
        onClick(v, key)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<HostingView>(R.id.litho)
        lithoView?.unmountAllItems()
    }

    override fun convert(helper: BaseViewHolder, item: Page) {
        val lithoView = helper.getView<HostingView>(R.id.litho)
        lithoView.setEventHandler(this)
        lithoView.setContentAsync(item)
    }
}