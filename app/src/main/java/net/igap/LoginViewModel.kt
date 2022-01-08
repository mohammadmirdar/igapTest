package net.igap

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.DataState
import com.example.core.ProgressBarState
import com.example.core.UIComponent
import com.example.login_domain.UserLoginObject
import com.example.login_interactor.LoginProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.igap.network_module.service.LoginService

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var progressBarState: MutableLiveData<ProgressBarState>? = null
    var userLoginObject: MutableLiveData<UserLoginObject>? = null

    val interactor by lazy {
        LoginProcess(LoginService())
    }

    init {
        userLoginObject = MutableLiveData()
        progressBarState = MutableLiveData()

    }

    fun login(phoneNumber: String) {

        interactor.execute(phoneNumber).onEach { dataState ->
            when (dataState) {
                is DataState.Response -> {
                    when (dataState.uiComponent) {
                        is UIComponent.Dialog -> {

                        }

                        is UIComponent.None -> {

                        }
                    }
                }


                is DataState.Data -> {
                    userLoginObject!!.postValue(dataState.data ?: UserLoginObject())
                }

                is DataState.Loading -> {
                    progressBarState!!.postValue(dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)


    }

}