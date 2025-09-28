package yawza.zawya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import yawza.zawya.adapter.StickerTransferAdapter
import yawza.zawya.databinding.FragmentTransactionsBinding
import yawza.zawya.viewmodel.TransactionsViewModel

class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var adapter: StickerTransferAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupUI()
        observeViewModel()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh stickers to reflect current collection state
        viewModel.refreshStickers()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TransactionsViewModel::class.java]
    }

    private fun setupUI() {
        // Setup friend ID input
        binding.btnAddFriend.setOnClickListener {
            val friendId = binding.editFriendId.text.toString().trim()
            if (friendId.isNotEmpty()) {
                viewModel.setFriendId(friendId)
                binding.textFriendName.text = "Friend: $friendId"
                binding.textFriendName.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Please enter a friend ID", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup send button
        binding.btnSend.setOnClickListener {
            val selectedStickers = adapter.getSelectedStickers()
            if (selectedStickers.isNotEmpty()) {
                viewModel.sendRequest(selectedStickers)
            } else {
                Toast.makeText(context, "Please select stickers to send", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup transfer button
        binding.btnTransfer.setOnClickListener {
            val selectedStickers = adapter.getSelectedStickers()
            if (selectedStickers.isNotEmpty()) {
                viewModel.transferStickers(selectedStickers)
            } else {
                Toast.makeText(context, "Please select stickers to transfer", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup send proposal button
        binding.btnSendProposal.setOnClickListener {
            val selectedStickers = adapter.getSelectedStickers()
            if (selectedStickers.isNotEmpty()) {
                viewModel.sendRequest(selectedStickers)
            } else {
                Toast.makeText(context, "Please select stickers to send", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.stickers.observe(viewLifecycleOwner) { stickers ->
            adapter = StickerTransferAdapter(stickers) { selectedStickers ->
                updateTransferButton(selectedStickers)
            }
            binding.recyclerStickers.layoutManager = GridLayoutManager(context, 3)
            binding.recyclerStickers.adapter = adapter
        }

        viewModel.transferResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                // Check if this was a send request or transfer
                val isSendRequest = binding.btnSend.isEnabled && binding.btnSend.alpha == 1.0f
                if (isSendRequest) {
                    Toast.makeText(context, "Request sent! Waiting for friend's response...", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Stickers transferred successfully!", Toast.LENGTH_SHORT).show()
                }
                binding.editFriendId.text?.clear()
                binding.textFriendName.visibility = View.GONE
                adapter.clearSelection()
            } else {
                Toast.makeText(context, "Operation failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateTransferButton(selectedStickers: List<String>) {
        val hasSelection = selectedStickers.isNotEmpty()
        
        // Show/hide send proposal button
        binding.btnSendProposal.visibility = if (hasSelection) View.VISIBLE else View.GONE
        
        // Update transfer controls
        binding.btnSend.isEnabled = hasSelection
        binding.btnTransfer.isEnabled = hasSelection
        binding.textSelectedCount.text = "Selected: ${selectedStickers.size}"
    }
}
