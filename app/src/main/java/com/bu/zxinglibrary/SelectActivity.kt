package com.bu.zxinglibrary

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_select.*

/**
 *Function: SelectActivity
 *Author:@author BuJie
 *Date: 2018/9/10
 */
class SelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        btn_java.setOnClickListener {
            startActivity(Intent(this@SelectActivity, JavaMainActivity::class.java))
        }
        btn_kotlin.setOnClickListener {
            startActivity(Intent(this@SelectActivity, KotlinMainActivity::class.java))
        }
    }
}
