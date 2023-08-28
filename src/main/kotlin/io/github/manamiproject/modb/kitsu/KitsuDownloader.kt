package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.RetryCase
import kotlinx.coroutines.withContext

/**
 * Downloads anime data from kitsu.io
 * @since 1.0.0
 * @param config Configuration for downloading data.
 * @param httpClient To actually download the anime data.
 */
public class KitsuDownloader(
    private val config: MetaDataProviderConfig,
    private val httpClient: HttpClient = DefaultHttpClient(isTestContext = config.isTestContext()).apply {
        retryBehavior.addCases(RetryCase { it.code == 400 })
    },
) : Downloader {

    override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String = withContext(LIMITED_NETWORK) {
        val response = httpClient.get(
            url = config.buildDataDownloadLink(id).toURL(),
        )

        check(response.body.isNotBlank()) { "Response body was blank for [kitsuId=$id] with response code [${response.code}]" }

        return@withContext when(response.code) {
            200 -> response.body
            404 -> {
                onDeadEntry.invoke(id)
                EMPTY
            }
            else -> throw IllegalStateException("Unable to determine the correct case for [kitsutId=$id], [responseCode=${response.code}]")
        }
    }
}