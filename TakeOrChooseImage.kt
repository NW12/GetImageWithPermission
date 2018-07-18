/* 
This is a kotlin class

You need to call checkSelfPermission method from where you want to get the image
Call - > checkSelfPermission()
*/


private val cameraImage: Int = 1
private val galleryImage: Int = 2
private val writePermission: Int = 3

private var mCurrentPhotoPath: String = ""
 
 
private fun checkSelfPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
            val writePer = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, writePer, writePermission)
        } else showImageDialog()
    }
    
    
    
        private fun showImageDialog() {
        val options = arrayOf<CharSequence>(getString(R.string.take_photo),
                getString(R.string.choose_from_gallery), getString(R.string.cancel))
        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.add_photo))
        alert.setItems(options) { dialog, which ->
            when {
                options[which] == options[0] -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()))
                    } else {
                        val uri = FileProvider.getUriForFile(this,
                                BuildConfig.APPLICATION_ID + ".provider", createImageFile())
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivityForResult(intent, cameraImage)
                }
                options[which] == options[1] -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, galleryImage)
                }
                else -> dialog.dismiss()
            }
        }
        alert.show()
    }






    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "JPEG_Profile.jpg"
        val dirPath = Environment.getExternalStorageDirectory().absolutePath +
                File.separator + System.currentTimeMillis() + imageFileName

        val dir = File(dirPath)

        mCurrentPhotoPath = "file:" + dir.absolutePath
        return dir
    }
    
    
    
    
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == cameraImage) {
                val imageURI = Uri.parse(mCurrentPhotoPath)
                profile_iv.setImageURI(imageURI)

                val pathToRemove = mCurrentPhotoPath.removePrefix("file:")
                val file = File(pathToRemove)
                if (file.exists()) {
                    file.delete()
                }

            } else if (requestCode == galleryImage) {
                val selectedImage = data!!.data
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                val c = contentResolver.query(selectedImage, filePath, null, null, null)
                c.moveToFirst()
                val columnIndex = c.getColumnIndex(filePath[0])
                val picturePath = c.getString(columnIndex)
                c.close()
                val thumbnail = BitmapFactory.decodeFile(picturePath)
                val bitmap = Bitmap.createScaledBitmap(thumbnail, profile_iv.width,
                        profile_iv.height, false)
                profile_iv.setImageBitmap(bitmap)
            }
        }
}