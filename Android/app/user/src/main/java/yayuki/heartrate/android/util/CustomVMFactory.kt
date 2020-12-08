package yayuki.heartrate.android.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class CustomVMFactory private constructor(
    private val creator: Creator
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(creator.cls))
            return creator.createViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    abstract class Creator(val cls: Class<*>) {
        abstract fun createViewModel(): ViewModel
        fun getFactory(): CustomVMFactory {
            return CustomVMFactory(this)
        }
    }
}