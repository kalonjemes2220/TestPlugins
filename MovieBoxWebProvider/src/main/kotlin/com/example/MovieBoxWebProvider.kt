
package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class MovieBoxWebPlugin : Plugin() {
    override fun load(context: android.content.Context) {
        registerMainAPI(MovieBoxWebProvider())
    }
}

class MovieBoxWebProvider : MainAPI() {
    override var mainUrl = "https://officialmoviebox.com"
    override var name = "MovieBox Web"
    override val hasMainPage = true
    override var lang = "en"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = mainPageOf(
        "trending" to "🔥 Trending Now"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val home = listOf<HomePageList>()
        return newHomePageResponse(home)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        return listOf()
    }

    override suspend fun load(url: String): LoadResponse {
        return newMovieLoadResponse("Title", url, TvType.Movie, "") {
            this.posterUrl = ""
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        return true
    }
}
