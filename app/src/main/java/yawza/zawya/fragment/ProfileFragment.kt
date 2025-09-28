package yawza.zawya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import yawza.zawya.databinding.FragmentProfileBinding
import yawza.zawya.manager.AuthManager
import yawza.zawya.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var authManager: AuthManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.initializeAuthManager(requireContext())
        
        setupUI()
        observeUserProfile()
    }
    
    private fun setupUI() {
        binding.btnSignOut.setOnClickListener {
            profileViewModel.signOut()
        }
        
        binding.btnSignIn.setOnClickListener {
            // Navigate to sign-in activity
            val intent = android.content.Intent(requireContext(), yawza.zawya.activity.SimpleAuthActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun observeUserProfile() {
        profileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // User is signed in - show profile info and sign out button
                binding.tvUserName.text = user.displayName ?: "User"
                binding.tvUserEmail.text = user.email ?: ""
                binding.btnSignOut.visibility = android.view.View.VISIBLE
                binding.btnSignIn.visibility = android.view.View.GONE
                
                // Load profile picture if available
                user.photoUrl?.let { photoUrl ->
                    // You can use Glide or Picasso to load the image
                    // For now, we'll just show a placeholder
                }
            } else {
                // User is not signed in - show sign in prompt and hide sign out button
                binding.tvUserName.text = "Not signed in"
                binding.tvUserEmail.text = "Please sign in to view your profile"
                binding.btnSignOut.visibility = android.view.View.GONE
                binding.btnSignIn.visibility = android.view.View.VISIBLE
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
