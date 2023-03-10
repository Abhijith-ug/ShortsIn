package com.example.mymvvmnewsapp.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mymvvmnewsapp.Session
import com.example.mymvvmnewsapp.adapters.CountriesSpinnerAdapter
import com.example.mymvvmnewsapp.models.Country
import com.example.mymvvmnewsapp.models.User
import com.example.mymvvmnewsapp.ui.viewmodels.UserViewModel
import com.example.mymvvmnewsapp.ui.viewmodels.UserViewModelProviderFactory
import com.example.mymvvmnewsapp.util.CommonFunctions
import com.example.mymvvmnewsapp.util.Constants
import com.example.mymvvmnewsapp.util.Constants.Companion.IS_DEBUG
import com.example.mymvvmnewsapp.util.Constants.Companion.IS_USER_DEFAULT_VALUE
import com.example.mymvvmnewsapp.util.Constants.Companion.PROFILE_IMAGES
import com.example.mymvvmnewsapp.util.Constants.Companion.UID
import com.example.mymvvmnewsapp.util.Constants.Companion.USER_AUTH
import com.example.mymvvmnewsapp.util.UiState
import com.example.mynewsappdemo.R
import com.example.mynewsappdemo.databinding.ActivityProfileAndSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class ProfileAndSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileAndSettingBinding
    lateinit var viewModel: UserViewModel
    private lateinit var logoutAlertDialog: AlertDialog
    private lateinit var selectedCountry: String
    private lateinit var spinnerAdapter: com.example.mymvvmnewsapp.adapters.CountriesSpinnerAdapter

    // Pending to move to MVVM.
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAndSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize something.
        initializeComponents()
        // Get existing data to views.
        getProfileImageFromFirebaseStorage()
        //readDataFromRealtimeDatabase()
        getUserFromFireStore()
        // Get existing details to views.
        getDetailsToViews()
        // Get the selected item from the spinner.
        binding.btnSaveProfileInfo.setOnClickListener {
            val position = binding.spinnerCountries.selectedItemPosition
            val selectedItem = binding.spinnerCountries.adapter.getItem(position) as Country
            val user = User(
                auth.uid.toString(),
                binding.etFullName.text.toString(),
                binding.etUserEmail.text.toString(),
                selectedItem.value.toString(),
                IS_USER_DEFAULT_VALUE
            )

            viewModel.updateUserProfileData(user) {
                when (it) {
                    is UiState.Failure -> {
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                    UiState.Loading -> {}
                    is UiState.Success<*> -> {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                        com.example.mymvvmnewsapp.Session.user = user
                        //readDataFromRealtimeDatabase()
                    }
                }
            }
        }

        binding.contactUs.setOnClickListener {
            binding.contactUs.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(Constants.CONTACT_US))
                startActivity(intent)
            }
        }

        binding.ivProfileImage.setOnClickListener {
            // Create an intent to open the gallery
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }

        binding.btnLogout.setOnClickListener {
            logoutAlertDialog.show()
        }
    }

    private val logoutOnClickListener by lazy {
        DialogInterface.OnClickListener { _, _ ->
            viewModel.logout()
            // Go to user intent.
            val intent = Intent(this@ProfileAndSettingsActivity, UserActivity::class.java)
            startActivity(intent)
            finishAffinity()
            // Clear saved user data from shared preferences.
            viewModel.clearSavedUserDataSharedPreference(USER_AUTH)
        }
    }

    private fun initializeComponents() {
        // Pending to move to MVVM.
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        // Instantiate the news repository.
        val viewModelProviderFactory = UserViewModelProviderFactory(application)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]

        // Initialize logout alert dialog.
        logoutAlertDialog =
            AlertDialog.Builder(this).setTitle("Logout").setMessage("Are you sure want to logout?").setIcon(R.drawable.ic_logout).setPositiveButton(
                R.string.logout, logoutOnClickListener
            ).setNegativeButton(R.string.cancel, null).create()

        // Set up countries spinner.
        spinnerAdapter = com.example.mymvvmnewsapp.adapters.CountriesSpinnerAdapter(this, Constants.COUNTRIES)
        binding.spinnerCountries.adapter = spinnerAdapter
        binding.spinnerCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCountry = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getDetailsToViews() {
        auth.currentUser?.let {
            binding.tvUserPhoneNumber.text = it.phoneNumber.toString()
        }
    }

    private fun getProfileImageFromFirebaseStorage() {
        viewModel.getProfileImageFromFirebaseStorage() {
            when (it) {
                is UiState.Failure -> {
                    if (IS_DEBUG) {
                        Toast.makeText(this, "No profile picture", Toast.LENGTH_SHORT).show()
                    }
                }
                is UiState.Loading -> {}
                is UiState.Success -> {
                    binding.ivProfileImage.setImageBitmap(it.data)
                }
            }
        }
    }

    private fun getUserFromFireStore() {
        viewModel.getUserFromFireStore(auth.uid.toString()) {
            when (it) {
                is UiState.Failure -> {}
                is UiState.Loading -> {}
                is UiState.Success -> {
                    val user = it.data
                    binding.etFullName.setText(user.fullName)
                    if (user.fullName.trim().isEmpty()) binding.tvUserFullName.text = "Not Available"
                    else binding.tvUserFullName.text = user.fullName
                    binding.etUserEmail.setText(user.email)
                    selectedCountry = user.countryCode
                    // Set spinner selected item.
                    val adapter = binding.spinnerCountries.adapter as com.example.mymvvmnewsapp.adapters.CountriesSpinnerAdapter
                    val position = adapter.getPosition(getCountryCodePosition(it.data.countryCode))
                    binding.spinnerCountries.setSelection(position)
                    // Update user on session.
                    Session.user = user
                }
            }
        }
    }

    private fun getCountryCodePosition(countryCode: String): Country {
        for (country in Constants.COUNTRIES) {
            if (country.value == countryCode.trim()) {
                return country
            }
        }
        return Constants.COUNTRIES[0]
    }

    private fun updateProfileImageOnFirebase(profileImageUri: Uri) {
        viewModel.updateProfileImage(profileImageUri) {
            when (it) {
                is UiState.Failure -> {}
                is UiState.Loading -> {}
                is UiState.Success<*> -> {
                    Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val profileImageUri = data?.data!!
            // Get image bitmap and reduce the size of image.
            val bitmapStudentImage = CommonFunctions.resizeBitmap(
                MediaStore.Images.Media.getBitmap(
                    contentResolver, profileImageUri
                )
            )
            binding.ivProfileImage.setImageBitmap(bitmapStudentImage)
            updateProfileImageOnFirebase(profileImageUri)
        }
    }
}



