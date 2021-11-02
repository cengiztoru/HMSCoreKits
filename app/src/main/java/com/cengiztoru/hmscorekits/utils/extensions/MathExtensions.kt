package com.cengiztoru.hmscorekits.utils.extensions

import kotlin.random.Random


fun <T> getRandomItem(list: List<T>): T? {
    if (list.isNullOrEmpty()) return null
    return list[Random.nextInt(list.size)]
}
