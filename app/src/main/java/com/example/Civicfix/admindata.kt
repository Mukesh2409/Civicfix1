package com.example.Civicfix

data class AdminData(
    val email:String?= null,
    var name:String?= null,
    var number:String?= null,
    var organizationname:String?= null,
    var uid: List<String?>? = null ,//This includes the list of user id under a admin
    var admin_services: List<String?>? = null,
    var isSuperAdmin: Boolean = false
)

data class UserData(
    var userId: String?=null,
    var email:String?= null,
    var name:String?= null,
    var number:String?= null,
    var age:String?=null,
    var flatNo:String?=null,
    var buildingNo:String?=null,
    var buildingName:String?=null,
    var adminId:String?=null,
    var ComplainsPending:Long?=0,
    var complainResolved:Long?=0,
)

data class Verification(
    val verificationId: String?=null,
    val timestamp:Long?=null,
    val verified:String?=null,
    var type:String?=null,
    var phone:String?=null
                                                                                       
)

data class UserComplain(
    var ComplainId: String? = null,
    var userId:String?=null,
    var ComplainType: String?=null,
    var ComplainSubject: String?=null,
    var ComplainDescription: String?=null,
    var image:String?=null,
    var Complainsolved:String?=null,
    var complain_time:String?=null,
    var completed_solved_time:String?=null,
)

data class AdminComplain(
    val userComplain: UserComplain,
    val userData: UserData,
)
