package com.pablodiste.sowingoapp.ui.products

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
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

/**
 * Screen showing the list of products and filters for them
 */
private const val TAG = "ProductsFragment"
@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products), ProductsAdapter.OnItemClickListener,
    SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private val viewModel: ProductsViewModel by viewModels()
    private lateinit var searchView: SearchView

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
                    it.data?.let { products ->
                        if (products.isNotEmpty()) {
                            productsAdapter.submitList(products)
                            binding.rvProducts.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                        } else {
                            binding.rvProducts.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                        }
                    }
                    binding.swipeContainer.isRefreshing = false
                }
                is Resource.Error -> {
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error: $message")
                    }
                    binding.swipeContainer.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.swipeContainer.isRefreshing = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.products_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_products)
        searchView.setOnQueryTextListener(this)
        searchView.isIconified = false
        searchView.isIconifiedByDefault = true
        searchView.setOnCloseListener {
            searchView.setQuery("", false)
            return@setOnCloseListener true
        }

        searchItem.setOnActionExpandListener(this)

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
        // TODO: Item click, this might lead to a product details screen
    }

    override fun onFavoriteCheckedChange(product: Product, isFavorite: Boolean) {
        viewModel.setFavorite(product, isFavorite)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.clearFocus()
        viewModel.textFilter = query.orEmpty()
        viewModel.refresh()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.textFilter = newText.orEmpty()
        viewModel.refresh()
        return true
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        searchView.requestFocus()
        viewModel.textFilter = searchView.query.toString()
        viewModel.refresh()
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        viewModel.textFilter = ""
        viewModel.refresh()
        searchView.clearFocus()
        return true
    }
}