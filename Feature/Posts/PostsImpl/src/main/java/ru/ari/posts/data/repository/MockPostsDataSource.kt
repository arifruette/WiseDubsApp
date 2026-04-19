package ru.ari.posts.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import ru.ari.posts.api.domain.models.CreatePostParams
import ru.ari.posts.api.domain.models.MockPickupLocationStore
import ru.ari.posts.api.domain.models.PickupLocation
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.models.PostImage
import ru.ari.posts.api.domain.models.UpdatePostParams

@Singleton
class MockPostsDataSource @Inject constructor(
    context: Context
) {

    private val appContext = context.applicationContext
    private val imagesRootDir = File(appContext.filesDir, "mock_post_images").apply { mkdirs() }
    private val seedImagesDir = File(imagesRootDir, "seed").apply { mkdirs() }

    fun getAllPosts(): List<Post> = sharingPosts.toList()

    fun getMyPosts(): List<Post> = myPosts.toList()

    fun getPostById(id: Long): Post? = (sharingPosts + myPosts).firstOrNull { it.id == id }

    fun setPostActive(id: Long, isActive: Boolean): Post? {
        val index = myPosts.indexOfFirst { it.id == id }
        if (index == -1) {
            return null
        }

        val updatedPost = myPosts[index].copy(isActive = isActive)
        myPosts[index] = updatedPost
        return updatedPost
    }

    fun createPost(params: CreatePostParams): Post {
        val postId = (myPosts.maxOfOrNull(Post::id) ?: 200L) + 1L
        val post = Post(
            id = postId,
            title = params.title,
            description = params.description.orEmpty(),
            exchange = params.exchange.orEmpty(),
            pickupLocation = requirePickupLocation(params.pickupLocationId),
            messageId = params.messageId,
            isActive = true,
            isReserved = false,
            createdAt = "2026-04-14T00:00:00",
            reservedBy = params.reservedBy,
            authorEmail = "me@example.com",
            images = storeUploadedImages(postId = postId, imageFiles = params.imageFiles)
        )
        myPosts.add(0, post)
        return post
    }

    fun updatePost(params: UpdatePostParams): Post? {
        val index = myPosts.indexOfFirst { it.id == params.postId }
        if (index == -1) {
            return null
        }

        val current = myPosts[index]
        val updated = current.copy(
            title = params.title,
            description = params.description.orEmpty(),
            exchange = params.exchange.orEmpty(),
            pickupLocation = params.pickupLocationId?.let(::requirePickupLocation) ?: current.pickupLocation,
            messageId = params.messageId,
            reservedBy = params.reservedBy,
            images = params.imageFiles?.let { imageFiles ->
                storeUploadedImages(postId = params.postId, imageFiles = imageFiles)
            } ?: current.images
        )
        myPosts[index] = updated
        return updated
    }

    private fun requirePickupLocation(id: Int): PickupLocation {
        return MockPickupLocationStore.getById(id)
            ?: error("Pickup location with id=$id is missing in mock store")
    }

    private fun storeUploadedImages(postId: Long, imageFiles: List<File>): List<PostImage> {
        val postDir = File(imagesRootDir, "post_$postId").apply {
            deleteRecursively()
            mkdirs()
        }

        return imageFiles.mapIndexed { index, sourceFile ->
            val extension = sourceFile.extension.takeIf(String::isNotBlank) ?: "jpg"
            val targetFile = File(postDir, "image_${index + 1}.$extension")
            sourceFile.copyTo(targetFile, overwrite = true)
            PostImage(
                id = index.toLong() + 1L,
                url = targetFile.toURI().toString()
            )
        }
    }

    private fun seedImage(
        fileName: String,
        title: String,
        backgroundColor: Int,
        accentColor: Int
    ): PostImage {
        val file = File(seedImagesDir, "$fileName.png")
        if (!file.exists()) {
            writePlaceholderImage(
                file = file,
                title = title,
                backgroundColor = backgroundColor,
                accentColor = accentColor
            )
        }

        return PostImage(
            id = fileName.filter(Char::isDigit).toLongOrNull() ?: fileName.hashCode().toLong(),
            url = file.toURI().toString()
        )
    }

    private fun writePlaceholderImage(
        file: File,
        title: String,
        backgroundColor: Int,
        accentColor: Int
    ) {
        val bitmap = createBitmap(1200, 900)
        val canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)

        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
        }
        canvas.drawCircle(180f, 170f, 120f, accentPaint)
        canvas.drawRect(730f, 110f, 1080f, 260f, accentPaint)
        canvas.drawRect(120f, 620f, 1080f, 700f, accentPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 82f
            isFakeBoldText = true
        }
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(220, 255, 255, 255)
            textSize = 42f
        }

        canvas.drawText(title, 120f, 470f, titlePaint)
        canvas.drawText("WiseDubs mock image", 120f, 545f, subtitlePaint)

        file.outputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        bitmap.recycle()
    }

    private val defaultPickupLocation = PickupLocation(
        id = 1,
        userId = 1,
        corpus = "ГУК А",
        entrance = null,
        floor = null,
        room = "314",
        comment = null,
        label = "Главный корпус",
        createdAt = "2026-04-09T10:00:00",
        updatedAt = "2026-04-09T10:00:00"
    )

    private val sharingPosts = mutableListOf(
        Post(
            id = 101L,
            title = "Ноутбук Lenovo ThinkPad T480",
            description = "Рабочий ноутбук для учебы, заряд держит около 4 часов, отдам с оригинальной зарядкой.",
            exchange = "Электроника",
            pickupLocation = defaultPickupLocation.copy(id = 101, corpus = "ГУК А", room = "314"),
            messageId = "sharing-101",
            isActive = true,
            isReserved = false,
            createdAt = "2026-04-09T10:00:00",
            reservedBy = "",
            authorEmail = "maria@example.com",
            images = listOf(
                seedImage(
                    fileName = "sharing_101",
                    title = "ThinkPad T480",
                    backgroundColor = "#5C3B2E".toColorInt(),
                    accentColor = "#E4A663".toColorInt()
                )
            )
        ),
        Post(
            id = 102L,
            title = "Графический калькулятор Casio FX-9860GIII",
            description = "Почти новый калькулятор, подходит для инженерных задач и контрольных по матану.",
            exchange = "Учеба",
            pickupLocation = defaultPickupLocation.copy(id = 102, corpus = "ГУК Б", room = "208"),
            messageId = "sharing-102",
            isActive = true,
            isReserved = true,
            createdAt = "2026-04-08T18:30:00",
            reservedBy = "alex@example.com",
            authorEmail = "olga@example.com",
            images = listOf(
                seedImage(
                    fileName = "sharing_102",
                    title = "Casio FX",
                    backgroundColor = "#243B53".toColorInt(),
                    accentColor = "#5BC0BE".toColorInt()
                )
            )
        ),
        Post(
            id = 103L,
            title = "Складной самокат Oxelo Town 7",
            description = "Самокат в хорошем состоянии, удобен для дороги от метро до корпуса.",
            exchange = "Транспорт",
            pickupLocation = defaultPickupLocation.copy(id = 103, corpus = "ГУК В", room = "119"),
            messageId = "sharing-103",
            isActive = true,
            isReserved = false,
            createdAt = "2026-04-07T12:15:00",
            reservedBy = "",
            authorEmail = "irina@example.com",
            images = listOf(
                seedImage(
                    fileName = "sharing_103",
                    title = "Oxelo Town 7",
                    backgroundColor = "#294936".toColorInt(),
                    accentColor = "#F4E285".toColorInt()
                )
            )
        )
    )

    private val myPosts = mutableListOf(
        Post(
            id = 201L,
            title = "iPad 9th Gen 64GB",
            description = "Планшет с чехлом и стилусом, без трещин, подходит для конспектов и чтения PDF.",
            exchange = "Электроника",
            pickupLocation = defaultPickupLocation.copy(id = 201, corpus = "ГУК А", room = "402"),
            messageId = "my-201",
            isActive = true,
            isReserved = false,
            createdAt = "2026-04-09T09:10:00",
            reservedBy = "",
            authorEmail = "me@example.com",
            images = listOf(
                seedImage(
                    fileName = "my_201",
                    title = "iPad 9",
                    backgroundColor = "#3A506B".toColorInt(),
                    accentColor = "#A1C6EA".toColorInt()
                )
            )
        ),
        Post(
            id = 202L,
            title = "Наушники Sony WH-1000XM4",
            description = "Полноразмерные Bluetooth-наушники, шумодав работает, в комплекте кабель и кейс.",
            exchange = "Аксессуары",
            pickupLocation = defaultPickupLocation.copy(id = 202, corpus = "ГУК Б", room = "126"),
            messageId = "my-202",
            isActive = true,
            isReserved = true,
            createdAt = "2026-04-08T15:45:00",
            reservedBy = "nikita@example.com",
            authorEmail = "me@example.com",
            images = listOf(
                seedImage(
                    fileName = "my_202",
                    title = "Sony XM4",
                    backgroundColor = "#2B2D42".toColorInt(),
                    accentColor = "#EF8354".toColorInt()
                )
            )
        ),
        Post(
            id = 203L,
            title = "Учебник Campbell Biology, 12th Edition",
            description = "Толстый англоязычный учебник по биологии, есть пометки карандашом на нескольких страницах.",
            exchange = "Книги",
            pickupLocation = defaultPickupLocation.copy(id = 203, corpus = "ГУК Г", room = "221"),
            messageId = "my-203",
            isActive = false,
            isReserved = false,
            createdAt = "2026-04-05T11:20:00",
            reservedBy = "",
            authorEmail = "me@example.com",
            images = listOf(
                seedImage(
                    fileName = "my_203",
                    title = "Campbell Biology",
                    backgroundColor = "#5C2A9D".toColorInt(),
                    accentColor = "#B8F2E6".toColorInt()
                )
            )
        ),
        Post(
            id = 204L,
            title = "Рюкзак Herschel Little America",
            description = "Вместительный городской рюкзак, состояние хорошее, использовался один семестр.",
            exchange = "Одежда и аксессуары",
            pickupLocation = defaultPickupLocation.copy(id = 204, corpus = "ГУК Д", room = "017"),
            messageId = "my-204",
            isActive = false,
            isReserved = false,
            createdAt = "2026-04-01T08:00:00",
            reservedBy = "",
            authorEmail = "me@example.com",
            images = listOf(
                seedImage(
                    fileName = "my_204",
                    title = "Herschel Pack",
                    backgroundColor = "#6B4226".toColorInt(),
                    accentColor = "#F7B267".toColorInt()
                )
            )
        )
    )
}
