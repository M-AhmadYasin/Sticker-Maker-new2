package com.pro.stickermaker.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pro.stickermaker.R
import com.pro.stickermaker.databinding.StickerViewDialogBinding

class StickerDialog() : DialogFragment(R.layout.sticker_view_dialog) {

    private var _binding: StickerViewDialogBinding? = null
    val binding get() = _binding!!

    init {
        Log.d(TAG, "init: ")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = StickerViewDialogBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
        _binding = StickerViewDialogBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        builder.setCancelable(true)
        Log.d(TAG, "returning builder dialog")
        return builder.create()
//        return super.onCreateDialog(savedInstanceState)
    }

    override fun getView(): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cat, resources.newTheme()))
        binding.dialogCloseButton.setOnClickListener { dismiss() }

    }

    companion object {

        private const val TAG = "StickerDialog_Logs"

    }
}