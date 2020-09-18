package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import java.net.URL

/**
 * Configuration for downloading related anime from kitsu.io
 * @since 1.0.0
 */
public object KitsuTagsConfig : MetaDataProviderConfig by KitsuConfig {

    override fun buildDataDownloadUrl(id: String): URL = URL("https://${hostname()}/api/edge/anime/$id/categories")
}