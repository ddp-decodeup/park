package com.parkloyalty.lpr.scan.pagination

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListPaginationUtils(recyclerView: RecyclerView, list: ArrayList<Any?>) {
    private var PAGE_INDEX_START_WITH = 1
    private val VISIBLE_THRESH_HOLD = 1
    private var PER_PAGE_COUNT = 25

    private var loading = false
    private var isCompleteLoading = false
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var mIndex = PAGE_INDEX_START_WITH
    private var totalPageCount = 1

    private var mRecyclerView: RecyclerView = recyclerView
    private var itemList: ArrayList<Any?> = list
    private var mListPaginationListener: ListPaginationListener? = null

    init {
        initListPaginationAdapter()
    }

    /**
     * Used to set pagination listener from the class
     */
    fun setOnListPaginationListener(onListPaginationListener: ListPaginationListener) {
        this.mListPaginationListener = onListPaginationListener
    }

    /**
     * Used to initialize pagination data for the first time
     */
    private fun initListPaginationAdapter() {
        if (mRecyclerView.layoutManager is LinearLayoutManager) {
            val linearLayoutManager = mRecyclerView.layoutManager as LinearLayoutManager
            mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    totalItemCount = linearLayoutManager.itemCount
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    if (mIndex <= totalPageCount && !isCompleteLoading && !loading && totalItemCount <= lastVisibleItem + VISIBLE_THRESH_HOLD) {
                        startPaginationLoading()
                    }
                }
            })
        }
    }

    /**
     * Used to make a force ful call to start API call & it will reflect into overridden method onDataLoading with current index
     */
    fun startPaginationLoading() {
        loading = true
        showLoadingView()
        if (mListPaginationListener != null)
            mListPaginationListener!!.onDataLoading(mIndex)

    }

    /**
     * Used to find current page index
     */
    fun getCurrentPageIndex(): Int {
        return mIndex
    }

    /**
     * Used to update page index forcefully
     */
    fun updatePageIndex(index: Int) {
        this.mIndex = index
    }

    /**
     * Used to set total number of page count, if we are getting that from API response
     */
    fun setTotalPageCount(totalPage: Int) {
        totalPageCount = totalPage
        removeLoadingView()

    }

    /**
     * Used to set total number of data count, if we are getting that from API response
     */
    fun setTotalDataCount(totalCount: Int) {
        totalPageCount = getPageCount(totalCount)
        removeLoadingView()
    }

    /**
     * General Utility function to convert total data count into page count in the set of per page count
     */
    private fun getPageCount(totalCount: Int): Int {
        return if (totalCount % PER_PAGE_COUNT == 0) {
            totalCount / PER_PAGE_COUNT
        } else {
            totalCount / PER_PAGE_COUNT + 1
        }
    }

    fun getPerPageCount(): Int {
        return PER_PAGE_COUNT
    }

    /**
     * Used to reset whole pagination to zero
     */
    fun resetPaginationLoading() {
        mIndex = PAGE_INDEX_START_WITH
        totalPageCount = 1
        loading = false
        isCompleteLoading = false
    }

    /**
     * Used to let pagination util knows that we had the API call success
     */
    fun completeAPICall() {
        val currentPage = if (PAGE_INDEX_START_WITH == 0) mIndex + 1 else mIndex
        isCompleteLoading = currentPage >= totalPageCount

        mIndex++
        loading = false
        if (mListPaginationListener != null)
            mListPaginationListener!!.onFinishDataLoading()
    }

    /**
     * Used to notify that we had error in between pagination
     */
    fun errorOccurred() {
        removeLoadingView()
        loading = false
        if (mListPaginationListener != null)
            mListPaginationListener!!.onFinishDataLoading()
    }

    /**
     * Used to notify that we have finished complete pagination
     */
    fun finishPagination() {
        mIndex = PAGE_INDEX_START_WITH
        loading = false
        isCompleteLoading = true
        if (mListPaginationListener != null)
            mListPaginationListener!!.onFinishDataLoading()
    }

    /**
     * Used to check that is pagination is finished or not
     */
    fun isLoadingFinished(): Boolean {
        return isCompleteLoading
    }

    /**
     * Used to check is anything is loading or not
     */
    fun isLoading() = loading

    /**
     * Used to add pagination loading view in the list
     */
    private fun showLoadingView() {
        if (itemList.size > 1) {
            itemList.add(null)
            mRecyclerView.adapter?.notifyItemInserted(itemList.size - 1)
        }
    }

    /**
     * Used to remove pagination loading view from the list
     */
    private fun removeLoadingView() {
        if (itemList.size > 1) {
            itemList.removeAt(itemList.size - 1)
            mRecyclerView.adapter?.notifyItemRemoved(itemList.size)
        }
    }

    interface ListPaginationListener {
        fun onDataLoading(index: Int)
        fun onFinishDataLoading()
    }
}