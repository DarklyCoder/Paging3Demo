# Jetpack分页加载库-Paging

## 配置

```gralde
implementation "androidx.paging:paging-runtime:3.0.0-alpha05"
```

## 基本使用

1. 自定义数据源

    自定义实现`PagingSource`:

    ```kotlin
    class UserPagingSource : PagingSource<Int, User>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
            try {
                val page = params.key ?: 1
                // 获取分页数据
                val response = fetchData(page, params.loadSize)

                // 获取正确结果
                return LoadResult.Page(
                    data = response,
                    prevKey = if (page == 1) null else page,
                    nextKey = page + 1
                )

            } catch (e: Exception) {
                // 处理错误
                return LoadResult.Error(e)
            }
        }

        private fun fetchData(page: Int, size: Int): List<User> {
            val start = (page - 1) * size
            val end = start + size - 1
            val users = ArrayList<User>()
            for (i in start..end) {
                users.add(User(i, "name$i"))
            }

            Thread.sleep(500)

            return users
        }

    }
    ```

2. 连接数据源

    在`ViewModel`通过`Pager`连接数据源

    ```kotlin
    private val config = PagingConfig(
        pageSize = 10,
        prefetchDistance = 2,
        initialLoadSize = 10
    )

    val flow = Pager(config = config) { UserPagingSource() }.flow.cachedIn(viewModelScope)
    ```

3. 创建Adapter

    继承`PagingDataAdapter<T,VH>`实现adapter:

    ```kotlin
    class UserAdapter : PagingDataAdapter<User, UserAdapter.VH>(DIFF_CALLBACK) {

        companion object {
            val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
                override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.uid == newItem.uid
                override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
            }
        }

        override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(parent)

        class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        ) {

            private val tvName = itemView.findViewById<TextView>(R.id.tv_name)

            fun bind(item: User?) {
                tvName.text = item?.name
            }
        }

    }
    ```

4. 展示数据

    ```kotlin
     private val vm by viewModels<UserViewModel>()
     ......
     // 在Activities下可以直接使用`lifecycleScope`, 但在`Fragments`里使用`viewLifecycleOwner.lifecycleScope`
     lifecycleScope.launch {
         vm.flow.collectLatest { adapter.submitData(it) }
     }
    ```

如上即可完成正常的分页显示。

## 进阶使用

### 显示加载状态

框架对外暴露了分页数据的加载状态，通过`LoadState`对象展示当前状态：

* LoadState.NotLoading: 没有发生操作和错误
* LoadState.Loading: 加载操作中 
* LoadState.Error: 发生了错误

1. 通过添加listener监听状态：

    通过 `PagingDataAdapter` 的 `addLoadStateListener`方法监听

    ```kotlin
     lifecycleScope.launch {
        // 监听加载状态
        adapter.loadStateFlow.collectLatest {
            Log.d("test", it.refresh.toString())
        }
     }
    ```
2. 通过adapter展示加载状态

    ```kotlin
    class LoadStateViewHolder(parent: ViewGroup, retry: () -> Unit) : RecyclerView.ViewHolder(
      LayoutInflater.from(parent.context) .inflate(R.layout.load_state_item, parent, false)
    ) {
      private val binding = LoadStateItemBinding.bind(itemView)
      private val progressBar: ProgressBar = binding.progressBar
      private val errorMsg: TextView = binding.errorMsg
      private val retry: Button = binding.retryButton
        .also {
          it.setOnClickListener { retry() }
        }

      fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
          errorMsg.text = loadState.error.localizedMessage
        }

        progressBar.isVisible = loadState is LoadState.Loading
        retry.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error
      }
    }

    // 继承LoadStateAdapter
    class ExampleLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadStateViewHolder()> {

      override fun onCreateViewHolder(parent: ViewGroup,loadState: LoadState ) = LoadStateViewHolder(parent, retry)

      override fun onBindViewHolder(holder: LoadStateViewHolder,loadState: LoadState) = holder.bind(loadState)
    }
    ```

    ```kotlin
    // 使用adapter替换recycleview绑定的adapter
    // 也可使用`withLoadStateHeader()` / `withLoadStateFooter()` 显示header/footer
    val adapter = pagingAdapter
      .withLoadStateHeaderAndFooter(
        header = ExampleLoadStateAdapter(adapter::retry),
        footer = ExampleLoadStateAdapter(adapter::retry)
      )
    ```

### RemoteMediator

TODO

## 参考

1. [Paging3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
2. [PagingConfig](https://developer.android.google.cn/reference/kotlin/androidx/paging/PagingConfig)