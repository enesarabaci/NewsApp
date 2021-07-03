package com.example.newsapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R

class ArticleLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<ArticleLoadStateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.load_state_footer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val progressBar = view.findViewById<ProgressBar>(R.id.footer_progressBar)
        val errorText = view.findViewById<TextView>(R.id.footer_error_text)
        val retryButton = view.findViewById<Button>(R.id.footer_retry_button)
        init {
            retryButton.setOnClickListener {
                retry.invoke()
            }
        }
        fun bind(loadState: LoadState) {
            progressBar.isVisible = loadState is LoadState.Loading
            errorText.isVisible = loadState !is LoadState.Loading
            retryButton.isVisible = loadState !is LoadState.Loading
        }
    }

}