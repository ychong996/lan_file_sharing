package com.ychong.lan_file_sharing.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ychong.lan_file_sharing.databinding.LayoutEmptyBinding
import com.ychong.lan_file_sharing.databinding.LayoutHeadBinding
import com.ychong.lan_file_sharing.databinding.LayoutListFooterBinding
import com.ychong.lan_file_sharing.databinding.LayoutListHeaderBinding

abstract class BaseRecyclerAdapter<T>(private var list: MutableList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_HEADER = 1001
    private val TYPE_FOOTER = 1002
    private val TYPE_EMPTY = 1003

    private var isHeader: Boolean = false
    private var isFooter: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val headerBinding = getHeaderBinding(parent)
                HeaderViewHolder(headerBinding)
            }
            TYPE_FOOTER -> {
                val footerBinding = getFooterBinding(parent)
                FooterViewHolder(footerBinding)
            }
            TYPE_EMPTY -> {
                val emptyBinding =
                    LayoutEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EmptyViewHolder(emptyBinding)
            }
            else -> {
                val itemBinding = getItemBinding(parent)
                ItemViewHolder(itemBinding)
            }
        }
    }

    abstract fun getItemBinding(parent: ViewGroup): ViewBinding
    open fun getHeaderBinding(parent: ViewGroup): ViewBinding {
        return LayoutListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
    open fun getFooterBinding(parent: ViewGroup): ViewBinding {
        return LayoutListFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEADER -> {
                holder as HeaderViewHolder
                convertHeader(holder)

            }
            TYPE_FOOTER -> {
                holder as FooterViewHolder
                convertFooter(holder)
            }
            TYPE_EMPTY -> {
                holder as EmptyViewHolder
                if (list.isNullOrEmpty()) {
                    holder.binding.emptyLayout.visibility = View.VISIBLE
                } else {
                    holder.binding.emptyLayout.visibility = View.GONE
                }

            }
            else -> {
                holder as ItemViewHolder
                convert(holder, position - 1)
            }
        }
    }

    open fun convertHeader(holder: HeaderViewHolder) {}
    open fun convertFooter(holder: FooterViewHolder) {}
    fun convertEmpty(holder: EmptyViewHolder) {}
    abstract fun convert(holder: ItemViewHolder, position: Int)

    override fun getItemCount(): Int {
        var count = 0
        return if (list.isNullOrEmpty()) {
            count = 2
            count
        } else {
            count = 2
            list.size.plus(count)

        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEADER
        } else if (itemCount == 2 && position == itemCount - 1) {
            return TYPE_EMPTY
        } else if (position == itemCount - 1) {
            return TYPE_FOOTER
        }
        return position
    }

    class ItemViewHolder(var binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    class HeaderViewHolder(var binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FooterViewHolder(var binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    class EmptyViewHolder(var binding: LayoutEmptyBinding) : RecyclerView.ViewHolder(binding.root)

}