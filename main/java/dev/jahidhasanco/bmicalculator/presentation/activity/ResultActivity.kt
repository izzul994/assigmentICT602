package dev.jahidhasanco.bmicalculator.presentation.activity



import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import dev.jahidhasanco.bmicalculator.R
import dev.jahidhasanco.bmicalculator.databinding.ActivityResultBinding
import dev.jahidhasanco.bmicalculator.utils.displayToast
import dev.jahidhasanco.bmicalculator.utils.saveBitmap


class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val _binding get() = binding

    private var weight: Double =1.0
    private var height: Double = 1.0
    private var result: Double = 0.0
    private var gender: Int = 0

    // handle permission dialog
    private val requestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) shareImage() else showErrorDialog()
        }

    private fun showErrorDialog() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        weight = intent.getDoubleExtra("Weight", 50.0)
        height = intent.getDoubleExtra("Height", 1.0)
        gender = intent.getIntExtra("Gender", 0)

        bmiCal()
        animationView()
        _binding.reloadBtn.setOnClickListener {

            backPreviousPage()

        }

        _binding.deleteBtn.setOnClickListener {

            backPreviousPage()

        }

        _binding.shareBtn.setOnClickListener {
            shareImage()
        }

    }

    private fun shareImage() {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }

        // unHide the app logo and name
//        showAppNameAndLogo()
        val imageURI = _binding.detailView.drawToBitmap().let { bitmap ->
 //           hideAppNameAndLogo()
            saveBitmap(this, bitmap)
        } ?: run {
            displayToast("Error occurred!")
            return
        }

        val intent = ShareCompat.IntentBuilder(this)
            .setType("image/jpeg")
            .setStream(imageURI)
            .intent

        startActivity(Intent.createChooser(intent, null))
    }

//    private fun showAppNameAndLogo() = with(_binding.transactionDetails) {
//        appIconForShare.show()
//        appNameForShare.show()
//    }
//
//    private fun hideAppNameAndLogo() = with(binding.transactionDetails) {
//        appIconForShare.hide()
//        appNameForShare.hide()
//    }

    private fun isStoragePermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED


//    private fun shareText() = with(binding) {
//        val shareMsg = getString(
//            3,
//            ""
//            R.string.share_message,
//            transactionDetails.title.text.toString(),
//            transactionDetails.amount.text.toString(),
//            transactionDetails.type.text.toString(),
//            transactionDetails.tag.text.toString(),
//            transactionDetails.date.text.toString(),
//            transactionDetails.note.text.toString(),
//            transactionDetails.createdAt.text.toString()
//        )
//
//        val intent = ShareCompat.IntentBuilder(Activity())
//            .setType("text/plain")
//            .setText(shareMsg)
//            .intent
//
//        startActivity(Intent.createChooser(intent, null))
//    }


    private fun backPreviousPage(){
        animationViewUp()
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        }, 600)

    }

    private fun animationView() {

        _binding.apply {

            deskImage.translationY = 100f
            resultText.translationY = 40f
            bmiText.translationY = 50f
            bmiTextNormal.translationY = 50f
            deleteBtn.translationY = 70f
            reloadCardView.translationY = 70f
            shareBtn.translationY = 70f

            deskImage.alpha = 0f
            resultText.alpha = 0f
            bmiText.alpha = 0f
            bmiTextNormal.alpha = 0f
            deleteBtn.alpha = 0f
            reloadCardView.alpha = 0f
            shareBtn.alpha = 0f

            deskImage.setPadding(100)

            deskImage.animate().translationY(0f).alpha(1f).setDuration(500).setStartDelay(300)
                .start()
            deskImage.setPadding(0)
            resultText.animate().translationY(0f).alpha(1f).setDuration(500).setStartDelay(500)
                .start()
            bmiText.animate().translationY(0f).alpha(1f).setDuration(500).setStartDelay(450).start()
            bmiTextNormal.animate().translationY(0f).alpha(.3f).setDuration(500).setStartDelay(500)
                .start()
            deleteBtn.animate().translationY(0f).alpha(.3f).setDuration(500).setStartDelay(600)
                .start()
            reloadCardView.animate().translationY(0f).alpha(1f).setDuration(500).setStartDelay(750)
                .start()
            shareBtn.animate().translationY(0f).alpha(.3f).setDuration(500).setStartDelay(900)
                .start()


        }
    }

    private fun animationViewUp() {

        _binding.apply {

            textView.animate().translationY(0f).alpha(0f).setDuration(500).setStartDelay(0)
                .start()
            deskImage.animate().translationY(-250f).alpha(0f).setDuration(500).setStartDelay(0)
                .start()

            resultText.animate().translationY(-250f).alpha(0f).setDuration(500).setStartDelay(50)
                .start()
            bmiText.animate().translationY(-250f).alpha(0f).setDuration(500).setStartDelay(100)
                .start()
            bmiTextNormal.animate().translationY(-250f).alpha(0f).setDuration(500).setStartDelay(150)
                .start()
            deleteBtn.animate().translationY(-250f).alpha(0f).setDuration(300).setStartDelay(200)
                .start()
            reloadCardView.animate().translationY(-250f).alpha(0f).setDuration(300)
                .setStartDelay(250).start()
            shareBtn.animate().translationY(-250f).alpha(0f).setDuration(300).setStartDelay(300)
                .start()


        }
    }


    private fun bmiCal() {
        if (height > 0 && weight > 0) {
            if (gender == 0) {
                bmiCalMale()
            } else if (gender == 1) {
                bmiCalFemale()
            }
            showResult()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun showResult() {

        val solution = String.format("%.1f", result)
        _binding.resultText.text = solution
        _binding.bmiText.apply {
            if (result < 18.5) {
                this.text = "Malnutrition risk"
            } else if (result >= 18.5 && result < 24.9) {
                this.text = "Low risk"
            } else if (result >= 25 && result < 29.9) {
                this.text = "Enchanced risk"
            }
            else if (result >= 30 && result < 34.9) {
                this.text = "Medium risk"
            }
            else if (result >= 35 && result < 39.9) {
                this.text = "High risk"
            }
            else if (result >= 40) {
                this.text = "Very high risk"
            }
        }


    }

    private fun bmiCalMale() {
        result = ((weight / (height * height)) * 10000)
    }

    private fun bmiCalFemale() {
        result = ((weight / (height * height)) * 10000)
    }

    override fun onBackPressed() {
        backPreviousPage()
    }

}