package com.youknow.hppk2018.angelswing.ui.statistics

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IAxisValueFormatter

interface StatisticsContract {
    interface View {
        fun onChartLoaded(barData: BarData?, param: IAxisValueFormatter)

    }

    interface Presenter {
        fun unsubscribe()
        fun getProducts()

    }
}