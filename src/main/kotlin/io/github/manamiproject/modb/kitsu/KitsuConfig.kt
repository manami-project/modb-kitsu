package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import java.net.URL

/**
 * Configuration for downloading and converting anime data from kitsu.io
 * @since 1.0.0
 */
object KitsuConfig : MetaDataProviderConfig {

    override fun hostname(): Hostname = "kitsu.io"

    override fun buildDataDownloadUrl(id: String): URL = URL("https://kitsu.io/api/edge/anime/$id")

    override fun fileSuffix(): FileSuffix = "json"
}
