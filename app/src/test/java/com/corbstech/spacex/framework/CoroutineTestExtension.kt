package com.corbstech.spacex.framework

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor

@ExperimentalCoroutinesApi
class CoroutineTestExtension : TestInstancePostProcessor, AfterEachCallback {

    private val testScope = TestScope()

    override fun postProcessTestInstance(
        testInstance: Any?,
        context: ExtensionContext?
    ) {
        (testInstance as? CoroutineTest)?.let { it.testScope = testScope }
    }

    override fun afterEach(context: ExtensionContext?) {
        testScope.cancel()
    }
}