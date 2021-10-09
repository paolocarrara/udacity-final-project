package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.model.GoToScreenEvent
import com.example.android.politicalpreparedness.network.models.Division

class ElectionsFragment: Fragment() {

    companion object {
        private const val TAG = "ElectionsFragment"
    }

    private lateinit var viewModel: ElectionsViewModel
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_election,
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

        this.viewModel.init()
    }

    private fun setViewModels() {
        val electionDao = ElectionDatabase.getInstance(requireContext()).electionDao

        val viewModelFactory by lazy {
            ElectionsViewModelFactory(electionDao)
        }

        val viewModel: ElectionsViewModel by viewModels {
            viewModelFactory
        }

        this.viewModel = viewModel
        binding.viewModel = viewModel
    }

    private fun setViewModelObservers() {
        binding.upcomingElectionsRecyclerView.adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            viewModel.goToElection(it)
        })

        viewModel.upcomingElections.observe(viewLifecycleOwner) { list ->
            val adapter = binding.upcomingElectionsRecyclerView.adapter as ElectionListAdapter
            adapter.submitList(list)
        }


        binding.saveElectionsRecyclerView.adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            viewModel.goToElection(it)
        })

        viewModel.savedElections.observe(viewLifecycleOwner) { list ->
            val adapter = binding.saveElectionsRecyclerView.adapter as ElectionListAdapter
            adapter.submitList(list)
        }

        viewModel.goToScreen.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                goToScreen(it)
            }
        }
    }

    private fun goToScreen(goToScreenEvent: GoToScreenEvent) {
        when (goToScreenEvent.screenName) {
            "VoterInfoFragment" -> {
                val bundle = goToScreenEvent.data
                val electionId = bundle.getInt("id")
                val division: Division = bundle.getParcelable("division")!!

                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(electionId, division))
            }
        }
    }

}