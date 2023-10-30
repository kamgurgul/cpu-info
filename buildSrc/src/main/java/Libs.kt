object Libs {

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.12.0"
        const val activityKtx = "androidx.activity:activity-compose:1.8.0"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.6.1"
        const val appCompat = "androidx.appcompat:appcompat:1.6.1"
        const val preference = "androidx.preference:preference:1.2.1"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val multiDex = "androidx.multidex:multidex:2.0.1"
        const val viewPager2 = "androidx.viewpager2:viewpager2:1.0.0"
        const val datastorePreferences = "androidx.datastore:datastore-preferences:1.0.0"

        object Test {
            private const val version = "1.5.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:1.5.2"
            const val rules = "androidx.test:rules:$version"

            const val archCoreTesting = "androidx.arch.core:core-testing:2.2.0"
            const val jUnitExt = "androidx.test.ext:junit:1.1.5"
            const val orchestrator = "androidx.test:orchestrator:1.4.2"

            object Espresso {
                private const val version = "3.5.1"
                const val core = "androidx.test.espresso:espresso-core:$version"
                const val contrib = "androidx.test.espresso:espresso-contrib:$version"
            }
        }
    }
}