package com.darklycoder.paging3demo.data

import androidx.paging.PagingSource

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