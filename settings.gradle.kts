rootProject.name = "stat"

val prefix: String = rootProject.name

include("$prefix-api")
include("$prefix-core")

val sample = "$prefix-sample"
include(sample)
file(sample)
    .listFiles()
    ?.filter {
        it.isDirectory
    }?.forEach { file ->
        include(":$sample:${file.name}")
    }

include("$prefix-plugin")
include("$prefix-publish")
