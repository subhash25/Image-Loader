package assignment.imageloader
data class ImageDataModelItem(
    val backupDetails: BackupDetails,
    val coverageURL: String,
    val id: String,
    val language: String,
    val mediaType: Int,
    val publishedAt: String,
    val publishedBy: String,
    val thumbnail: Thumbnail,
    val title: String
)

data class BackupDetails(
    val pdfLink: String,
    val screenshotURL: String
)

data class Thumbnail(
    val aspectRatio: Int,
    val basePath: String,
    val domain: String,
    val id: String,
    val key: String,
    val qualities: List<Int>,
    val version: Int
) {
}