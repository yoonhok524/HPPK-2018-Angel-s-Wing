package com.youknow.hppk2018.angelswing.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.youknow.hppk2018.angelswing.R
import kotlinx.android.synthetic.main.activity_statistics.*


class StatisticsActivity : AppCompatActivity(), StatisticsContract.View {
    private lateinit var mPresenter: StatisticsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        mPresenter = StatisticsPresenter(this)
    }

    override fun onStart() {
        super.onStart()
        mPresenter.getProducts()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun onChartLoaded(barData: BarData?, formatter: IAxisValueFormatter) {
        if (barData != null) {
            hBarChart.data = barData


            val xAxis = hBarChart.xAxis
            xAxis.valueFormatter = formatter
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            
//            xAxis.granularity = 1f
//
//            xAxis.textSize = 12f
//            xAxis.setDrawAxisLine(true)
//            xAxis.setDrawGridLines(false)
//            xAxis.labelCount = barData.entryCount
//
//            hBarChart.axisRight.isEnabled = false
//            hBarChart.axisLeft.isEnabled = false

            hBarChart.invalidate()
        }
    }


}