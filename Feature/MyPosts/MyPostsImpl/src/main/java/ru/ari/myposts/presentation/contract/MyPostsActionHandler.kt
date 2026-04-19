package ru.ari.myposts.presentation.contract

import androidx.compose.runtime.Stable

@Stable
fun interface MyPostsActionHandler {
    fun onAction(action: MyPostsScreenAction)
}
