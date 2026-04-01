package ru.ari.sharing.data.repository

import ru.ari.network.domain.models.Result
import ru.ari.sharing.api.domain.models.Post
import ru.ari.sharing.api.domain.models.PostImage
import ru.ari.sharing.api.domain.repository.SharingRepository
import javax.inject.Inject

class MockSharingRepositoryImpl @Inject constructor() : SharingRepository {

    private val fakePosts = listOf(
        Post(
            id = 1L,
            title = "Книга по Kotlin",
            description = "Отдам книгу по Kotlin в хорошем состоянии.",
            exchange = "На книгу по Android",
            corpus = "А",
            room = "305",
            messageId = "msg-1",
            isActive = true,
            isReserved = false,
            createdAt = "2026-02-18T18:00:00Z",
            reservedBy = "",
            authorEmail = "student1@edu.ru",
            images = listOf(
                PostImage(id = 101L, url = "https://img.freepik.com/free-photo/cute-kitten-sitting-staring-playful-fluffy-looking-camera-generated-by-artificial-intelligence_188544-113029.jpg?semt=ais_hybrid&w=740"),
                PostImage(id = 102L, url = "https://t3.ftcdn.net/jpg/06/01/11/80/360_F_601118087_sP4ZdrQsPZ4ta1X1V3bRdcjHuEY2SpX7.jpg")
            )
        ),
        Post(
            id = 2L,
            title = "Монитор 24 дюйма",
            description = "Рабочий монитор, без битых пикселей.",
            exchange = "На механическую клавиатуру",
            corpus = "Б",
            room = "112",
            messageId = "msg-2",
            isActive = true,
            isReserved = false,
            createdAt = "2026-02-17T12:30:00Z",
            reservedBy = "",
            authorEmail = "student2@edu.ru",
            images = listOf(
                PostImage(id = 201L, url = "monitor_1.jpg")
            )
        ),
        Post(
            id = 3L,
            title = "Гитара акустическая",
            description = "Подойдет для начинающих, струны новые.",
            exchange = "На колонку Bluetooth",
            corpus = "В",
            room = "410",
            messageId = "msg-3",
            isActive = true,
            isReserved = false,
            createdAt = "2026-02-16T09:15:00Z",
            reservedBy = "",
            authorEmail = "student3@edu.ru",
            images = emptyList()
        )
    )

    override suspend fun getPosts(forceRefresh: Boolean): Result<List<Post>> {
        return Result.Success(fakePosts)
    }

    override suspend fun getPostById(id: Long): Result<Post> {
        val post = fakePosts.firstOrNull { it.id == id }
        return if (post != null) {
            Result.Success(post)
        } else {
            Result.Error(404, "Пост с id=$id не найден")
        }
    }
}
