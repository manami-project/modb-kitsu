![Build](https://github.com/manami-project/modb-kitsu/workflows/Build/badge.svg)
# modb-kitsu
_[modb](https://github.com/manami-project?tab=repositories&q=modb&type=source)_ stands for _**M**anami **O**ffline **D**ata**B**ase_. Repositories prefixed with this acronym are used to create the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).

# What does this lib do?
This lib contains downloader and converter for downloading raw data from kitsu.io and convert it to an `Anime` object.
Don't use this lib to crawl the website entirely. Instead check whether [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database) already offers the data that you need.

# Usage
Add the repository and dependency to your build file
```kotlin
repositories {
    maven {
        url = uri("https://dl.bintray.com/manami-project/maven")
    }
}

dependencies {
    implementation("io.github.manamiproject:modb-kitsu:$version")
}
```