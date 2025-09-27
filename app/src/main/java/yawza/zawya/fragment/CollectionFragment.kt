package yawza.zawya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import yawza.zawya.adapter.CollectionAdapter
import yawza.zawya.databinding.FragmentCollectionBinding
import yawza.zawya.viewmodel.CollectionViewModel

class CollectionFragment : Fragment() {
    
    private lateinit var binding: FragmentCollectionBinding
    private lateinit var viewModel: CollectionViewModel
    private lateinit var adapter: CollectionAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CollectionViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = CollectionAdapter(emptyList())
        binding.recyclerCollections.layoutManager = LinearLayoutManager(context)
        binding.recyclerCollections.adapter = adapter
    }
    
    private fun observeViewModel() {
        viewModel.collections.observe(viewLifecycleOwner) { collections ->
            adapter = CollectionAdapter(collections)
            binding.recyclerCollections.adapter = adapter
        }
    }
    
    fun updateCollection(brandName: String, newCount: Int) {
        viewModel.updateCollection(brandName, newCount)
    }
}
