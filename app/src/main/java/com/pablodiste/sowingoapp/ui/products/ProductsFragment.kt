package com.pablodiste.sowingoapp.ui.products

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.pablodiste.sowingoapp.R
import com.pablodiste.sowingoapp.adapter.ProductsAdapter
import com.pablodiste.sowingoapp.data.model.Product
import com.pablodiste.sowingoapp.databinding.FragmentProductsBinding
import com.pablodiste.sowingoapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "ProductsFragment"
@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products), ProductsAdapter.OnItemClickListener {

    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            swipeContainer.setOnRefreshListener {
                viewModel.getProducts()
            }
            swipeContainer.post { swipeContainer.isRefreshing = true }
        }

        viewModel.productsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { products -> productsAdapter.submitList(products) }
                }
                is Resource.Error -> {
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error: $message")
                    }
                }
                is Resource.Loading -> { }
            }
            binding.swipeContainer.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.products_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filterByFavorites -> {
                item.isChecked = !item.isChecked
                viewModel.showOnlyFavorites = item.isChecked
                viewModel.refresh()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(product: Product) {
        // TODO: Item Click
    }

    override fun onFavoriteCheckedChange(product: Product, isFavorite: Boolean) {
        viewModel.setFavorite(product, isFavorite)
    }
}