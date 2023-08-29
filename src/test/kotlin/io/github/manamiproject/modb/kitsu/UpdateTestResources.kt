package io.github.manamiproject.modb.kitsu

import io.github.manamiproject.modb.core.coroutines.CoroutineManager.runCoroutine
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import java.nio.file.Path
import java.nio.file.Paths

internal fun main() {
    val downloader = KitsuDownloader(KitsuConfig)
    val relationsDownloader = KitsuDownloader(KitsuRelationsConfig)
    val tagsDownloader = KitsuDownloader(KitsuTagsConfig)
    
    runCoroutine {
        downloader.download("186").writeToFile(resourceFile("file_converter_tests/anime_season/1989.json"))
        downloader.download("42328").writeToFile(resourceFile("file_converter_tests/anime_season/fall.json"))
        downloader.download("10613").writeToFile(resourceFile("file_converter_tests/anime_season/null.json"))
        downloader.download("41370").writeToFile(resourceFile("file_converter_tests/anime_season/spring.json"))
        downloader.download("42028").writeToFile(resourceFile("file_converter_tests/anime_season/summer.json"))
        downloader.download("41312").writeToFile(resourceFile("file_converter_tests/anime_season/winter.json"))

        downloader.download("10041").writeToFile(resourceFile("file_converter_tests/duration/0.json"))
        downloader.download("10").writeToFile(resourceFile("file_converter_tests/duration/24.json"))
        downloader.download("10035").writeToFile(resourceFile("file_converter_tests/duration/120.json"))
        downloader.download("46530").writeToFile(resourceFile("file_converter_tests/duration/null.json"))

        downloader.download("1126").writeToFile(resourceFile("file_converter_tests/episodes/39.json"))
        downloader.download("44019").writeToFile(resourceFile("file_converter_tests/episodes/null.json"))

        downloader.download("6334").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/null.json"))
        downloader.download("42006").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/pictures.json"))
    
        downloader.download("8641").writeToFile(resourceFile("file_converter_tests/related_anime/has_adaption_but_no_relation/8641.json"))
        relationsDownloader.download("8641").writeToFile(resourceFile("file_converter_tests/related_anime/has_adaption_but_no_relation/8641_relations.json"))
        downloader.download("1415").writeToFile(resourceFile("file_converter_tests/related_anime/has_adaption_multiple_relations/1415.json"))
        relationsDownloader.download("1415").writeToFile(resourceFile("file_converter_tests/related_anime/has_adaption_multiple_relations/1415_relations.json"))
        downloader.download("7664").writeToFile(resourceFile("file_converter_tests/related_anime/no_adaption_multiple_relations/7664.json"))
        relationsDownloader.download("7664").writeToFile(resourceFile("file_converter_tests/related_anime/no_adaption_multiple_relations/7664_relations.json"))
        downloader.download("5989").writeToFile(resourceFile("file_converter_tests/related_anime/no_adaption_no_relations/5989.json"))
        relationsDownloader.download("5989").writeToFile(resourceFile("file_converter_tests/related_anime/no_adaption_no_relations/5989_relations.json"))
        downloader.download("46232").writeToFile(resourceFile("file_converter_tests/related_anime/one_adaption_one_relation/46232.json"))
        relationsDownloader.download("46232").writeToFile(resourceFile("file_converter_tests/related_anime/one_adaption_one_relation/46232_relations.json"))
    
        downloader.download("1517").writeToFile(resourceFile("file_converter_tests/sources/1517.json"))
    
        downloader.download("12").writeToFile(resourceFile("file_converter_tests/status/current.json"))
        downloader.download("10041").writeToFile(resourceFile("file_converter_tests/status/finished.json"))
        downloader.download("42059").writeToFile(resourceFile("file_converter_tests/status/null.json"))
        downloader.download("45557").writeToFile(resourceFile("file_converter_tests/status/tba.json"))
        downloader.download("46873").writeToFile(resourceFile("file_converter_tests/status/unreleased.json"))
        downloader.download("46358").writeToFile(resourceFile("file_converter_tests/status/upcoming.json"))

        downloader.download("1217").writeToFile(resourceFile("file_converter_tests/synonyms/abbreviatedTitles_contains_null.json"))
        downloader.download("13228").writeToFile(resourceFile("file_converter_tests/synonyms/combine_titles_and_synonyms.json"))
    
        downloader.download("1").writeToFile(resourceFile("file_converter_tests/tags/1.json"))
        tagsDownloader.download("1").writeToFile(resourceFile("file_converter_tests/tags/1_tags.json"))
        downloader.download("43298").writeToFile(resourceFile("file_converter_tests/tags/43298.json"))
        tagsDownloader.download("43298").writeToFile(resourceFile("file_converter_tests/tags/43298_tags.json"))
    
        downloader.download("11260").writeToFile(resourceFile("file_converter_tests/title/special_chars.json"))
    
        downloader.download("2027").writeToFile(resourceFile("file_converter_tests/type/movie.json"))
        downloader.download("11791").writeToFile(resourceFile("file_converter_tests/type/music.json"))
        downloader.download("11613").writeToFile(resourceFile("file_converter_tests/type/ona.json"))
        downloader.download("11913").writeToFile(resourceFile("file_converter_tests/type/ova.json"))
        downloader.download("343").writeToFile(resourceFile("file_converter_tests/type/special.json"))
        downloader.download("6266").writeToFile(resourceFile("file_converter_tests/type/tv.json"))

        println("Done")
    }
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}