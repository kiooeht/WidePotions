package com.evacipated.cardcrawl.mod.widepotions.extensions

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

private val fieldMap: MutableMap<Pair<Class<*>, String>, Field> = mutableMapOf()
private val methodMap: MutableMap<MethodMapKey, Method> = mutableMapOf()
typealias MethodMapKey = Triple<Class<*>, String, Array<out Class<*>>>

class RMethod internal constructor(
    private val instance: Any?,
    clazz: Class<*>,
    methodName: String,
    argTypes: Array<out Class<*>>
) {
    private val descriptor: MethodMapKey = Triple(clazz, methodName, argTypes)

    fun <R> invoke(vararg args: Any?): R {
        @Suppress("UNCHECKED_CAST")
        return getSavedMethod(this.descriptor).invoke(this.instance, *args) as R
    }
}

private fun getSavedField(clazz: Class<*>, fieldName: String): Field {
    val pair = Pair(clazz, fieldName)
    return fieldMap[pair] ?: run {
        clazz.getDeclaredField(fieldName).also {
            it.isAccessible = true
            fieldMap[pair] = it
        }
    }
}

private fun getSavedMethod(descriptor: MethodMapKey): Method {
    return methodMap[descriptor] ?: run {
        descriptor.first.getDeclaredMethod(descriptor.second, *descriptor.third).also {
            it.isAccessible = true
            methodMap[descriptor] = it
        }
    }
}

// Field access helpers
fun <T> Any.getPrivate(
    fieldName: String,
    clazz: Class<*> = this::class.java
): T {
    @Suppress("UNCHECKED_CAST")
    return getSavedField(clazz, fieldName).get(this) as T
}

fun <T> Any.setPrivate(
    fieldName: String,
    value: T,
    clazz: Class<*> = this::class.java
) {
    getSavedField(clazz, fieldName).set(this, value)
}

fun <T> Any.setPrivateFinal(
    fieldName: String,
    value: T,
    clazz: Class<*> = this::class.java
) {
    val targetField = getSavedField(clazz, fieldName)
    getSavedField(Field::class.java, "modifiers").set(targetField, targetField.modifiers and Modifier.FINAL.inv())
    targetField.set(this, value)
}

fun <T> KClass<*>.getPrivate(fieldName: String): T {
    return this.java.getPrivate(fieldName)
}

fun <T> KClass<*>.setPrivate(fieldName: String, value: T) {
    this.java.setPrivate(fieldName, value)
}

fun <T> Class<*>.getPrivate(fieldName: String): T {
    @Suppress("UNCHECKED_CAST")
    return getSavedField(this, fieldName).get(null) as T
}

fun <T> Class<*>.setPrivate(fieldName: String, value: T) {
    getSavedField(this, fieldName).set(null, value)
}

// Method call helpers
fun Any.privateMethod(
    methodName: String,
    vararg argTypes: KClass<*>,
    clazz: KClass<*> = this::class
): RMethod {
    return this.privateMethod(methodName, *argTypes.map { it.java }.toTypedArray(), clazz = clazz.java)
}

fun Any.privateMethod(
    methodName: String,
    vararg argTypes: Class<*>,
    clazz: Class<*> = this::class.java
): RMethod {
    return RMethod(this, clazz, methodName, argTypes)
}

fun KClass<*>.privateMethod(
    methodName: String,
    vararg argTypes: KClass<*>
): RMethod {
    return this.java.privateMethod(methodName, *argTypes.map { it.java }.toTypedArray())
}

fun Class<*>.privateMethod(
    methodName: String,
    vararg argTypes: Class<*>
): RMethod {
    return RMethod(null, this, methodName, argTypes)
}
