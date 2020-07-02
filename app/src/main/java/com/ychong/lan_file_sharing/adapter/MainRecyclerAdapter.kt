package com.ychong.lan_file_sharing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ychong.lan_file_sharing.data.MenuBean
import com.ychong.lan_file_sharing.databinding.ItemMenuBinding

class MainRecyclerAdapter(var list: MutableList<MenuBean>) :
    RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder>() {
    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }


    public fun setData(list: MutableList<MenuBean>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }
    fun clearData(){
        this.list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainRecyclerAdapter.MainViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (list.isNullOrEmpty()) {
            0
        } else {
            list.size
        }
    }

    override fun onBindViewHolder(holder: MainRecyclerAdapter.MainViewHolder, position: Int) {
        val item = list[position]
        holder.binding.menuTv.text = item.name
        holder.binding.menuTv.setOnClickListener {
            itemClickListener.onClick(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    class MainViewHolder(var binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onClick(item: MenuBean)
    }
}