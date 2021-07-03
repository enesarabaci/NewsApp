package com.example.newsapp.Util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.newsapp.Model.Article
import com.example.newsapp.R

fun showArticleDialog(article: Article, context: Context, listener: (Article) -> Unit) {
    val dialog = Dialog(context)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    dialog.setContentView(R.layout.dialog_article)
    val image = dialog.findViewById<ImageView>(R.id.dialog_image)
    val title = dialog.findViewById<TextView>(R.id.dialog_title)
    val description = dialog.findViewById<TextView>(R.id.dialog_description)
    val favorite = dialog.findViewById<ImageView>(R.id.dialog_favorite)
    val author = dialog.findViewById<TextView>(R.id.dialog_author)
    image.loadImage(article.urlToImage)
    title.text = article.title
    description.text = article.description
    favorite.setColorFilter(
        ContextCompat.getColor(context,
            setFavoriteColor(article)
        ))
    favorite.setOnClickListener {
        article.isFavorite = !article.isFavorite
        favorite.setColorFilter(
            ContextCompat.getColor(context,
                setFavoriteColor(article)
            ))
        listener.invoke(article)
    }

    author.text = article.author
    dialog.create()
    dialog.show()
}

fun setFavoriteColor(article: Article) : Int = if (article.isFavorite) R.color.red else R.color.white