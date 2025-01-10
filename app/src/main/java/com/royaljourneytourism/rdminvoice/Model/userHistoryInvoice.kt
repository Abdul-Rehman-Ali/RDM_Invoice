package com.royaljourneytourism.rdminvoice.Model

data class userHistoryInvoice(
    val userId: String = "",
    val packageName: String = "",
    val name: String = "",
    val timeStamp : String = "",
    val totalPrice: Double = 0.0
)
