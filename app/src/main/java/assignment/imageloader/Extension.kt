package assignment.imageloader

fun Thumbnail.buildThumbnailUrl(): String {
    return "${this.domain}/${this.basePath}/0/${this.key}"
}