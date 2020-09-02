package com.darklycoder.paging3demo

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.darklycoder.paging3demo.data.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val vm by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = UserAdapter()
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter

        lifecycleScope.launch {
            vm.flow.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launch {
            // 监听加载状态
            adapter.loadStateFlow.collectLatest {
                Log.d("test", it.refresh.toString())
            }
        }
    }

}