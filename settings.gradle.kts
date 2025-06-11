pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Pro"
include(":app")
include(":feature")
include(":core")
include(":core:theme")
include(":core:network")
include(":core:util")
include(":core:testing")
include(":feature:camera")
include(":feature:creation")
include(":feature:home")
include(":feature:results")
include(":data")
include(":benchmark")
