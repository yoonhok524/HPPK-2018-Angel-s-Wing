package com.youknow.hppk2018.angelswing.ui.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.ui.list.ProductsActivity
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast


class SignInActivity : AppCompatActivity(), AnkoLogger, SignInContract.View {

    private val RC_SIGN_IN = 1018
    private lateinit var mPresenter: SignInContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mPresenter = SignInPresenter(this, PreferenceManager.getDefaultSharedPreferences(this))

        initRegisterView()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            etName.setText(user.displayName)
        } else {
            startSignInFlow()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                mPresenter.isAlreadyExistUser(user!!.email!!)
            } else {
                error("[HPPK] signIn failed - ${response!!.error}")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun showInvalidName(visibility: Int) {
        tvErrName.visibility = visibility
    }

    override fun showInvalidEmail(visibility: Int) {
        tvErrEmail.visibility = visibility
    }

    override fun registerDone(success: Boolean) {
        if (success) {
            startActivity(Intent(this, ProductsActivity::class.java))
            finish()
        }
    }

    override fun showProgressBar(visible: Int) {
        progressBar.visibility = visible
    }

    private fun startSignInFlow() {
        val authProviders = listOf(AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(authProviders)
                        .build(),
                RC_SIGN_IN)
    }

    private fun initRegisterView() {
        val adapterLab = ArrayAdapter.createFromResource(this, R.array.labs, R.layout.spinner_item_black)
        adapterLab.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spnLab.adapter = adapterLab
        spnLab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val partResId = when (position) {
                    0 -> R.array.part1
                    1 -> R.array.part2
                    else -> R.array.partSolution
                }

                val adapterPart = ArrayAdapter.createFromResource(this@SignInActivity, partResId, R.layout.spinner_item_black)
                adapterPart.setDropDownViewResource(R.layout.spinner_dropdown_item)
                spnPart.adapter = adapterPart
            }
        }

        btnRegister.setOnClickListener {
            mPresenter.register(etName.text.toString(), etHpAccount.text.toString(), spnLab.selectedItem.toString(), spnPart.selectedItem.toString())
        }
    }

}