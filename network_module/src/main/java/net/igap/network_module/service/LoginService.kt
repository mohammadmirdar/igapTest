package net.igap.network_module.service

import com.example.login_domain.UserLoginObject
import net.iGap.proto.ProtoRequest
import net.iGap.proto.ProtoUserRegister
import net.iga.common.HelperString
import net.igap.network_module.AbstractObject
import net.igap.network_module.IG_RPC.User_Login
import net.igap.network_module.OnResponse
import net.igap.network_module.RequestManager
import net.igap.network_module.model.UserLoginDto
import java.lang.Exception
import java.util.concurrent.CountDownLatch

class LoginService {

    suspend fun login(phoneNumber: String): UserLoginObject {
        val countDownLatch = CountDownLatch(1)
        val userLoginObject = arrayOfNulls<UserLoginObject>(1)
        var req: AbstractObject? = null
        val user_login = User_Login()
        user_login.phoneNumber = phoneNumber.replace("-", "").toLong()
        user_login.appId = 2
        user_login.methodValue =
            ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SMS.number
        user_login.buildForValue =
            ProtoRequest.Request.newBuilder().setId(HelperString.generateKey())
        user_login.countryCode = "IR"
        req = user_login

        RequestManager.getInstance(0)
            .sendRequest(req) { response: AbstractObject?, error: AbstractObject? ->
                if (error == null) {
                    try {
                        val userLoginDto = UserLoginDto.create(response)
                        userLoginObject[0] = UserLoginObject(
                            userLoginDto.phoneNumber,
                            "",
                            "",
                            userLoginDto.userName,
                            userLoginDto.userId,
                            userLoginDto.smsNumbers,
                            userLoginDto.regex,
                            userLoginDto.verifyCodeDigitCount,
                            userLoginDto.authorHash,
                            userLoginDto.callMethodSupported,
                            userLoginDto.resendCodeDelay,
                            userLoginDto.methodpublicue,
                            userLoginDto.countryCode
                        )
                        countDownLatch.countDown()
                    } catch (e: Exception) {
                        print(e)
                    } finally {
                        countDownLatch.countDown()
                    }

                } else {
                    countDownLatch.countDown()
                }
            }
        try {
            countDownLatch.await()
        } catch (e: Exception) {

        }
        return userLoginObject[0]!!

    }


}