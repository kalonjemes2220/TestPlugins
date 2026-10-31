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
        "trending" to "🔥 Trending Now",
        "cinema" to "🔥 Cinema",
        "bollywood" to "Trending Bollywood",
        "anime" to "Anime"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val list = mutableListOf<HomePageList>()
        
        try {
            val url = when(request.data) {
                "trending" -> "$mainUrl/ranking-list/gA8zZqlP3ga?id=8610422883619422240&page_from=more_SUBJECTS_MOVIE"
                "cinema" -> "$mainUrl/ranking-list/yfPrzpXpwM6?id=5692654647815587592&page_from=more_SUBJECTS_MOVIE"
                "bollywood" -> "$mainUrl/ranking-list/g862TVthEu?id=414907768299210008&page_from=more_SUBJECTS_MOVIE"
                "anime" -> "$mainUrl/ranking-list/U6n7BTbz43a?id=8434602210994128512&page_from=more_SUBJECTS_MOVIE"
                else -> "$mainUrl"
            }

            val document = app.get(url).document
            val items = mutableListOf<SearchResponse>()

            // ডামি ডেটা (যাতে অ্যাপ ক্র্যাশ না করে)
            for (i in 1..10) {
                items.add(newMovieSearchResponse(
                    name = "${request.name} Movie $i",
                    url = "$mainUrl/movie/$i",
                    type = TvType.Movie,
                    posterUrl = "https://via.placeholder.com/300x450?text=Movie+$i"
                ))
            }
            
            list.add(newHomePageList(request.name, items))
        } catch (e: Exception) {
            // এরর হলে ডামি ডেটা
            val items = mutableListOf<SearchResponse>()
            for (i in 1..10) {
                items.add(newMovieSearchResponse(
                    name = "${request.name} Movie $i",
                    url = "$mainUrl/movie/$i",
                    type = TvType.Movie,
                    posterUrl = "https://via.placeholder.com/300x450?text=Movie+$i"
                ))
            }
            list.add(newHomePageList(request.name, items))
        }
        
        return newHomePageResponse(list)
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
