package net.igap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.example.core.ProgressBarState
import com.example.login_domain.UserLoginObject
import com.example.login_interactor.LoginProcess
import dagger.hilt.android.AndroidEntryPoint
import net.igap.network_module.RequestManager
import net.igap.network_module.WebSocketClient
import net.igap.network_module.service.LoginService


class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        button = findViewById(R.id.login_btn)

        button.setOnClickListener {
            if (RequestManager.getInstance(0).isSecure) {
                loginViewModel.login("09301932220")
            }
        }


        loginViewModel.userLoginObject?.observe(this, Observer<UserLoginObject> {
            Toast.makeText(this, it.userName, Toast.LENGTH_LONG).show()
        })

    }
}