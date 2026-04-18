package ru.ari.managepost.presentation.contract

fun interface ManagePostActionHandler {
    fun onAction(action: ManagePostScreenAction)
}
