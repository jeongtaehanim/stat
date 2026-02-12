rootProject.name = "stat"

val prefix: String? = rootProject.name

include("$prefix-api")
include("$prefix-core")

//val dongle = "$prefix-dongle"
//include(dongle)
//file(dongle)
//    .listFiles()
//    ?.filter {
//        it.isDirectory && it.name.startsWith("v")
//    }?.forEach { file ->
//        include(":$dongle:${file.name}")
//    }


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
