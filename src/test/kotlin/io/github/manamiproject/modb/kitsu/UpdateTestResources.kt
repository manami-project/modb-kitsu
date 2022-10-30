package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.core.extensions.writeToFileSuspendable
import io.github.manamiproject.modb.test.testResource
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val downloader = KitsuDownloader(KitsuConfig)
    val relationsDownloader = KitsuDownloader(KitsuRelationsConfig)
    val tagsDownloader = KitsuDownloader(KitsuTagsConfig)
    
    runBlocking {
        downloader.downloadSuspendable("186").writeToFileSuspendable(resourceFile("file_converter_tests/anime_season/1989.json"))
        downloader.downloadSuspendable("42328").writeToFileSuspendable(resourceFile("file_converter_tests/anime_season/fall.json"))
        downloader.downloadSuspendable("10613").writeToFileSuspendable(resourceFile("file_converter_tests/anime_season/null.json"))
        downloader.downloadSuspendable("41370").writeToFileSuspendable(resourceFile("file_converter_tests/anime_season/spring.json"))
        downloader.downloadSuspendable("42028").writeToFileSuspendable(resourceFile("file_converter_tests/anime_season/summer.json"))
        downloader.downloadSuspendable("9587").writeToFileSuspendable(resourceFile("file_converter_tests/anime_season/undefined.json"))
        downloader.downloadSuspendable("41312").writeToFileSuspendable(resourceFile("file_converter_tests/anime_season/winter.json"))
        
        downloader.downloadSuspendable("10041").writeToFileSuspendable(resourceFile("file_converter_tests/duration/0.json"))
        downloader.downloadSuspendable("10").writeToFileSuspendable(resourceFile("file_converter_tests/duration/24.json"))
        downloader.downloadSuspendable("10035").writeToFileSuspendable(resourceFile("file_converter_tests/duration/120.json"))
        downloader.downloadSuspendable("101").writeToFileSuspendable(resourceFile("file_converter_tests/duration/null.json"))
    
        downloader.downloadSuspendable("1126").writeToFileSuspendable(resourceFile("file_converter_tests/episodes/39.json"))
        downloader.downloadSuspendable("44019").writeToFileSuspendable(resourceFile("file_converter_tests/episodes/null.json"))
    
        downloader.downloadSuspendable("12032").writeToFileSuspendable(resourceFile("file_converter_tests/picture_and_thumbnail/null.json"))
        downloader.downloadSuspendable("42006").writeToFileSuspendable(resourceFile("file_converter_tests/picture_and_thumbnail/pictures.json"))
    
        downloader.downloadSuspendable("8641").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/has_adaption_but_no_relation/8641.json"))
        relationsDownloader.downloadSuspendable("8641").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/has_adaption_but_no_relation/8641_relations.json"))
        downloader.downloadSuspendable("1415").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/has_adaption_multiple_relations/1415.json"))
        relationsDownloader.downloadSuspendable("1415").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/has_adaption_multiple_relations/1415_relations.json"))
        downloader.downloadSuspendable("7664").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/no_adaption_multiple_relations/7664.json"))
        relationsDownloader.downloadSuspendable("7664").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/no_adaption_multiple_relations/7664_relations.json"))
        downloader.downloadSuspendable("5989").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/no_adaption_no_relations/5989.json"))
        relationsDownloader.downloadSuspendable("5989").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/no_adaption_no_relations/5989_relations.json"))
        downloader.downloadSuspendable("177").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/one_adaption_one_relation/177.json"))
        relationsDownloader.downloadSuspendable("177").writeToFileSuspendable(resourceFile("file_converter_tests/related_anime/one_adaption_one_relation/177_relations.json"))
    
        downloader.downloadSuspendable("1517").writeToFileSuspendable(resourceFile("file_converter_tests/sources/1517.json"))
    
        downloader.downloadSuspendable("12").writeToFileSuspendable(resourceFile("file_converter_tests/status/current.json"))
        downloader.downloadSuspendable("10041").writeToFileSuspendable(resourceFile("file_converter_tests/status/finished.json"))
        downloader.downloadSuspendable("42059").writeToFileSuspendable(resourceFile("file_converter_tests/status/null.json"))
        downloader.downloadSuspendable("4278").writeToFileSuspendable(resourceFile("file_converter_tests/status/tba.json"))
        downloader.downloadSuspendable("13240").writeToFileSuspendable(resourceFile("file_converter_tests/status/unreleased.json"))
        downloader.downloadSuspendable("12509").writeToFileSuspendable(resourceFile("file_converter_tests/status/upcoming.json"))
    
        downloader.downloadSuspendable("1217").writeToFileSuspendable(resourceFile("file_converter_tests/synonyms/abbreviatedTitles_contains_null.json"))
        downloader.downloadSuspendable("13228").writeToFileSuspendable(resourceFile("file_converter_tests/synonyms/combine_titles_and_synonyms.json"))
    
        downloader.downloadSuspendable("1").writeToFileSuspendable(resourceFile("file_converter_tests/tags/1.json"))
        tagsDownloader.downloadSuspendable("1").writeToFileSuspendable(resourceFile("file_converter_tests/tags/1_tags.json"))
        downloader.downloadSuspendable("43298").writeToFileSuspendable(resourceFile("file_converter_tests/tags/43298.json"))
        tagsDownloader.downloadSuspendable("43298").writeToFileSuspendable(resourceFile("file_converter_tests/tags/43298_tags.json"))
    
        downloader.downloadSuspendable("11260").writeToFileSuspendable(resourceFile("file_converter_tests/title/special_chars.json"))
    
        downloader.downloadSuspendable("2027").writeToFileSuspendable(resourceFile("file_converter_tests/type/movie.json"))
        downloader.downloadSuspendable("11791").writeToFileSuspendable(resourceFile("file_converter_tests/type/music.json"))
        downloader.downloadSuspendable("11613").writeToFileSuspendable(resourceFile("file_converter_tests/type/ona.json"))
        downloader.downloadSuspendable("11913").writeToFileSuspendable(resourceFile("file_converter_tests/type/ova.json"))
        downloader.downloadSuspendable("343").writeToFileSuspendable(resourceFile("file_converter_tests/type/special.json"))
        downloader.downloadSuspendable("6266").writeToFileSuspendable(resourceFile("file_converter_tests/type/tv.json"))
    }
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}