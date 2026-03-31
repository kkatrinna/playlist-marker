package com.example.playlist_maker_android_yulyakondratiuk.creator

import com.example.playlist_maker_android_yulyakondratiuk.data.dto.TrackDto

class Storage {

    private val listTracks = listOf(
        TrackDto(
            trackId = 1L,
            trackName = "Владивосток 2000",
            artistName = "Мумий Троль",
            trackTimeMillis = 158000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 2L,
            trackName = "Группа крови",
            artistName = "Кино",
            trackTimeMillis = 283000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 3L,
            trackName = "Не смотри назад",
            artistName = "Ария",
            trackTimeMillis = 312000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 4L,
            trackName = "Звезда по имени Солнце",
            artistName = "Кино",
            trackTimeMillis = 225000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 5L,
            trackName = "Лондон",
            artistName = "Аквариум",
            trackTimeMillis = 272000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 6L,
            trackName = "На заре",
            artistName = "Альянс",
            trackTimeMillis = 230000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 7L,
            trackName = "Перемен",
            artistName = "Кино",
            trackTimeMillis = 296000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 8L,
            trackName = "Розовый фламинго",
            artistName = "Сплин",
            trackTimeMillis = 195000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 9L,
            trackName = "Танцевать",
            artistName = "Мельница",
            trackTimeMillis = 222000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackId = 10L,
            trackName = "Чёрный бумер",
            artistName = "Серега",
            trackTimeMillis = 241000,
            artworkUrl100 = null
        )
    )

    fun search(request: String): List<TrackDto> {
        val query = request.lowercase().trim()
        if (query.isEmpty()) return emptyList()

        return listTracks.filter { track ->
            val trackName = track.trackName?.lowercase() ?: ""
            val artistName = track.artistName?.lowercase() ?: ""

            trackName.contains(query) ||
                    artistName.contains(query) ||
                    track.trackName?.split(" ")?.any { word ->
                        word.lowercase().contains(query)
                    } ?: false ||
                    track.artistName?.split(" ")?.any { word ->
                        word.lowercase().contains(query)
                    } ?: false
        }
    }

    fun getTrackById(trackId: Long): TrackDto? {
        return listTracks.find { it.trackId == trackId }
    }
}