package com.pro.stickermaker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pro.stickermaker.MainActivity
import com.pro.stickermaker.R
import com.pro.stickermaker.adapter.StickerPackAdapter
import com.pro.stickermaker.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            if (it is MainActivity) {
                binding.mainRecyclerView.adapter = StickerPackAdapter(it){uniqueIdentifier ->

                    findNavController().navigate(R.id.action_HomeFragment_to_StickerPackFragment)

                }

                binding.createStickerPackButton.setOnClickListener {
                    findNavController().navigate(R.id.action_HomeFragment_to_selectStickerFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}