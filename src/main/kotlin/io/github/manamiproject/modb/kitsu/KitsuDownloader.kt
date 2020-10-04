package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.retry.RetryBehavior
import io.github.manamiproject.modb.core.httpclient.retry.RetryableRegistry
import io.github.manamiproject.modb.core.random

/**
 * Downloads anime data from kitsu.io
 * @since 1.0.0
 * @param config Configuration for downloading data.
 * @param httpClient To actually download the anime data.
 */
public class KitsuDownloader(
    private val config: MetaDataProviderConfig,
    private val httpClient: HttpClient = DefaultHttpClient()
) : Downloader {

    init {
        registerRetryBehavior()
    }

    override fun download(id: AnimeId, onDeadEntry: (AnimeId) -> Unit): String {
        val response = httpClient.get(
            url = config.buildDataDownloadUrl(id),
            retryWith = config.hostname()
        )

        check(response.body.isNotBlank()) { "Response body was blank for [kitsuId=$id] with response code [${response.code}]" }

        return when(response.code) {
            200 -> response.body
            404 -> {
                onDeadEntry.invoke(id)
                EMPTY
            }
            else -> throw IllegalStateException("Unable to determine the correct case for [kitsutId=$id], [responseCode=${response.code}]")
        }
    }

    private fun registerRetryBehavior() {
        val retryBehaviorConfig = RetryBehavior(
            waitDuration = { random(4000, 8000) },
            retryOnResponsePredicate = { httpResponse ->
                listOf(500, 502, 520, 522).contains(httpResponse.code)
            }
        )

        RetryableRegistry.register(config.hostname(), retryBehaviorConfig)
    }
}