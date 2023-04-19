package ru.netology.nmedia.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject
import kotlin.text.Typography.dagger


@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app){

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    val viewModel: AuthViewModel by viewModels()

    val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action!= Intent.ACTION_SEND){
                return@let
            }
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank()!= true){
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment_container)
                .navigate(
                    R.id.action_feedFragment2_to_newPostFragment2,
                    Bundle().apply {
                        textArg = text
                    }
                )

        }

        checkGoogleApiAvailability()



        var currentMenuProvider: MenuProvider? = null
        viewModel.data.observe(this) {
            currentMenuProvider?.also { removeMenuProvider(it) }
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                    val authorized = viewModel.authorized
                    menu.setGroupVisible(R.id.authorized, authorized)
                    menu.setGroupVisible(R.id.unAuthorized, !authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean  =
                    when (menuItem.itemId) {
                        R.id.signIn -> {
                            findNavController(R.id.nav_host_fragment_container).navigate(R.id.signInFragment2)
                            true
                        }
                        R.id.signUp -> {
                            findNavController(R.id.nav_host_fragment_container).navigate(R.id.registrationFragment)
                            true
                        }
                        R.id.logout -> {
                            showSignOutDialog()
                            postViewModel.refreshPosts()

                            true
                        }
                        else -> false
                    }
            }.apply {
                currentMenuProvider = this
            })
        }
    }

    private fun showSignOutDialog(){
        val listener = DialogInterface.OnClickListener{ _, which->
            when(which) {
                DialogInterface.BUTTON_POSITIVE ->  appAuth.removeAuth()
                DialogInterface.BUTTON_NEGATIVE -> Toast.makeText(this,getString(R.string.yes), Toast.LENGTH_SHORT).show()
            }
        }
        val dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(getString(R.string.dialogTitle))
            .setMessage(getString(R.string.dialogMes))
            .setPositiveButton(getString(R.string.yes), listener)
            .setNegativeButton(getString(R.string.no), listener)
            .create()

        dialog.show()
        postViewModel.refreshPosts()
    }

    private fun checkGoogleApiAvailability(){
        with(googleApiAvailability){
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS){
                return@with
            }
            if (isUserResolvableError(code)){
                getErrorDialog(this@AppActivity,code,9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity,getString(R.string.errorGoogle),Toast.LENGTH_LONG).show()

        }
        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }
}




