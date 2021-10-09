package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    companion object {
        private const val TAG = "VoterInfoFragment"
    }

    private lateinit var viewModel: VoterInfoViewModel
    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_voter_info,
            container,
            false
        )
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setViewModels()
        setViewModelObservers()
    }

    private fun setViewModels() {
        val electionDao = ElectionDatabase.getInstance(requireContext()).electionDao
        val id = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision

        val viewModelFactory by lazy {
            VoterInfoViewModelFactory(
                dataSource = electionDao,
                electionId = id,
                division = division
            )
        }

        val viewModel: VoterInfoViewModel by viewModels {
            viewModelFactory
        }

        this.viewModel = viewModel
        binding.viewModel = viewModel
    }

    private fun setViewModelObservers() {
        viewModel.goToUrl.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val uri = Uri.parse(it)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent);
            }
        }

        viewModel.isItBeingFollowed.observe(viewLifecycleOwner) {
            if (it) {
                binding.simpleViewFlipper.displayedChild = binding.simpleViewFlipper.indexOfChild(binding.unfollowButton)
            } else {
                binding.simpleViewFlipper.displayedChild = binding.simpleViewFlipper.indexOfChild(binding.followButton)
            }
        }
    }
}