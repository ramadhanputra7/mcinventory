package com.azhara.inventarisbarang.home.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.entity.Product
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_product.view.*

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK){

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>(){
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.totalItem == newItem.totalItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }

        }
    }

    private var onOptionClicked: OnOptionClicked? = null

    fun setOnOptionClicked(onOptionClicked: OnOptionClicked?){
        this.onOptionClicked = onOptionClicked
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductAdapter.ProductViewHolder {
        return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_product, parent, false))
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(product: Product){
            with(itemView){
                if (product.imgUrl != null){
                    Glide.with(context).load(product.imgUrl).into(img_product_item)
                }
                tv_product_name_item.text = product.productName
                tv_total_product_item.text = "${product.totalItem} items"
                btn_option_item.setOnClickListener {
                    onOptionClicked?.onOptionClicked(product)
                }
            }
        }
    }

    interface OnOptionClicked{
        fun onOptionClicked(product: Product)
    }

}