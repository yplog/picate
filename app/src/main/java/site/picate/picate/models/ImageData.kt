package site.picate.picate.models

class ImageData {
    var uuid: String? = null
    var imgUrl: String? = null
    var userName: String? = null
    var userLink: String? = null

    constructor(uuid: String, imgUrl: String, userName: String, userLink: String) {
        this.uuid = uuid
        this.imgUrl = imgUrl
        this.userName = userName
        this.userLink = userLink
    }

    constructor(){}
}