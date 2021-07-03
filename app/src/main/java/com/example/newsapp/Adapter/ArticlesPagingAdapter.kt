package com.example.newsapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Model.Article
import com.example.newsapp.R
import com.example.newsapp.Util.loadImage

class ArticlesPagingAdapter : PagingDataAdapter<Article, ArticlesPagingAdapter.ViewHolder>(diffUtil) {

    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onItemLongClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticlesPagingAdapter.ViewHolder, position: Int) {
        val currentArticle = getItem(position)
        holder.apply {
            image.loadImage(currentArticle?.urlToImage)
            title.text = currentArticle?.title
            author.text = (currentArticle?.author) ?: "Bilinmeyen Yazar"
            root.setOnClickListener {
                onItemClickListener?.let {
                    currentArticle?.let { article ->
                        it(article)
                    }
                }
            }
            root.setOnLongClickListener {
                onItemLongClickListener?.let {
                    currentArticle?.let { article ->
                        it(article)
                    }
                }
                false
            }
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
    fun setOnItemLongClickListener(listener: (Article) -> Unit) {
        onItemLongClickListener = listener
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticlesPagingAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_article_search, parent, false)
        return ViewHolder(view)
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