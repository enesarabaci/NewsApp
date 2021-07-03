package com.example.newsapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Model.Article
import com.example.newsapp.R
import com.example.newsapp.Util.loadImage

class ArticlesRecyclerAdapter : RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder>() {

    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onItemLongClickListener: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    private val diffUtil = object: DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
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
    var list: List<Article>
        get() = recylcerListDiffer.currentList
        set(value) = recylcerListDiffer.submitList(value)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = list.get(position)
        holder.apply {
            image.loadImage(currentArticle.urlToImage)
            title.text = currentArticle.title
            author.text = (currentArticle.author) ?: "Bilinmeyen Yazar"
            root.setOnClickListener {
                onItemClickListener?.let {
                    it(currentArticle)
                }
            }
            root.setOnLongClickListener {
                onItemLongClickListener?.let {
                    it(currentArticle)
                }
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var root: CardView
        var image: ImageView
        var title: TextView
        var author: TextView
        init {
            root = view.findViewById(R.id.item_article_root)
            image = view.findViewById(R.id.item_article_image)
            title = view.findViewById(R.id.item_article_title)
            author = view.findViewById(R.id.item_article_author)
        }
    }

}