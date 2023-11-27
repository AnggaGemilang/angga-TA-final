package com.agrapana.fertigation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.agrapana.fertigation.R
import com.agrapana.fertigation.model.OnboardingData

class OnboardingAdapter(private var context: Context, private var onBoardingDataList : List<OnboardingData>) : PagerAdapter() {

    override fun getCount(): Int {
        return onBoardingDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(context).inflate(R.layout.template_onboarding, null)

        val imageView: ImageView = view.findViewById(R.id.image)
        val title: TextView = view.findViewById(R.id.title)
        val desc: TextView = view.findViewById(R.id.desc);

        imageView.setImageResource(onBoardingDataList[position].ImageUrl)
        imageView.layoutParams.width = (onBoardingDataList[position].width*2.7).toInt()
        imageView.layoutParams.height = (onBoardingDataList[position].height*2.7).toInt()
        (imageView.layoutParams as ConstraintLayout.LayoutParams).apply {
            marginStart=onBoardingDataList[position].marginStart
            topMargin=onBoardingDataList[position].marginTop
            rightMargin=onBoardingDataList[position].marginRight
        }

        title.text = onBoardingDataList[position].title
        (title.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin=onBoardingDataList[position].marginBottom
        }

        desc.text = onBoardingDataList[position].desc

        container.addView(view)
        return view
    }
}