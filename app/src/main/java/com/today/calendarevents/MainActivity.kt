package com.today.calendarevents

import com.today.calendarevents.base.BaseActivity
import com.today.calendarevents.databinding.ActivityMainBinding


class MainActivity  : BaseActivity<ActivityMainBinding, MainViewModel>(){

    override fun getViewModelResId(): Int = BR.mainActivityVM

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getLayoutResId(): Int = R.layout.activity_main

}
