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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI

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
            override fun buildAnimeLink(id: AnimeId): URI = KitsuConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://localhost:$port/graphql")
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
            override fun buildAnimeLink(id: AnimeId): URI = KitsuConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://localhost:$port/graphql")
            override fun fileSuffix(): FileSuffix = KitsuConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/graphql"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withStatus(599)
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
        assertThat(result).hasMessage("Unable to determine the correct case for [kitsutId=$id], [responseCode=599]")
    }

    @Test
    fun `successfully load an entry`() {
        // given
        val id = 1535

        val testKitsuConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = KitsuConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://localhost:$port/graphql")
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
            override fun buildAnimeLink(id: AnimeId): URI = KitsuConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://localhost:$port/graphql")
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

    @ParameterizedTest
    @ValueSource(ints = [400, 500, 502, 520, 522, 525])
    fun `pause and retry on response code`(responseCode: Int) {
        // given
        val id = 1535

        val testKitsuConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = KitsuConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://localhost:$port/graphql")
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
                                        .withStatus(responseCode)
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