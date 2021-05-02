package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.Json.parseJson
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.extensions.*
import io.github.manamiproject.modb.core.models.*
import io.github.manamiproject.modb.core.models.Anime.Status
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.MINUTES
import java.net.URI

/**
 * Converts raw data to an [Anime].
 * @since 1.0.0
 * @param config Configuration for converting data.
 * @param relationsDir Directory containing the raw files for the related anime.
 * @param tagsDir Directory containing the raw files for the tags.
 * @throws IllegalArgumentException if either [relationsDir] or [tagsDir] doesn't exist or is not a directory.
 */
public class KitsuConverter(
    private val config: MetaDataProviderConfig = KitsuConfig,
    private val relationsDir: Directory,
    private val tagsDir: Directory
) : AnimeConverter {

    init {
        require(relationsDir.directoryExists()) { "Directory for relations [$relationsDir] does not exist or is not a directory." }
        require(tagsDir.directoryExists()) { "Directory for tags [$tagsDir] does not exist or is not a directory." }
    }

    override fun convert(rawContent: String): Anime {
        val document = parseJson<KitsuDocument>(rawContent)!!

        return Anime(
            _title = extractTitle(document),
            episodes = extractEpisodes(document),
            type = extractType(document),
            picture = extractPicture(document),
            thumbnail = extractThumbnail(document),
            status = extractStatus(document),
            duration = extractDuration(document),
            animeSeason = extractAnimeSeason(document)
        ).apply {
            addSources(extractSourcesEntry(document))
            addSynonyms(extractSynonyms(document))
            addRelations(extractRelatedAnime(document))
            addTags(extractTags(document))
        }
    }

    private fun extractTitle(document: KitsuDocument): Title = document.data.attributes.canonicalTitle

    private fun extractEpisodes(document: KitsuDocument): Episodes = document.data.attributes.episodeCount ?: 0

    private fun extractPicture(document: KitsuDocument): URI = URI(document.data.attributes.posterImage?.get("small") ?: NOT_FOUND_PIC)

    private fun extractThumbnail(document: KitsuDocument): URI =  URI(document.data.attributes.posterImage?.get("tiny") ?: NOT_FOUND_PIC)

    private fun extractSourcesEntry(document: KitsuDocument): List<URI> = listOf(config.buildAnimeLink(document.data.id))

    private fun extractType(document: KitsuDocument): Type {
        return when(document.data.attributes.subtype) {
            "TV" -> TV
            "ONA" -> ONA
            "movie" -> Movie
            "OVA" -> OVA
            "special" -> Special
            "music" -> Special
            else -> throw IllegalStateException("Unknown type [${document.data.attributes.subtype}]")
        }
    }

    private fun extractSynonyms(document: KitsuDocument): List<Title> {
        return document.data.attributes.titles.values.union(
            document.data.attributes.abbreviatedTitles?.filterNotNull() ?: emptyList()
        ).toList()
    }

    private fun extractRelatedAnime(document: KitsuDocument): List<URI> {
        val relationsFile = relationsDir.resolve("${document.data.id}.${config.fileSuffix()}")

        return if (relationsFile.regularFileExists()) {
            parseJson<KitsuRelation>(relationsFile.newInputStream())!!.included.filter { it.type == "anime" }
                .map { it.id }
                .map { config.buildAnimeLink(it) }
        } else {
            throw IllegalStateException("Relations file is missing")
        }
    }

    private fun extractStatus(document: KitsuDocument): Status {
        return when(document.data.attributes.status) {
            "finished" -> FINISHED
            "current" -> CURRENTLY
            "unreleased" -> UPCOMING
            "upcoming" -> UPCOMING
            "tba" -> UNKNOWN
            null -> UNKNOWN
            else -> throw IllegalStateException("Unknown status [${document.data.attributes.status}]")
        }
    }

    private fun extractDuration(document: KitsuDocument): Duration {
        val durationInMinutes = document.data.attributes.episodeLength ?: 0

        return Duration(durationInMinutes, MINUTES)
    }

    private fun extractTags(document: KitsuDocument): List<Tag> {
        val tagsFile = tagsDir.resolve("${document.data.id}.${config.fileSuffix()}")

        return if (tagsFile.regularFileExists()) {
            parseJson<KitsuTagsDocument>(tagsFile.newInputStream())!!.data.map { it.attributes.title }.distinct()
        } else {
            throw IllegalStateException("Tags file is missing")
        }
    }

    private fun extractAnimeSeason(document: KitsuDocument): AnimeSeason {
        val startDate = document.data.attributes.startDate ?: EMPTY
        val month = Regex("-[0-9]{2}-").findAll(startDate).firstOrNull()?.value?.replace("-", "")?.toInt() ?: 0
        val year = Regex("[0-9]{4}").find(startDate)?.value?.let { if (it.startsWith("0")) "0" else it }?.toInt() ?: 0

        val season = when(month) {
            12, 1, 2 -> WINTER
            3, 4, 5 -> SPRING
            6, 7, 8 -> SUMMER
            9, 10, 11 -> FALL
            else -> UNDEFINED
        }

        return AnimeSeason(
            season = season,
            year = year
        )
    }

    private companion object {
        private const val NOT_FOUND_PIC = "https://cdn.myanimelist.net/images/qm_50.gif"
    }
}

private data class KitsuDocument(
    val data: KitsuData
)

private data class KitsuData(
    val id: String,
    val attributes: KitsuDataAttributes
)

private data class KitsuDataAttributes(
    val titles: Map<String, String>,
    val canonicalTitle: String,
    val abbreviatedTitles: List<String?>?,
    val startDate: String?,
    val subtype: String,
    val posterImage: Map<String, String>?,
    val episodeCount: Int?,
    val episodeLength: Int?,
    val status: String?
)

private data class KitsuRelation(
    val included: List<KitsuRelationData> = emptyList()
)

private data class KitsuRelationData(
    val id: String,
    val type: String
)

private data class KitsuTagsDocument(
    val data: List<KitsuTag>
)

private data class KitsuTag(
    val attributes: KitsuTagAttributes
)

private data class KitsuTagAttributes(
    val title: String
)
