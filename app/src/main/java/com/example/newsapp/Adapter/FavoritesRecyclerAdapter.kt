package com.example.newsapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Model.Article
import com.example.newsapp.Model.FavoriteArticle
import com.example.newsapp.R
import com.example.newsapp.Util.loadImage
import com.example.newsapp.Util.makeArticle

class FavoritesRecyclerAdapter : RecyclerView.Adapter<FavoritesRecyclerAdapter.ViewHolder>() {

    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onItemLongClickListener: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_article_search, parent, false)
        return ViewHolder(view)
    }

    private val diffUtil = object: DiffUtil.ItemCallback<FavoriteArticle>() {
        override fun areItemsTheSame(oldItem: FavoriteArticle, newItem: FavoriteArticle): Boolean {
            return oldItem.favoriteUrl == newItem.favoriteUrl
        }

        override fun areContentsTheSame(oldItem: FavoriteArticle, newItem: FavoriteArticle): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
    fun setOnItemLongClickListener(listener: (Article) -> Unit) {
        onItemLongClickListener = listener
    }

    private val recylcerListDiffer = AsyncListDiffer(this, diffUtil)
    var list: List<FavoriteArticle>
        get() = recylcerListDiffer.currentList
        set(value) = recylcerListDiffer.submitList(value)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = list.get(position)
        holder.apply {
            image.loadImage(currentArticle.favoriteUrlToImage)
            title.text = currentArticle.favoriteTitle
            author.text = (currentArticle.favoriteAuthor) ?: "Bilinmeyen Yazar"
            root.setOnClickListener {
                onItemClickListener?.let {
                    val article = currentArticle.makeArticle(true)
                    it(article)
                }
            }
            root.setOnLongClickListener {
                onItemLongClickListener?.let {
                    val article = currentArticle.makeArticle(true)
                    it(article)
                }
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var root: ConstraintLayout
        var image: ImageView
        var title: TextView
        var author: TextView
        init {
            root = view.findViewById(R.id.item_article_search_root)
            image = view.findViewById(R.id.item_article_search_image)
            title = view.findViewById(R.id.item_article_search_title)
            author = view.findViewById(R.id.item_article_search_author)
        }
    }

}