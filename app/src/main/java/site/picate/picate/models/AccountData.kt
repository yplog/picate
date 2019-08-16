package site.picate.picate.models

class AccountData{
    var user_id: String? = null
    var fav_list: HashMap<String, ImageData> = hashMapOf()
    var options: AccountOptionsData = AccountOptionsData()

    constructor(user_id: String, fav_list: HashMap<String, ImageData>, options: AccountOptionsData) {
        this.user_id = user_id
        this.fav_list = fav_list
        this.options = options
    }

    constructor(){}
}