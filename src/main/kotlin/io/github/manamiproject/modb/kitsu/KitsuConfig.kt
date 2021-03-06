package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import java.net.URI

/**
 * Configuration for downloading and converting anime data from kitsu.io
 * @since 1.0.0
 */
public object KitsuConfig : MetaDataProviderConfig {

    override fun hostname(): Hostname = "kitsu.io"

    override fun buildDataDownloadLink(id: String): URI = URI("https://kitsu.io/api/edge/anime/$id")

    override fun fileSuffix(): FileSuffix = "json"
}
