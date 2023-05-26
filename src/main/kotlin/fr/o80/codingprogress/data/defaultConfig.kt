package fr.o80.codingprogress.data

/**
 * Configuration used while it has not been changed.
 */
fun defaultConfig() = ProgressConfigs(
    mutableListOf(
        ProgressConfig(
            path = "*",
            imagePath = "husky",
            colors = "#eb4023,#f8ca51,#fffd5d,#61fa4c,#90f6f5,#2610f0,#551d84"
        )
    )
)
