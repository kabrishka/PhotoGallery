package com.kabrishka.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kabrishka.photogallery.model.GalleryItem

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        photoGalleryViewModel = ViewModelProvider(this)[PhotoGalleryViewModel::class.java]
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            photoHolder.bindDrawable(drawable)
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        photoGalleryViewModel.galleryItemsLiveData.observe(viewLifecycleOwner) { galleryItems ->
            photoRecyclerView.adapter = PhotosAdapter(galleryItems)
        }

        setupToolbar(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    private fun setupToolbar(view: View) {
        with(view.findViewById<Toolbar>(R.id.toolbar)) {
            inflateMenu(R.menu.fragment_photo_gallery)

            val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
            val searchView = searchItem.actionView as SearchView
            searchView.apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.d(TAG, "QueryTextSubmit: $query")
                        query?.let {
                            photoGalleryViewModel.fetchPhotos(it)
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.d(TAG, "QueryTextChange: $newText")
                        return false
                    }

                })

                setOnSearchClickListener {
                    searchView.setQuery(photoGalleryViewModel.searchTerm, false)
                }
            }

            val clearItem: MenuItem = menu.findItem(R.id.menu_item_clear)
            clearItem.setOnMenuItemClickListener {
                photoGalleryViewModel.fetchPhotos("")
                true
            }
        }
    }

    private class PhotoHolder(private val itemImageView: ImageView) :
        RecyclerView.ViewHolder(itemImageView) {

        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
    }

    private inner class PhotosAdapter(private val galleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(
                R.layout.list_item_gallery,
                parent,
                false
            ) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            val placeholder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_loading
            ) ?: ColorDrawable()
            holder.bindDrawable(placeholder)
            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }

    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}