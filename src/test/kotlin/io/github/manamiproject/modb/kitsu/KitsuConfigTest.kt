package io.github.manamiproject.modb.kitsu

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URL

internal class KitsuConfigTest {

    @Test
    fun `isTestContext is false`() {
        // when
        val result = KitsuConfig.isTestContext()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `hostname must be correct`() {
        // when
        val result = KitsuConfig.hostname()

        // then
        assertThat(result).isEqualTo("kitsu.io")
    }

    @Test
    fun `build anime link URL correctly`() {
        // given
        val id = "1376"

        // when
        val result = KitsuConfig.buildAnimeLinkUrl(id)

        // then
        assertThat(result).isEqualTo(URL("https://kitsu.io/anime/$id"))
    }

    @Test
    fun `build data download URL correctly`() {
        // given
        val id = "1535"

        // when
        val result = KitsuConfig.buildDataDownloadUrl(id)

        // then
        assertThat(result).isEqualTo(URL("https://kitsu.io/api/edge/anime/$id"))
    }

    @Test
    fun `file suffix must be json`() {
        // when
        val result = KitsuConfig.fileSuffix()

        // then
        assertThat(result).isEqualTo("json")
    }
}
