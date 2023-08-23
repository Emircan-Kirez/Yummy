package com.emircankirez.yummy.ui.presentation.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.emircankirez.yummy.databinding.DialogErrorBinding


class ErrorDialog constructor(
   context: Context
) {

    private var binding: DialogErrorBinding = DialogErrorBinding.inflate(LayoutInflater.from(context))
    private var alertDialog: AlertDialog = createDialog(context)

    private fun createDialog(context: Context) : AlertDialog {
        val dialog =  AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun show(desc: String, onClicked: () -> Unit){
        binding.apply {
            tvDesc.text = desc

            btnDialogButton.setOnClickListener {
                onClicked.invoke()
                dismiss()
            }

            alertDialog.show()
        }
    }

    private fun dismiss(){
        alertDialog.dismiss()
    }
}