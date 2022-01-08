package com.example.login_interactor

import com.example.core.DataState
import com.example.core.ProgressBarState
import com.example.core.UIComponent
import com.example.login_domain.UserLoginObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.igap.network_module.service.LoginService

class LoginProcess(private val service: LoginService) {


    fun execute(phoneNumber: String): Flow<DataState<UserLoginObject>> = flow {

        try {
            emit(DataState.Loading<UserLoginObject>(progressBarState = ProgressBarState.Loading))

            val login: UserLoginObject = try {
                service.login(phoneNumber)
            } catch (e: Exception) {
                e.printStackTrace() // log to crashlytics?
                emit(
                    DataState.Response<UserLoginObject>(
                        uiComponent = UIComponent.Dialog(
                            title = "Network Data Error",
                            description = e.message ?: "Unknown error"
                        )
                    )
                )

                UserLoginObject()
            }
            emit(DataState.Data(login))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Response<UserLoginObject>(
                    uiComponent = UIComponent.Dialog(
                        title = "Error",
                        description = e.message ?: "Unknown error"
                    )
                )
            )

        }


    }


}