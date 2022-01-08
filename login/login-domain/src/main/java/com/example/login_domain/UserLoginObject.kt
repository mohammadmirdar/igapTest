package com.example.login_domain

import net.iGap.proto.ProtoUserRegister.UserRegisterResponse

data class UserLoginObject(
    val phoneNumber: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val userName: String? = null,
    val userId: Long? = null,
    val smsNumbers: List<Long>? = null,
    val regex: String? = null,
    val verifyCodeDigitCount: Int? = null,
    val authorHash: String? = null,
    val callMethodSupported: Boolean? = null,
    val resendCodeDelay: Long? = null,
    val methodValue: net.iGap.proto.ProtoUserRegister.UserRegisterResponse.Method? = null,
    val countryCode: String = "IR"
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        0,
        listOf(),
        "",
        0,
        "",
        false,
        0,
        null,
    )
}
