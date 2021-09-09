package com.masai.ezetap_mobile_solutions_assignment

import com.google.firebase.database.Exclude


//model for saving info to the realtime database
class UserModel(
    @get:Exclude
    var id: String? = null,
    var name:String? = null,
    var email:String? = null,
    var password:String? = null,
    var age:String? = null,
    var birth_date:String? = null,
    var location:String? = null,
    var latitude:String? = null,
    var longitude:String? = null

) {

}