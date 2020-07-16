package io.github.manamiproject.modb.kitsu

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.APPLICATION_JSON
import io.github.manamiproject.modb.core.httpclient.retry.RetryableRegistry
import io.github.manamiproject.modb.test.MockServerTestCase
import io.github.manamiproject.modb.test.WireMockServerCreator
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL

internal class KitsuDownloaderTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @AfterEach
    override fun afterEach() {
        serverInstance.stop()
        RetryableRegistry.clear()
    }

    @Test
    fun `responding 404 indicating dead entry - add to dead entry list`() {
        // given
        val id = 1535

        val testKitsuConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLinkUrl(id: AnimeId): URL = KitsuConfig.buildAnimeLinkUrl(id)
            override fun buildDataDownloadUrl(id: String): URL = URL("http://localhost:$port/graphql")
            override fun fileSuffix(): FileSuffix = KitsuConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/graphql"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withStatus(404)
                        .withBody("<html><head/><body></body></html>")
                )
        )

        var deadEntry = EMPTY
        val downloader = KitsuDownloader(testKitsuConfig)

        // when
        downloader.download(id.toString()) {
            deadEntry = it
        }

        // then
        assertThat(deadEntry).isEqualTo(id.toString())
    }

    @Test
    fun `throws an exception in case of an unhandled response code`() {
        // given
        val id = 1535

        val testKitsuConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLinkUrl(id: AnimeId): URL = KitsuConfig.buildAnimeLinkUrl(id)
            override fun buildDataDownloadUrl(id: String): URL = URL("http://localhost:$port/graphql")
            override fun fileSuffix(): FileSuffix = KitsuConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/graphql"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withStatus(502)
                        .withBody("{ }")
                )
        )

        val downloader = KitsuDownloader(testKitsuConfig)

        // when
        val result = assertThrows<IllegalStateException> {
            downloader.download(id.toString()) {
                shouldNotBeInvoked()
            }
        }

        // then
        assertThat(result).hasMessage("Unable to determine the correct case for [kitsutId=$id], [responseCode=502]")
    }

    @Test
    fun `successfully load an entry`() {
        // given
        val id = 1535

        val testKitsuConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLinkUrl(id: AnimeId): URL = KitsuConfig.buildAnimeLinkUrl(id)
            override fun buildDataDownloadUrl(id: String): URL = URL("http://localhost:$port/graphql")
            override fun fileSuffix(): FileSuffix = KitsuConfig.fileSuffix()
        }

        val responseBody = "{ \"kitsuId\": $id }"

        serverInstance.stubFor(
            get(urlPathEqualTo("/graphql")).willReturn(
                aResponse()
                    .withHeader("Content-Type", APPLICATION_JSON)
                    .withStatus(200)
                    .withBody(responseBody)
            )
        )

        val downloader = KitsuDownloader(testKitsuConfig)

        // when
        val result = downloader.download(id.toString()) {
            shouldNotBeInvoked()
        }

        // then
        assertThat(result).isEqualTo(responseBody)
    }

    @Test
    fun `throws an exception if the response body is empty`() {
        // given
        val id = 1535

        val testKitsuConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLinkUrl(id: AnimeId): URL = KitsuConfig.buildAnimeLinkUrl(id)
            override fun buildDataDownloadUrl(id: String): URL = URL("http://localhost:$port/graphql")
            override fun fileSuffix(): FileSuffix = KitsuConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/graphql")).willReturn(
                aResponse()
                    .withHeader("Content-Type", APPLICATION_JSON)
                    .withStatus(200)
                    .withBody(EMPTY)
            )
        )

        val downloader = KitsuDownloader(testKitsuConfig)

        // when
        val result = assertThrows<IllegalStateException> {
            downloader.download(id.toString()) {
                shouldNotBeInvoked()
            }
        }

        // then
        assertThat(result).hasMessage("Response body was blank for [kitsuId=1535] with response code [200]")
    }

    @Test
    fun `pause and retry on response code 522`() {
        // given
        val id = 1535

        val testKitsuConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLinkUrl(id: AnimeId): URL = KitsuConfig.buildAnimeLinkUrl(id)
            override fun buildDataDownloadUrl(id: String): URL = URL("http://localhost:$port/graphql")
            override fun fileSuffix(): FileSuffix = KitsuConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/graphql"))
                .inScenario("pause and retry")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("successful retrieval")
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withStatus(522)
                        .withBody("<html></html>")
                )
        )

        val responseBody = "{ \"kitsuId\": $id }"

        serverInstance.stubFor(
            get(urlPathEqualTo("/graphql"))
                .inScenario("pause and retry")
                .whenScenarioStateIs("successful retrieval")
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withStatus(200)
                        .withBody(responseBody)
                )
        )

        val downloader = KitsuDownloader(testKitsuConfig)

        // when
        val result = downloader.download(id.toString()) {
            shouldNotBeInvoked()
        }

        // then
        assertThat(result).isEqualTo(responseBody)
    }
}