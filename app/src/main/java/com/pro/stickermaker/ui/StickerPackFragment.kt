package com.pro.stickermaker.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.pro.stickermaker.MainActivity
import com.pro.stickermaker.R
import com.pro.stickermaker.adapter.StickerAdapter
import com.pro.stickermaker.customized.GridSpacingItemDecoration
import com.pro.stickermaker.databinding.FragmentStickerPackBinding
import com.pro.stickermaker.ui.dialog.StickerDialog

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class StickerPackFragment : Fragment() {

    private var _binding: FragmentStickerPackBinding? = null
    private var dialogBox: StickerDialog? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStickerPackBinding.inflate(inflater, container, false)

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menu.clear()
                menuInflater.inflate(R.menu.sticker_pack_menu_items, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        // clearCompletedTasks()
                        true
                    }
                    R.id.action_share -> {
                        // loadTasks(true)
                        true
                    }
                    R.id.action_delete -> {
                        // loadTasks(true)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            if (it is MainActivity) {
                val spanCount = 4
                val spacing =
                    getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp) // 50px
                val includeEdge = true
                binding.recyclerView.addItemDecoration(
                    GridSpacingItemDecoration(
                        spanCount,
                        spacing,
                        includeEdge
                    )
                )
                binding.recyclerView.adapter = StickerAdapter(
                    it
                )
                { uri, longClick, add, remove ->

                    if (!longClick) {
                        Log.d(TAG, "single click")
                        dialogBox = StickerDialog()
                        dialogBox?.show(parentFragmentManager, "StickerDialog")
                        /*dialogBox?.onDismiss(object : DialogInterface {
                            override fun cancel() {
                                dialogBox?.dismiss()
                                dialogBox = null
                            }

                            override fun dismiss() {
                                dialogBox?.dismiss()
                                dialogBox = null
                            }
                        })*/
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "StickerPackFragmentLogs"
    }
}