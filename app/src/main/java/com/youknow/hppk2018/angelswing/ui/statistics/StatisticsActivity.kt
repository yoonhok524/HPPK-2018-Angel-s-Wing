package com.youknow.hppk2018.angelswing.ui.statistics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
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
        mPresenter.getChartData()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun onChartLoaded(barData: BarData?, formatter: IAxisValueFormatter) {
        if (barData != null) {
            chartMostSales.data = barData

            val xAxis = chartMostSales.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = formatter
            xAxis.textSize = 12f
            xAxis.labelCount = barData.entryCount
            xAxis.setDrawGridLines(false)
//            xAxis.setDrawAxisLine(false)

            chartMostSales.axisLeft.labelCount
            chartMostSales.axisRight.isEnabled = false
//            hBarChart.axisLeft.isEnabled = false

            chartMostSales.setExtraOffsets(72f, 0f, 0f, 0f)


            val yAxis = chartMostSales.axisLeft

            // Minimum section is 0.25, could be 1.0f for only integer values
            var labelInterval = 1f

            var sections: Int
            do {
                labelInterval *= 2f
                sections = Math.ceil((chartMostSales.getYMax() / labelInterval).toDouble()).toInt()
            } while (sections > 6)

            // If the ymax lies on one of the top interval, add another one for some spacing
            if (chartMostSales.getYMax() === sections * labelInterval) {
                sections++
            }

            yAxis.setAxisMaxValue(labelInterval * sections)
            yAxis.setLabelCount(sections + 1, true)

            chartMostSales.description.isEnabled = false


            // Disable touch
            chartMostSales.setTouchEnabled(false)
            chartMostSales.setScaleEnabled(false)
            chartMostSales.setPinchZoom(false)
            chartMostSales.isDragEnabled = false
            chartMostSales.isScaleXEnabled = false
            chartMostSales.isScaleYEnabled = false
            chartMostSales.invalidate()
        }
    }

    override fun onDonationKingLoaded(s: String) {
        chartMostDonate.setNoDataText(getString(R.string.msg_will_be_open_event_day))
        chartMostDonate.invalidate()
    }

    override fun onSalesKingLoaded(s: String) {
        chartMostSales.setNoDataText(getString(R.string.msg_will_be_open_event_day))
        chartMostSales.invalidate()
    }

}