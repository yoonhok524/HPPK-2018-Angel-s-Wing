package com.youknow.hppk2018.angelswing.ui.statistics

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
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

    val parts = mutableMapOf<Float, String>()

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun getProducts() {
        parts.clear()
        disposable.add(productDataSource.getProducts()
                .map {
                    val entries = mutableListOf<BarEntry>()
                    var count = 0f
                    it.groupBy { it.seller.part }
                            .forEach {
                                parts[count++] = it.value[0].seller.part
                                entries.add(BarEntry(parts.values.indexOf(it.value[0].seller.part).toFloat(), it.value.size.toFloat()))
                                info("[HPPK] getProdcts - ${it.key}: ${it.value.size}")
                            }

                    val barDataSet = BarDataSet(entries, "HPPK")
                    BarData(barDataSet)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onChartLoaded(it, IAxisValueFormatter { value, _ ->
                        parts[value]
                    })
                }, {

                }))
    }

}