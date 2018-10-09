package com.youknow.hppk2018.angelswing.ui.statistics

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IAxisValueFormatter

interface StatisticsContract {
    interface View {
        fun onChartLoaded(barData: BarData?, xAxisFormatter: IAxisValueFormatter)
        fun onDonationKingLoaded(s: String)
        fun onSalesKingLoaded(s: String)

    }

    interface Presenter {
        fun unsubscribe()
        fun getChartData()

    }
}