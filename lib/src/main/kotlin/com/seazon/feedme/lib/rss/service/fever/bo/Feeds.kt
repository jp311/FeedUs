package com.seazon.feedme.lib.rss.service.fever.bo

import com.seazon.feedme.lib.rss.bo.RssFeed
import com.seazon.feedme.lib.rss.service.Static
import kotlinx.serialization.Serializable

@Serializable
data class Feeds(
    val feeds: List<Feed>,
    val feeds_groups: List<FeedGroup>
) {
    companion object {

        fun parse(json: String, groups: List<Group>): List<RssFeed> {
            val feeds = Static.defaultJson.decodeFromString<Feeds>(json)

            val feedGroupMap = mutableMapOf<Int, MutableList<Group>>()
            feeds.feeds_groups.forEach {
                groups.firstOrNull { group -> group.id == it.group_id }?.let { group ->
                    it.feed_ids.split(",").forEach { feedId ->
                        feedGroupMap[feedId.toInt()]?.run {
                            this
                        } ?: run {
                            mutableListOf<Group>().also { feedGroupMap[feedId.toInt()] = it }
                        }.add(group)
                    }
                }
            }

            return feeds.feeds.map {
                FeverSubscription(
                    it.id,
                    it.favicon_id,
                    it.title,
                    it.url,
                    it.site_url,
                    it.is_spark,
                    it.last_updated_on_time,
                    null
                ).apply {
                    categories = feedGroupMap[id]
                }.convert()
            }
        }
    }
}