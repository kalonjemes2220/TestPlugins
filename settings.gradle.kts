pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    plugins {
        id("com.android.library") version "8.7.3"
        id("org.jetbrains.kotlin.android") version "2.1.20"
    }
}

rootProject.name = "TestPlugins"

// ExampleProvider কে বাদ দেওয়া হলো কারণ এটি এরর দিচ্ছিল
// include(":ExampleProvider") 

include(":MovieBoxWebProvider")
