package com.wyman.utillibrary.selector



/**
 * Created by wyman
 *   on 2019-09-13.
 *   集合选择器工具类
 */

//返回一个空
fun <T> nothing() : Iterable<T>.() -> T? = {
    null
}

/**
 * 获取想要的那个值
 * */
fun <T> single(preference : T) : Iterable<T>.() -> T? = {
    //find 为 Iterable 迭代器的内置函数
    find { it == preference}
}

/**
 * 该泛型是继承 Comparable的
 * 再通过迭代器获取最大值
 * 返回值为 T?
 * */
fun <T : Comparable<T>> highest(): Iterable<T>.() -> T? = Iterable<T>::max

fun <T : Comparable<T>> lowest() : Iterable<T>.() -> T? = Iterable<T>::min

/**
 * 最终返回Output
 * */
fun <Input,Output> firstAvailable(
        //可变数量参数（vararg）
        vararg functions : Input.() -> Output?
): Input.() -> Output? = {
    functions.findNonNull {
        it(this)
    }
}

/**
 * 返回不为空的值
 * */
fun <T : Any,R> Array<T>.findNonNull(selector : (T) -> R?) : R?{
    //因为将接收者 Array传了入来所以 forEach()能调用
    forEach {
        //selector(it)? 意思为：返回的 R如果为null则不调用let
        selector(it)?.let{
            return it
        }
    }
    return null
}

/**
 * @param selector 原始的选择器方法
 * @param predicate 在他传递给选择器之前检查是否符合条件
 * @return 返回一个符合的选择方法
 * */
fun <T : Any> filtered(
    selector : Iterable<T>.() -> T?,//传入一个迭代器
    predicate:(T) -> Boolean//传入一个方法类型
) : Iterable<T>.() -> T? = {
    selector(filter(predicate = predicate))
}





















