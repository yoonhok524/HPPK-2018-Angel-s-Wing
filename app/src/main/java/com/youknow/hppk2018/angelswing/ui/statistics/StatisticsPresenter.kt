package com.youknow.hppk2018.angelswing.ui.statistics

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class StatisticsPresenter(
        private val view: StatisticsContract.View,
        private val productDataSource: ProductDataSource = ProductDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
) : StatisticsContract.Presenter, AnkoLogger {

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun getChartData() {
        disposable.add(productDataSource.getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getSalesKing(it)
                    getDonationKing(it)
                }, {

                }))


        val parts = mutableMapOf<Float, String>()
//        disposable.add(productDataSource.getProducts()
//                .map {
//                    val entries = mutableListOf<BarEntry>()
//
//                    it.groupBy { it.seller.part }
//                            .forEach {
//                                info("[HPPK] getProdcts - ${it.key}: ${it.value.size}")
//                            }
//
//                    val top3 = it.groupBy { it.seller.part }
//                            .map { Pair(it.key, it.value.size) }
//                            .sortedByDescending{ it.second }
//                            .take(3)
//                            .reversed()
//
//                    for (i in top3.indices) {
//                        parts[i.toFloat()] = top3[i].first
//                        entries.add(BarEntry(i.toFloat(), top3[i].second.toFloat()))
//                    }
//
//                    val barDataSet = BarDataSet(entries, "HPPK")
//                    barDataSet.valueTextSize = 10f
//                    BarData(barDataSet)
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    view.onChartLoaded(it, IAxisValueFormatter { value, _ ->
//                        parts[value]
//                    })
//                }, {
//
//                }))
    }

    private fun getSalesKing(products: List<Product>) {
        view.onSalesKingLoaded("")
    }

    private fun getDonationKing(products: List<Product>) {
        view.onDonationKingLoaded("")
    }

}