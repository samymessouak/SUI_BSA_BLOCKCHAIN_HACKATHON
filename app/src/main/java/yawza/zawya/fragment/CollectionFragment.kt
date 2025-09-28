package yawza.zawya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import yawza.zawya.R
import yawza.zawya.adapter.CollectionAdapter
import yawza.zawya.adapter.StickerCollectionAdapter
import yawza.zawya.databinding.FragmentCollectionBinding
import yawza.zawya.models.CollectionItem
import yawza.zawya.models.StickerCollectionItem
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
    
    override fun onResume() {
        super.onResume()
        // Refresh collections when fragment becomes visible
        viewModel.refreshCollections()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CollectionViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = CollectionAdapter(emptyList()) { collection ->
            showStickerDetails(collection)
        }
        binding.recyclerCollections.layoutManager = LinearLayoutManager(context)
        binding.recyclerCollections.adapter = adapter
    }
    
    private fun observeViewModel() {
        viewModel.collections.observe(viewLifecycleOwner) { collections ->
            adapter = CollectionAdapter(collections) { collection ->
                showStickerDetails(collection)
            }
            binding.recyclerCollections.adapter = adapter
        }
    }
    
    fun updateCollection(brandName: String, newCount: Int) {
        viewModel.updateCollection(brandName, newCount)
    }
    
    private fun showStickerDetails(collection: CollectionItem) {
        // Create a custom dialog layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sticker_collection, null)
        
        // Setup the dialog
        val dialog = android.app.AlertDialog.Builder(context)
            .setTitle("${collection.brandIcon} ${collection.brandName} Collection")
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        
        // Populate with stickers
        setupStickerGrid(dialogView, collection)
        
        dialog.show()
    }
    
    private fun setupStickerGrid(dialogView: View, collection: CollectionItem) {
        val recyclerView = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_stickers)
        val progressText = dialogView.findViewById<android.widget.TextView>(R.id.text_progress)
        
        // Set progress text
        progressText.text = "Progress: ${collection.collectedStickers}/${collection.totalStickers} stickers"
        
        // Create sticker list (mock data for now)
        val stickers = createStickerList(collection)
        
        // Setup RecyclerView
        val adapter = StickerCollectionAdapter(stickers)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 4)
        recyclerView.adapter = adapter
    }
    
    private fun createStickerList(collection: CollectionItem): List<StickerCollectionItem> {
        val stickers = mutableListOf<StickerCollectionItem>()
        
        // Create mock stickers based on collection
        for (i in 1..collection.totalStickers) {
            val isOwned = i <= collection.collectedStickers
            stickers.add(
                StickerCollectionItem(
                    id = "${collection.brandName.lowercase()}_$i",
                    name = "${collection.brandName} Sticker $i",
                    isOwned = isOwned,
                    brandIcon = collection.brandIcon
                )
            )
        }
        
        return stickers
    }
}
