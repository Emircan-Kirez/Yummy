package com.emircankirez.yummy.ui.presentation.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.emircankirez.yummy.databinding.DialogLoadingBinding


class LoadingDialog constructor(
   context: Context
) {

    private var binding: DialogLoadingBinding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
    private var alertDialog: AlertDialog = createDialog(context)

    private fun createDialog(context: Context) : AlertDialog {
        val dialog =  AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun show(){
        alertDialog.show()
    }

    fun dismiss(){
        alertDialog.dismiss()
    }
}