package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val downloader = KitsuDownloader(KitsuConfig)
    val relationsDownloader = KitsuDownloader(KitsuRelationsConfig)
    val tagsDownloader = KitsuDownloader(KitsuTagsConfig)
    
    runBlocking {
        downloader.downloadSuspendable("186").writeToFile(resourceFile("file_converter_tests/anime_season/1989.json"))
        downloader.downloadSuspendable("42328").writeToFile(resourceFile("file_converter_tests/anime_season/fall.json"))
        downloader.downloadSuspendable("10613").writeToFile(resourceFile("file_converter_tests/anime_season/null.json"))
        downloader.downloadSuspendable("41370").writeToFile(resourceFile("file_converter_tests/anime_season/spring.json"))
        downloader.downloadSuspendable("42028").writeToFile(resourceFile("file_converter_tests/anime_season/summer.json"))
        downloader.downloadSuspendable("9587").writeToFile(resourceFile("file_converter_tests/anime_season/undefined.json"))
        downloader.downloadSuspendable("41312").writeToFile(resourceFile("file_converter_tests/anime_season/winter.json"))
        
        downloader.downloadSuspendable("10041").writeToFile(resourceFile("file_converter_tests/duration/0.json"))
        downloader.downloadSuspendable("10").writeToFile(resourceFile("file_converter_tests/duration/24.json"))
        downloader.downloadSuspendable("10035").writeToFile(resourceFile("file_converter_tests/duration/120.json"))
        downloader.downloadSuspendable("101").writeToFile(resourceFile("file_converter_tests/duration/null.json"))
    
        downloader.downloadSuspendable("1126").writeToFile(resourceFile("file_converter_tests/episodes/39.json"))
        downloader.downloadSuspendable("44019").writeToFile(resourceFile("file_converter_tests/episodes/null.json"))
    
        downloader.downloadSuspendable("12032").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/null.json"))
        downloader.downloadSuspendable("42006").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/pictures.json"))
    
        downloader.downloadSuspendable("8641").writeToFile(resourceFile("file_converter_tests/related_anime/has_adaption_but_no_relation/8641.json"))
        relationsDownloader.downloadSuspendable("8641").writeToFile(resourceFile("file_converter_tests/related_anime/has_adaption_but_no_relation/8641_relations.json"))
        downloader.downloadSuspendable("1415").writeToFile(resourceFile("file_converter_tests/related_anime/has_adaption_multiple_relations/1415.json"))
        relationsDownloader.downloadSuspendable("1415").writeToFile(resourceFile("file_converter_tests/related_anime/has_adaption_multiple_relations/1415_relations.json"))
        downloader.downloadSuspendable("7664").writeToFile(resourceFile("file_converter_tests/related_anime/no_adaption_multiple_relations/7664.json"))
        relationsDownloader.downloadSuspendable("7664").writeToFile(resourceFile("file_converter_tests/related_anime/no_adaption_multiple_relations/7664_relations.json"))
        downloader.downloadSuspendable("5989").writeToFile(resourceFile("file_converter_tests/related_anime/no_adaption_no_relations/5989.json"))
        relationsDownloader.downloadSuspendable("5989").writeToFile(resourceFile("file_converter_tests/related_anime/no_adaption_no_relations/5989_relations.json"))
        downloader.downloadSuspendable("177").writeToFile(resourceFile("file_converter_tests/related_anime/one_adaption_one_relation/177.json"))
        relationsDownloader.downloadSuspendable("177").writeToFile(resourceFile("file_converter_tests/related_anime/one_adaption_one_relation/177_relations.json"))
    
        downloader.downloadSuspendable("1517").writeToFile(resourceFile("file_converter_tests/sources/1517.json"))
    
        downloader.downloadSuspendable("12").writeToFile(resourceFile("file_converter_tests/status/current.json"))
        downloader.downloadSuspendable("10041").writeToFile(resourceFile("file_converter_tests/status/finished.json"))
        downloader.downloadSuspendable("42059").writeToFile(resourceFile("file_converter_tests/status/null.json"))
        downloader.downloadSuspendable("4278").writeToFile(resourceFile("file_converter_tests/status/tba.json"))
        downloader.downloadSuspendable("13240").writeToFile(resourceFile("file_converter_tests/status/unreleased.json"))
        downloader.downloadSuspendable("12509").writeToFile(resourceFile("file_converter_tests/status/upcoming.json"))
    
        downloader.downloadSuspendable("1217").writeToFile(resourceFile("file_converter_tests/synonyms/abbreviatedTitles_contains_null.json"))
        downloader.downloadSuspendable("13228").writeToFile(resourceFile("file_converter_tests/synonyms/combine_titles_and_synonyms.json"))
    
        downloader.downloadSuspendable("1").writeToFile(resourceFile("file_converter_tests/tags/1.json"))
        tagsDownloader.downloadSuspendable("1").writeToFile(resourceFile("file_converter_tests/tags/1_tags.json"))
        downloader.downloadSuspendable("43298").writeToFile(resourceFile("file_converter_tests/tags/43298.json"))
        tagsDownloader.downloadSuspendable("43298").writeToFile(resourceFile("file_converter_tests/tags/43298_tags.json"))
    
        downloader.downloadSuspendable("11260").writeToFile(resourceFile("file_converter_tests/title/special_chars.json"))
    
        downloader.downloadSuspendable("2027").writeToFile(resourceFile("file_converter_tests/type/movie.json"))
        downloader.downloadSuspendable("11791").writeToFile(resourceFile("file_converter_tests/type/music.json"))
        downloader.downloadSuspendable("11613").writeToFile(resourceFile("file_converter_tests/type/ona.json"))
        downloader.downloadSuspendable("11913").writeToFile(resourceFile("file_converter_tests/type/ova.json"))
        downloader.downloadSuspendable("343").writeToFile(resourceFile("file_converter_tests/type/special.json"))
        downloader.downloadSuspendable("6266").writeToFile(resourceFile("file_converter_tests/type/tv.json"))
    }
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}