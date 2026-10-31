package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import org.jsoup.Jsoup
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

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
            // MovieBox-এর র্যাংকিং পেজের URL
            val url = when(request.data) {
                "trending" -> "$mainUrl/ranking-list/gA8zZqlP3ga?id=8610422883619422240&page_from=more_SUBJECTS_MOVIE"
                "cinema" -> "$mainUrl/ranking-list/yfPrzpXpwM6?id=5692654647815587592&page_from=more_SUBJECTS_MOVIE"
                "bollywood" -> "$mainUrl/ranking-list/g862TVthEu?id=414907768299210008&page_from=more_SUBJECTS_MOVIE"
                "anime" -> "$mainUrl/ranking-list/U6n7BTbz43a?id=8434602210994128512&page_from=more_SUBJECTS_MOVIE"
                else -> "$mainUrl"
            }

            // পেজ থেকে HTML ডাউনলোড করা
            val document = app.get(url).document
            
            // পেজের ভেতরে থাকা __NUXT_DATA__ স্ক্রিপ্ট ট্যাগ থেকে JSON ডেটা বের করা
            val nuxtDataString = document.select("script#__NUXT_DATA__").html()
            
            val items = mutableListOf<SearchResponse>()

            if (nuxtDataString.isNotEmpty()) {
                // JSON ডেটাকে পার্স করা
                // এখানে আমরা সরাসরি ডেটা রিড করছি, তবে ডেটা স্ট্রাকচার জটিল হওয়ায় আপাতত রেগুলার এক্সপ্রেশন ব্যবহার করছি
                val titleRegex = Regex("\"(title|name)\":\\s*\"([^\"]+)\"")
                val imgRegex = Regex("\"url\":\\s*\"(https://[^\"]+\\.(jpg|png|webp))\"")
                val idRegex = Regex("\"(subjectId|detailPath)\":\\s*\"([^\"]+)\"")

                val titles = titleRegex.findAll(nuxtDataString).map { it.groupValues[2] }.toList()
                val images = imgRegex.findAll(nuxtDataString).map { it.groupValues[1] }.toList()
                val ids = idRegex.findAll(nuxtDataString).map { it.groupValues[2] }.toList()

                // ডেটা জোড়া লাগানো (যতটুকু সম্ভব)
                for (i in 0 until minOf(titles.size, images.size, ids.size)) {
                    items.add(newMovieSearchResponse(
                        name = titles[i],
                        url = "$mainUrl/moviesDetail/${ids[i]}",
                        type = TvType.Movie,
                        posterUrl = images[i]
                    ))
                }
            }

            // যদি কোনো ডেটা না পাওয়া যায়, তবে ডামি ডেটা দেখাবে
            if (items.isEmpty()) {
                for (i in 1..10) {
                    items.add(newMovieSearchResponse(
                        name = "${request.name} Movie $i",
                        url = "$mainUrl/movie/$i",
                        type = TvType.Movie,
                        posterUrl = "https://via.placeholder.com/300x450?text=Movie+$i"
                    ))
                }
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
