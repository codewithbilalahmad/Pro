package com.muhammad.pro.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer

@Composable
fun <T : Any> rememberMutableStateListOf(vararg elements : T) : SnapshotStateList<Any>{
    return rememberSaveable(saver = snapshotStateListSaver(serializableListSaver())){
        elements.toList().toMutableStateList()
    }
}

inline fun  <reified T : Any> serializableListSaver(
    serializer : KSerializer<T> = UnsafePolymorphicSerializer()
) = listSaver(
    save = {list ->
        list.map{
            encodeToSavedState(serializer,it)
        }
    }, restore = {list ->
        list.map { decodeFromSavedState(serializer, it) }
    }
)

fun <T> snapshotStateListSaver(
    listSaver : Saver<List<T>, out Any> = autoSaver()
) : Saver<SnapshotStateList<T>, Any> = with(listSaver as Saver<List<T>, Any>){
    Saver(
        save = {state ->
            save(state.toList().toMutableList())
        }, restore = {state ->
            restore(state)?.toMutableStateList()
        }
    )
}

class UnsafePolymorphicSerializer<T : Any> : KSerializer<T> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("PolymorphicData") {
            element(elementName = "type", serialDescriptor<String>())
            element(elementName = "payload", buildClassSerialDescriptor("Any"))
        }

    @OptIn(InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: T) {
        return encoder.encodeStructure(descriptor){
            val className = value::class.java.canonicalName!!
            encodeStringElement(descriptor, index = 0, className)
            val serializer = value::class.serializer() as KSerializer<T>
            encodeSerializableElement(descriptor, index = 1, serializer, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @OptIn(InternalSerializationApi::class)
    override fun deserialize(decoder: Decoder): T {
        return decoder.decodeStructure(descriptor) {
            val className =
                decodeStringElement(descriptor = descriptor, decodeElementIndex(descriptor))
            val classRef = Class.forName(className).kotlin
            val serializer = classRef.serializer()
            decodeSerializableElement(
                descriptor = descriptor,
                decodeElementIndex(descriptor),
                serializer
            ) as T
        }
    }

}