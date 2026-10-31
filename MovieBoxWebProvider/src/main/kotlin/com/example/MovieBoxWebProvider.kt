package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element
import org.jsoup.Jsoup
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.runBlocking
import android.util.Log

class MovieBoxWebProvider : MainAPI() {
    override var mainUrl = "https://officialmoviebox.com"
    override var name = "MovieBox Web"
    override val hasMainPage = true
    override var lang = "en"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    private val mapper = ObjectMapper().registerKotlinModule()

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

            // ডামি ডেটা (কারণ আসল ডেটা পার্স করতে আরও কোড লাগবে)
            // তবে এটি নিশ্চিত করবে যে অ্যাপে সেকশনগুলো দেখাবে
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
            Log.e("MovieBoxWeb", "Error getting main page", e)
        }
        
        return newHomePageResponse(list)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        // সার্চ লজিক (পরে যোগ করা হবে)
        return listOf()
    }

    override suspend fun load(url: String): LoadResponse {
        // ডিটেইলস লজিক (পরে যোগ করা হবে)
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
        // ভিডিও লিংক লজিক (পরে যোগ করা হবে)
        return true
    }
}
