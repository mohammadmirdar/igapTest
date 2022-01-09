package net.igap.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.login_domain.UserLoginObject
import net.igap.network_module.RequestManager
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import android.text.InputType
import android.text.TextWatcher
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import net.igap.*
import net.igap.adapter.AdapterDialog
import net.igap.model.StructCountry
import net.igap.utils.CountryListComparator
import net.igap.utils.CountryReader
import net.igap.utils.LayoutCreator
import net.igap.utils.SoftKeyboard
import net.igap.viewmodel.LoginViewModel


class MainActivity : AppCompatActivity() {
    lateinit var structCountryArrayList: ArrayList<StructCountry>
    lateinit var loginViewModel: LoginViewModel
    lateinit var rootView: LinearLayout
    lateinit var selectCountryRootview: LinearLayout
    lateinit var prefixNumberRootView: FrameLayout
    lateinit var prefixNumberTextView: TextView
    lateinit var selectCountryCodeRootView: FrameLayout
    lateinit var selectCountryCodeTextView: TextView
    lateinit var inputPhoneNumberRootView: FrameLayout
    lateinit var inputPhoneNumberEditTex: EditText
    lateinit var loginButton: Button
    lateinit var loginWighQrCodeButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutCreator.context = this
        rootView = LinearLayout(this)
        rootView.tag = "rootView"
        rootView.orientation = LinearLayout.VERTICAL
        selectCountryRootview = LinearLayout(this)
        selectCountryRootview.tag = "selectCountryRootview"
        rootView.addView(
            selectCountryRootview,
            LayoutCreator.createLinear(
                LayoutCreator.WRAP_CONTENT,
                LayoutCreator.WRAP_CONTENT,
                Gravity.CENTER, 0, 305, 0, 0
            )
        )


        selectCountryRootview.orientation = LinearLayout.HORIZONTAL
        prefixNumberRootView = FrameLayout(this)
        prefixNumberRootView.tag = "prefixNumberRootView"
        selectCountryRootview.addView(
            prefixNumberRootView,
            LayoutCreator.createLinear(82, 52, Gravity.CENTER)
        )


        prefixNumberTextView = TextView(this)
        prefixNumberTextView.tag = "prefixNumberTextView"
        prefixNumberRootView.addView(
            prefixNumberTextView,
            LayoutCreator.createFrame(
                LayoutCreator.WRAP_CONTENT,
                LayoutCreator.WRAP_CONTENT,
                Gravity.CENTER
            )
        )

        prefixNumberRootView.background = resources.getDrawable(R.drawable.round_border)
        prefixNumberTextView.text = "+98"


        selectCountryCodeRootView = FrameLayout(this)
        selectCountryCodeRootView.tag = "selectCountryCodeRootView"
        selectCountryRootview.addView(
            selectCountryCodeRootView,
            LayoutCreator.createLinear(254, 52, Gravity.CENTER, 7, 0, 0, 0)
        )
        selectCountryCodeTextView = TextView(this)
        selectCountryCodeTextView.tag = "selectCountryCodeTextView"
        selectCountryRootview.gravity = Gravity.CENTER

        selectCountryCodeRootView.addView(
            selectCountryCodeTextView,
            LayoutCreator.createFrame(
                LayoutCreator.WRAP_CONTENT,
                LayoutCreator.WRAP_CONTENT, Gravity.CENTER
            )
        )


        selectCountryCodeRootView.background = resources.getDrawable(R.drawable.round_border)
        selectCountryCodeTextView.text = "IR"

        inputPhoneNumberRootView = FrameLayout(this)
        inputPhoneNumberRootView.tag = "inputPhoneNumberRootView"
        inputPhoneNumberRootView.background = resources.getDrawable(R.drawable.round_border)
        rootView.addView(
            inputPhoneNumberRootView,
            LayoutCreator.createLinear(343, 52, Gravity.CENTER, 0, 11, 0, 0)
        )

        inputPhoneNumberEditTex = EditText(this)
        inputPhoneNumberEditTex.tag = "inputPhoneNumberEditTex"
        inputPhoneNumberEditTex.hint = "your phone no."
        inputPhoneNumberEditTex.background = null
        inputPhoneNumberEditTex.gravity = Gravity.CENTER
        inputPhoneNumberEditTex.maxLines = 1
        inputPhoneNumberEditTex.isSingleLine = true
        inputPhoneNumberEditTex.maxWidth = 11
        inputPhoneNumberEditTex.inputType = InputType.TYPE_CLASS_PHONE



        inputPhoneNumberRootView.addView(
            inputPhoneNumberEditTex,
            LayoutCreator.createFrame(
                LayoutCreator.MATCH_PARENT,
                LayoutCreator.MATCH_PARENT,
                Gravity.CENTER
            )
        )

        loginButton = Button(this)
        loginButton.tag = "loginButton"
        loginButton.background = resources.getDrawable(R.drawable.round_button)
        loginButton.text = "Login"
        loginButton.isEnabled = false
        loginButton.setTextColor(Color.WHITE)
        rootView.addView(
            loginButton,
            LayoutCreator.createLinear(343, 52, Gravity.CENTER, 0, 11, 0, 0)
        )

        loginWighQrCodeButton = Button(this)
        loginWighQrCodeButton.tag = "loginWighQrCodeButton"
        loginWighQrCodeButton.background = resources.getDrawable(R.drawable.round_button)
        loginWighQrCodeButton.text = "login with QR-code"
        loginWighQrCodeButton.setTextColor(Color.WHITE)
        rootView.addView(
            loginWighQrCodeButton,
            LayoutCreator.createLinear(343, 52, Gravity.CENTER, 0, 11, 0, 0)
        )

        setContentView(rootView)


        selectCountryCodeRootView.setOnClickListener {
            showCountryDialog()
        }
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        loginButton.setOnClickListener {
            if (RequestManager.getInstance(0).isSecure) {
                loginViewModel.login(inputPhoneNumberEditTex.text.toString().trim())
            }
        }

        inputPhoneNumberEditTex.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 0) {
                    loginButton.isEnabled = true
                    loginButton.background = resources.getDrawable(R.drawable.round_button_green)
                } else {
                    loginButton.background = resources.getDrawable(R.drawable.round_button)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        loginViewModel.userLoginObject?.observe(this, Observer<UserLoginObject> {
            Toast.makeText(this, it.userName, Toast.LENGTH_LONG).show()
        })

    }


    fun showCountryDialog() {
        val dialogChooseCountry = Dialog(this)
        dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogChooseCountry.setContentView(R.layout.rg_dialog)
        dialogChooseCountry.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val setWidth = (getResources().getDisplayMetrics().widthPixels * 0.9).toInt()
        val setHeight = (getResources().getDisplayMetrics().heightPixels * 0.9).toInt()
        dialogChooseCountry.getWindow()?.setLayout(setWidth, setHeight);
        val textTitle: TextView = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar)
        val searchView: androidx.appcompat.widget.SearchView =
            dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar)
        textTitle.setOnClickListener {
            searchView.setIconified(false)
            searchView.setIconifiedByDefault(true)
            textTitle.setVisibility(View.GONE)
        }
        searchView.setOnCloseListener {
            textTitle.setVisibility(View.VISIBLE)
            false
        }
        fillStructCountryList(
            CountryReader()
                .readFromAssetsTextFile("country.txt", this)
        )
        val listView: ListView = dialogChooseCountry.findViewById(R.id.lstContent)
        val adapterDialog =
            AdapterDialog(structCountryArrayList)
        listView.adapter = adapterDialog
        listView.setOnItemClickListener { parent, view, position, id ->

            dialogChooseCountry.dismiss();
        }

        val root: ViewGroup = dialogChooseCountry.findViewById(android.R.id.content)
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val softKeyBoard: SoftKeyboard =
            SoftKeyboard(root, inputMethodManager)
        softKeyBoard.setSoftKeyboardCallback(object : SoftKeyboard.SoftKeyboardChanged {
            override fun onSoftKeyboardHide() {
                if (searchView.getQuery().toString().length > 0) {
                    searchView.setIconified(false);
                    searchView.clearFocus();
                    textTitle.setVisibility(View.GONE);
                } else {
                    searchView.setIconified(true);
                    textTitle.setVisibility(View.VISIBLE);
                }
                adapterDialog.notifyDataSetChanged();
            }

            override fun onSoftKeyboardShow() {
                textTitle.setVisibility(View.GONE)
            }

        })

        val border: View = dialogChooseCountry.findViewById(R.id.rg_borderButton)
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {}
            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {
                if (i > 0) {
                    border.visibility = View.VISIBLE
                } else {
                    border.visibility = View.GONE
                }
            }
        })
        AdapterDialog.mSelectedVariation = -1
        adapterDialog.notifyDataSetChanged()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterDialog.getFilter().filter(newText)
                return false;
            }

        })
        val textOkDialog: TextView = dialogChooseCountry.findViewById(R.id.rg_txt_okDialog)
        textOkDialog.setOnClickListener {
            dialogChooseCountry.dismiss()
        }
        if (!isFinishing()) {
            dialogChooseCountry.show();
        }
    }

    fun fillStructCountryList(stringBuilder: StringBuilder) {
        structCountryArrayList = ArrayList<StructCountry>()
        val list = stringBuilder.toString()
        // Split line by line Into array
        // Split line by line Into array
        val listArray = list.split("\\n").toTypedArray()
        //Convert array
        //Convert array

        val structCountry = StructCountry()

        structCountry.setCountryCode("+98")
        structCountry.setAbbreviation("IR")
        structCountry.setName("Iran")

        structCountryArrayList.add(structCountry)

        Collections.sort(
            structCountryArrayList,
            CountryListComparator()
        )
    }
}