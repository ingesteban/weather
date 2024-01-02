package dev.esteban.sportinggoods.ui.forecast

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.esteban.common.utils.ScreenState
import dev.esteban.sportinggoods.R
import dev.esteban.sportinggoods.databinding.FragmentForecastBinding
import dev.esteban.sportinggoods.ui.home.HomeError
import dev.esteban.sportinggoods.utils.findActivity

@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private val adapter: ForecastAdapter = ForecastAdapter()
    private lateinit var binding: FragmentForecastBinding
    private val forecastViewModel: ForecastViewModel by viewModels()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            forecastViewModel.getForecast(context = requireContext())
        } else {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (showRationale) {
                forecastViewModel.showErrorNoLocationProvided()
            } else {
                forecastViewModel.showErrorNoLocationProvidedAndDoNotAskAgain()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForecastBinding.inflate(inflater, container, false)
        setupView()
        initObservers()
        return binding.root
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            forecastViewModel.getForecast(context = requireContext())
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun setupView() {
        val recyclerView = binding.forecastRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.swipeRefresh.setOnRefreshListener {
            forecastViewModel.onRefresh(binding.root.context)
        }
    }

    private fun initObservers() {
        forecastViewModel.uiState.observe(viewLifecycleOwner) { response ->
            when (response.screenState) {
                is ScreenState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.forecastRecyclerView.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE

                }

                is ScreenState.Success -> {
                    response.forecastWeatherList?.let {
                        adapter.setItems(it)
                        binding.forecastRecyclerView.visibility = View.VISIBLE
                        binding.errorLayout.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                    }
                }

                is ScreenState.Error -> {
                    binding.forecastRecyclerView.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.swipeRefresh.isRefreshing = false

                    response.error?.let {
                        val errorResourceMessage = when (it) {
                            ForecastError.NO_LOCATION_PROVIDED -> R.string.error_no_location_provided
                            ForecastError.NO_LOCATION_PROVIDED_DO_NOT_ASK_AGAIN -> R.string.error_no_location_provided_do_not_ask_again
                            ForecastError.GENERIC -> R.string.error_generic
                        }
                        binding.errorLabel.text = requireContext().getString(errorResourceMessage)
                        if (it == ForecastError.NO_LOCATION_PROVIDED) {
                            binding.errorButton.visibility = View.VISIBLE
                            binding.errorButton.setOnClickListener {
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                            }
                        } else {
                            binding.errorButton.visibility = View.GONE
                        }
                    }
                }

                is ScreenState.None -> {
                    // TODO :
                }
            }
        }
        requestPermission()
    }
}
