package fr.o80.codingprogress.data

import com.intellij.util.xmlb.Converter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class ProgressConfigsConverter : Converter<MutableList<ProgressConfig>>() {

    private val json = Json {
        explicitNulls = true
        encodeDefaults = true
        prettyPrint = false
    }

    override fun toString(value: MutableList<ProgressConfig>): String {
        return json.encodeToString(value)
    }

    override fun fromString(value: String): MutableList<ProgressConfig>? {
        return json.decodeFromString(value)
    }
}
