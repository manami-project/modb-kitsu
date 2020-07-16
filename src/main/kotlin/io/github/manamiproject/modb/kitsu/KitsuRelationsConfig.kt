package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import java.net.URL

/**
 * Configuration for downloading tags from kitsu.io
 * @since 1.0.0
 */
object KitsuRelationsConfig : MetaDataProviderConfig by KitsuConfig {

    override fun buildDataDownloadUrl(id: String): URL = URL("https://${hostname()}/api/edge/media-relationships?filter[source_id]=$id&filter[source_type]=Anime&include=destination&sort=role")
}
