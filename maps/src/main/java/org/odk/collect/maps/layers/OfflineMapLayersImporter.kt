package org.odk.collect.maps.layers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.odk.collect.async.Scheduler
import org.odk.collect.maps.databinding.OfflineMapLayersImporterBinding
import org.odk.collect.material.MaterialFullScreenDialogFragment
import org.odk.collect.material.MaterialProgressDialogFragment

class OfflineMapLayersImporter(
    private val scheduler: Scheduler,
    private val sharedLayersDirPath: String,
    private val projectLayersDirPath: String
) : MaterialFullScreenDialogFragment() {

    private val viewModel: OfflineMapLayersImporterViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OfflineMapLayersImporterViewModel(scheduler, requireContext().contentResolver) as T
            }
        }
    }

    private lateinit var binding: OfflineMapLayersImporterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OfflineMapLayersImporterBinding.inflate(inflater)

        viewModel.init(requireArguments().getStringArrayList(URIS))

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.addLayerButton.setOnClickListener {
            val layersDir = if (binding.allProjectsOption.isChecked) {
                sharedLayersDirPath
            } else {
                projectLayersDirPath
            }

            viewModel.addLayers(layersDir)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MaterialProgressDialogFragment.showOn(
            this,
            viewModel.isLoading,
            childFragmentManager
        ) {
            MaterialProgressDialogFragment().also { dialog ->
                dialog.message = getString(org.odk.collect.strings.R.string.loading)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.addLayerButton.isEnabled = !isLoading
        }

        viewModel.data.observe(this) { data ->
            val adapter = OfflineMapLayersImporterAdapter(data)
            binding.layers.setAdapter(adapter)
        }

        viewModel.isAddingNewLayersFinished.observe(this) { isAddingNewLayersFinished ->
            if (isAddingNewLayersFinished) {
                setFragmentResult(RESULT_KEY, bundleOf())
                dismiss()
            }
        }
    }

    override fun onCloseClicked() {
    }

    override fun onBackPressed() {
        dismiss()
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    companion object {
        const val URIS = "uris"
        const val RESULT_KEY = "layersAdded"
    }
}
