package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.retry.RetryBehavior
import io.github.manamiproject.modb.core.httpclient.retry.RetryableRegistry
import io.github.manamiproject.modb.core.random
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.toDuration

/**
 * Downloads anime data from kitsu.io
 * @since 1.0.0
 * @param config Configuration for downloading data.
 * @param httpClient To actually download the anime data.
 */
public class KitsuDownloader(
    private val config: MetaDataProviderConfig,
    private val httpClient: HttpClient = DefaultHttpClient(isTestContext = config.isTestContext()),
) : Downloader {

    init {
        registerRetryBehavior()
    }

    @Deprecated("Use coroutines",
        ReplaceWith("runBlocking { downloadSuspendable(id, onDeadEntry) }", "kotlinx.coroutines.runBlocking")
    )
    override fun download(id: AnimeId, onDeadEntry: (AnimeId) -> Unit): String = runBlocking {
        downloadSuspendable(id, onDeadEntry)
    }

    override suspend fun downloadSuspendable(id: AnimeId, onDeadEntry: (AnimeId) -> Unit): String = withContext(LIMITED_NETWORK) {
        val response = httpClient.getSuspedable(
            url = config.buildDataDownloadLink(id).toURL(),
            retryWith = config.hostname(),
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

    private fun registerRetryBehavior() {
        val retryBehaviorConfig = RetryBehavior(
            waitDuration = { random(4000, 8000).toDuration(MILLISECONDS) },
            isTestContext = config.isTestContext()
        ).apply {
            addCase {
                it.code in setOf(400, 500, 502, 520, 522, 525)
            }
        }

        RetryableRegistry.register(config.hostname(), retryBehaviorConfig)
    }
}