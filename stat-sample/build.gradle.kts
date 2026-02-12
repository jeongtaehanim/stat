plugins {
    alias(libs.plugins.ksp) apply false
}

subprojects {
    apply(plugin = rootProject.libs.plugins.ksp.get().pluginId)

    dependencies {
        implementation(projectApi)

        compileOnly(rootProject.libs.autoServiceAnnotations)
        add("ksp", rootProject.libs.autoServiceKsp)
    }

    tasks.withType<Jar>().configureEach {
        archiveBaseName.set(project.name)
        archiveVersion.set("")
        archiveClassifier.set("")
    }
}
