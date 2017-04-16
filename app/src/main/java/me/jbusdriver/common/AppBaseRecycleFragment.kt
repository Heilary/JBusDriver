package me.jbusdriver.common

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cfzx.mvp.view.BaseView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.Flowable
import jbusdriver.me.jbusdriver.R
import me.jbusdriver.mvp.presenter.BasePresenter

/**
 * Created by Administrator on 2017/4/9.
 */
abstract class AppBaseRecycleFragment<P : BasePresenter.BaseRefreshLoadMorePresenter<V>, V : BaseView.BaseListWithRefreshView, M> : AppBaseFragment<P, V>(), BaseView.BaseListWithRefreshView {

    abstract val swipeView: SwipeRefreshLayout?
    abstract val recycleView: RecyclerView
    abstract val layoutManager: RecyclerView.LayoutManager
    abstract val adapter: BaseQuickAdapter<M, in BaseViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun initWidget(rootView: View) {
        recycleView.layoutManager = layoutManager
        adapter.setOnLoadMoreListener({ mBasePresenter?.onLoadMore() }, recycleView)
        swipeView?.setColorSchemeResources(R.color.colorAccent)
        swipeView?.setOnRefreshListener { mBasePresenter?.onRefresh() }
        recycleView.adapter = adapter
    }

    override fun showLoading() {
        KLog.d("showLoading")
        swipeView?.let {
            if (!it.isRefreshing) {
                it.post {
                    it.setProgressViewOffset(false, 0, viewContext.dpToPx(24f))
                    it.isRefreshing = true
                }
            }
        } ?: super.showLoading()

        adapter.removeAllFooterView()
    }

    override fun dismissLoading() {
        KLog.d("dismissLoading")
        swipeView?.let {
            it.post { it.isRefreshing = false }
        } ?: super.dismissLoading()
    }

    override fun showContents(datas: List<*>?) {
        adapter.addData(datas as MutableList<M>)
    }

    override fun loadMoreComplete() {
        adapter.loadMoreComplete()
    }

    override fun loadMoreEnd() {
        adapter.loadMoreEnd()
    }

    override fun loadMoreFail() {
        adapter.loadMoreFail()
    }

    override fun enableRefresh(bool: Boolean) {
        swipeView?.isEnabled = bool
    }

    override fun enableLoadMore(bool: Boolean) {
        adapter.setEnableLoadMore(bool)
    }

    override fun getRequestParams(page: Int): Flowable<String> = Flowable.empty()


    override fun resetList() {
        adapter.getData().clear()
        adapter.removeAllHeaderView()
        adapter.removeAllFooterView()
        adapter.loadMoreComplete()
        adapter.notifyDataSetChanged()
    }

    override fun showError(e: Throwable?) {

    }
}