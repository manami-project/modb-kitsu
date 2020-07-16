package io.github.manamiproject.modb.kitsu

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URL

internal class KitsuRelationsConfigTest {

    @Test
    fun `isTestContext is false`() {
        // when
        val result = KitsuRelationsConfig.isTestContext()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `hostname must be equal to KitsuConfig`() {
        // when
        val result = KitsuRelationsConfig.hostname()

        // then
        assertThat(result).isEqualTo(KitsuConfig.hostname())
    }

    @Test
    fun `anime link URL is the same as for KitsuConfig`() {
        // given
        val id = "1376"

        // when
        val result = KitsuRelationsConfig.buildAnimeLinkUrl(id)

        // then
        assertThat(result).isEqualTo(KitsuConfig.buildAnimeLinkUrl(id))
    }

    @Test
    fun `build data download URL correctly`() {
        // given
        val id = "1376"

        // when
        val result = KitsuRelationsConfig.buildDataDownloadUrl(id)

        // then
        assertThat(result).isEqualTo(URL("https://kitsu.io/api/edge/media-relationships?filter[source_id]=$id&filter[source_type]=Anime&include=destination&sort=role"))
    }

    @Test
    fun `file suffix must be json`() {
        // when
        val result = KitsuRelationsConfig.fileSuffix()

        // then
        assertThat(result).isEqualTo("json")
    }
}
