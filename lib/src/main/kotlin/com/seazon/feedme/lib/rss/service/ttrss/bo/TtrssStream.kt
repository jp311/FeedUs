package com.seazon.feedme.lib.rss.service.ttrss.bo

import com.seazon.feedme.lib.rss.bo.Entity
import com.seazon.feedme.lib.rss.bo.RssItem
import com.seazon.feedme.lib.rss.bo.RssStream
import com.seazon.feedme.lib.rss.service.convert
import com.seazon.feedme.lib.utils.orZero
import com.seazon.feedme.lib.utils.toJson
import kotlinx.datetime.format.parse

data class TtrssStream(
    val continuation: String? = null,
    val content: List<TtrssItem>? = null,
    val ids: List<String>? = null,
) : Entity() {


//    fun getItems(): kotlin.collections.MutableList<TtrssItem?>? {
//        return content
//    }
//
//    fun setItems(items: kotlin.collections.MutableList<TtrssItem?>?) {
//        this.content = items
//    }
//
//
//    fun getItemIds(): kotlin.collections.MutableList<kotlin.String?>? {
//        return ids
//    }

    companion object {
//        fun parse(json: kotlin.String?): RssStream {
//            try {
//                return   convert(Gson().fromJson(json, TtrssStream::class.java))
//            } catch (e: java.lang.Exception) {
//                throw Entity.wrapException(json, e)
//            }
//        }

        fun parseIds(json: String?, continuation: String?): RssStream {
            val stream: RssStream = toJson<TtrssStream>(json.orEmpty()).convert()
            stream.ids = stream.items.map { it.id.orEmpty() }
            var c: Int = stream.items.size
            try {
                c += continuation.orEmpty().toInt()
            } catch (e: java.lang.Exception) {
            }
            stream.continuation = if (c > 0) "" + c else null
            return stream
        }
    }
}
