package com.seazon.feedme.lib.rss.service.ttrss.bo

import com.seazon.feedme.lib.rss.bo.Entity
import com.seazon.feedme.lib.rss.bo.RssTag
import com.seazon.feedme.lib.rss.service.ttrss.TtrssApi
import com.seazon.feedme.lib.utils.toJson

data class TtrssTag2List(
    val content: List<TtrssTag2>? = null,
)

data class TtrssTag2(
    val id: String? = null,
    val title: String? = null,
)

data class TtrssCategory(
    var id: String? = null, // ttrss的类别和订阅源id都是int数值，无法区分，为了区分，类别id加上category/前缀
    var label: String? = null,
) : Entity() {

    fun getLabelFromId(): String? {
        return label
    }

    companion object {
        const val ID_PREFIX: String = "category/"

        /**
         * @param json
         * @param useIdAsKey true表示返回map的key为id，false表示为title
         * @return
         * @throws JsonSyntaxException
         */
        fun parse(json: String, useIdAsKey: Boolean): Map<String, RssTag> {
            return toJson<TtrssTag2List>(json).content?.mapNotNull {
                try {
                    val idid = it.id?.toInt() ?: -1
                    if (idid >= 0) {
                        RssTag(
                            id = TtrssApi.wrapCategoryId(it.id!!),
                            label = it.title
                        )
                    } else {
                        null
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    null
                }
            }.orEmpty().associateBy {
                if (useIdAsKey) it.id.orEmpty() else it.label.orEmpty()
            }
        }
    }
}
