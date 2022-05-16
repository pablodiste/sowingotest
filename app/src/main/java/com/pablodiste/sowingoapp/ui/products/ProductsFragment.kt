package com.pablodiste.sowingoapp.ui.products

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.pablodiste.sowingoapp.R
import com.pablodiste.sowingoapp.adapter.ProductsAdapter
import com.pablodiste.sowingoapp.data.model.Product
import com.pablodiste.sowingoapp.databinding.FragmentProductsBinding
import com.pablodiste.sowingoapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_products.*


private const val TAG = "ProductsFragment"
@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products), ProductsAdapter.OnItemClickListener {

    private val viewModel: ProductsViewModel by viewModels()
    var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProductsBinding.bind(view)
        val productsAdapter = ProductsAdapter(this)

        binding.apply {
            rvProducts.apply {
                adapter = productsAdapter
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                setHasFixedSize(true)
            }
        }

        viewModel.products.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Success -> {
                    progressBar.visibility = View.INVISIBLE
                    isLoading = false
                    it.data?.let { productsResponse ->
                        productsAdapter.submitList(productsResponse.hits)
                    }
                }
                is Resource.Error -> {
                    progressBar.visibility = View.INVISIBLE
                    isLoading = true
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error: $message")
                    }
                }
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onItemClick(product: Product) {
        // TODO: Item Click
    }
}