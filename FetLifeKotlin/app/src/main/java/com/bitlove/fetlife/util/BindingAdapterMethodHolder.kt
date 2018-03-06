package com.bitlove.fetlife.util

import android.databinding.BindingAdapter
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.text.Html
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.view.get
import androidx.view.size
import com.android.databinding.library.baseAdapters.BR
import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.dataobject.Comment
import com.bitlove.fetlife.loadWithGlide
import com.bitlove.fetlife.viewmodel.generic.CommentViewDataHolder

@BindingAdapter("comments", "commentsDisplayed")
fun setComments(viewGroup: ViewGroup,
                comments: List<Comment>?, commentsDisplayed: Boolean?) {
    if (viewGroup.visibility == View.GONE) {
        return
    }
    if (comments == null || comments.isEmpty()) {
        viewGroup.removeAllViews()
        return
    }
    val inflater = viewGroup.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    val maxComments = Math.min(comments.size,if (commentsDisplayed == true) CommentViewDataHolder.COMMENT_MAX_COUNT_EXPANDED else CommentViewDataHolder.COMMENT_MAX_COUNT_COLLAPSED)
    for (i in comments.indices) {
        val binding = if (viewGroup.size > i) viewGroup[i].tag as ViewDataBinding else DataBindingUtil
                .inflate<ViewDataBinding>(inflater, R.layout.item_data_card_comment, viewGroup, true)
        binding.root.tag = binding
        if (i < comments.size - maxComments) {
            binding.root.visibility = View.GONE
        } else {
            binding.root.visibility = View.VISIBLE
            val comment = comments[i].copy()
            if (commentsDisplayed != true && comment.body != null) {
                if (comment.body!!.length > Comment.TRUNCATED_LENGTH) {
                    comment.body = comment.body!!.substring(0,Comment.TRUNCATED_LENGTH)
                    comment.body = comment.body!!.substring(0,comment.body!!.lastIndexOf(' ')) + Comment.TRUNCATED_SUFFIX
                }
            }
            binding.setVariable(BR.commentData, comment)
//            binding.root.findViewById<TextView>(R.id.comment_body)?.maxLines = if (commentsDisplayed == true) 100 else CommentViewDataHolder.COMMENT_WRAP_MAX_LINES
        }
    }
}

@BindingAdapter("formattedText", "textEntities", "mediaEntityHolder", "removeLineBreaks")
fun setFormattedText(textView: TextView?, formattedText: String?, textEntities : String? = null, mediaEntityHolder : Int, removeLineBreaks : Boolean? = false) {
    if (formattedText == null) {
        textView?.text = null
        return
    }
    var formattedString = formattedText
    if (removeLineBreaks == true) {
        formattedString = formattedText?.replace("\\s".toRegex()," ")
    }
    //TODO Add Text entities to the appropriate view
    textView?.text = Html.fromHtml(formattedString)
}


@BindingAdapter("srcGlide")
fun setGlideSrc(imageView: ImageView, srcGlide: String?) {
    imageView.loadWithGlide(srcGlide, R.mipmap.ic_launcher_round)
}

//TODO: check out other list bindings:
// https://medium.com/google-developers/android-data-binding-list-tricks-ef3d5630555e
// https://github.com/evant/binding-collection-adapter

//Generic List Binding
//@BindingAdapter("entries", "layout")
//fun <T> setComments(viewGroup: ViewGroup,
//                   entries: List<T>?, layoutId: Int) {
//    viewGroup.removeAllViews()
//    if (entries == null) {
//        return
//    }
//    val inflater = viewGroup.context
//            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//    for (i in entries.indices) {
//        val entry = entries[i]
//        val binding = DataBindingUtil
//                .inflate<ViewDataBinding>(inflater, layoutId, viewGroup, true)
//        binding.setVariable(BR.data, entry)
//    }
//}