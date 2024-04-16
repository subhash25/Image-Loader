package assignment.imageloader
data class ImageDataModelItem(
    val thumbnail: Thumbnail
)
data class Thumbnail(
    val id: String,
    val basePath: String,
    val domain: String,
    val key: String
)