package fr.o80.codingprogress.data

import com.intellij.util.xmlb.annotations.OptionTag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class ProgressConfigs(
    @OptionTag(converter = ProgressConfigsConverter::class)
    var paths: MutableList<ProgressConfig>? = null
)

@Serializable
data class ProgressConfig(
    @SerialName("path")
    val path: String?,
    @SerialName("imagePath")
    val imagePath: String?,
    @SerialName("colors")
    val colors: String?
)
