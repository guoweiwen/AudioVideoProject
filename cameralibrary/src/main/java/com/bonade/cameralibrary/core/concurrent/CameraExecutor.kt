package com.bonade.cameralibrary.core.concurrent

import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * 用于解决线程同步问题
 * 该类一次就一个线程执行
 * */
class CameraExecutor(
        private val executor : ExecutorService = Executors.newSingleThreadExecutor()
){
    //用于装载 执行任务
    private val cancellableTaskQueue = LinkedList<Future<*>>()

    /**
     * 执行 Operation对象的 任务即 ()->T
     * */
    fun<T> execute(operation : Operation<T>) : Future<T>{
        val future = executor.submit(Callable { //Callable 类似与Runnable
            operation.function()//执行实际操作
        })

        if(operation.cancellabel){
            cancellableTaskQueue += future
        }

        cleanUpCancelledTasks()

        return future
    }

    /**
     * 清除集合所有 Future
     * */
    private fun cleanUpCancelledTasks() {
        cancellableTaskQueue.removeAll {
            //it 为 Future
            !it.isPending
        }
    }

    /**
     * 取消所有在 队列的任务，没有取消标记的继续执行。
     * 在调用完此方法后， executor 对象仍然是有效状态能被使用
     * */
    fun cancelTasks(){
        //it 为 future
        cancellableTaskQueue
                .filter { it.isPending }//将正在执行的过滤出来
                .forEach {
                    //将正在执行的等其执行完操作停止
                    it.cancel(true)
                }
        //清除任务队列
        cancellableTaskQueue.clear()
    }

    //Pending 意思：是否执行完
    private val Future<*>.isPending
        get() = !isCancelled && !isDone

    /**
     * 类似于java bean 的封装数据类
     * */
    data class Operation<out T>(
        val cancellabel : Boolean = false,

        val function : ()-> T
    )
}


















