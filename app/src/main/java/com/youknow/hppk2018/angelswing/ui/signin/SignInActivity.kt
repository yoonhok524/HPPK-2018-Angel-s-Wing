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
import org.jetbrains.anko.error
import org.jetbrains.anko.info


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
            info("[HPPK] onCreate - user: ${user.email}, ${user.displayName}")
            etName.setText(user.displayName)
        } else {
            info("[HPPK] onCreate - startSignInFlow")
            startSignInFlow()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                info("[HPPK] onActivityResult - ${user!!.email}")
                mPresenter.isAlreadyExistUser(user!!.email!!)
            } else {
                error("[HPPK] signIn failed - ${response!!.error}")
                response!!.error!!.printStackTrace()
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
                    2 -> R.array.partSolution
                    else -> R.array.group
                }

                val adapterPart = ArrayAdapter.createFromResource(this@SignInActivity, partResId, R.layout.spinner_item_black)
                adapterPart.setDropDownViewResource(R.layout.spinner_dropdown_item)
                spnPart.adapter = adapterPart
            }
        }

        btnRegister.setOnClickListener {
            info("[HPPK] btnRegister on clicked - email: ${FirebaseAuth.getInstance().currentUser!!.email}")
            val lab = when(spnLab.selectedItem.toString()) {
                getString(R.string.lab_1) -> "1랩"
                getString(R.string.lab_2) -> "2랩"
                getString(R.string.lab_solution) -> "솔루션랩"
                getString(R.string.group) -> "그룹"
                else -> "All"
            }

            val part = when(spnPart.selectedItem.toString()) {
                getString(R.string.part_chaesukyoung) -> "파트-채수경"
                getString(R.string.part_choijunyoung) -> "파트-최준영"
                getString(R.string.part_chotaegyun) -> "파트-조태균"
                getString(R.string.part_dotaehoi) -> "파트-도대회"
                getString(R.string.part_hawoohwa) -> "파트-하우화"
                getString(R.string.part_heonam) -> "파트-허남"
                getString(R.string.part_jungjintae) -> "파트-정진태"
                getString(R.string.part_kanghyungjong) -> "파트-강형종"
                getString(R.string.part_kimbyoungyue) -> "파트-김병유"
                getString(R.string.part_kimhaengnan) -> "파트-김행난"
                getString(R.string.part_kimsudong) -> "파트-김수동"
                getString(R.string.part_kwonyongchan) -> "파트-권용찬"
                getString(R.string.part_leegeunchul) -> "파트-이근철"
                getString(R.string.part_leejoongmok) -> "파트-이중목"
                getString(R.string.part_leekeechang) -> "파트-이기창"
                getString(R.string.part_mohamad) -> "파트-Mohamad"
                getString(R.string.part_siminbo) -> "파트-심인보"
                getString(R.string.part_system_architect) -> "파트-System Architect"
                else -> "All"
            }
            mPresenter.register(etName.text.toString(), etHpAccount.text.toString(), lab, part)
        }
    }

}