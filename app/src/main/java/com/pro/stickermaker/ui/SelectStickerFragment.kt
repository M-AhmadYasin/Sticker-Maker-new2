package com.pro.stickermaker.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pro.stickermaker.MainActivity
import com.pro.stickermaker.adapter.StickerAdapter
import com.pro.stickermaker.customized.GridSpacingItemDecoration
import com.pro.stickermaker.databinding.FragmentSelectStickersBinding

class SelectStickerFragment : Fragment() {
    private var _binding: FragmentSelectStickersBinding? = null
    private var adapter: StickerAdapter? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectStickersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            if (it is MainActivity) {
                val spanCount = 4
                val spacing =
                    getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp)
                val includeEdge = true
                binding.recyclerView.addItemDecoration(
                    GridSpacingItemDecoration(
                        spanCount,
                        spacing,
                        includeEdge
                    )
                )
                adapter = StickerAdapter(
                    it, allowLongClick = false, allowSelection = true
                ) { uri, longClick, add, remove ->

                }
                binding.recyclerView.adapter = adapter
                binding.linearLayout.setOnClickListener {

                }
                binding.arrowPlaceHolder.setOnClickListener {

                }
                binding.fab.setOnClickListener {
                    if (binding.fab.isExtended) {
                        binding.fab.shrink()
                        adapter?.enableSelection(true)
                    } else {
                        binding.fab.extend()
                        adapter?.enableSelection(false)
                    }
                }
            }
        }

        adapter?.selectionStateInvoke = {
            Log.d(TAG, "selection invoked ")
            if(it){
                binding.fab.shrink()
            }else{
                binding.fab.extend()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        private const val TAG = "SelectStickerFragment_Logs"
    }
}