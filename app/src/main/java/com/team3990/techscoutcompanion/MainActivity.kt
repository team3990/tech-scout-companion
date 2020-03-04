package com.team3990.techscoutcompanion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /** Properties */

    private lateinit var appBarConfiguration: AppBarConfiguration

    /** Activity's lifecycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Get an instance of the Nav Controller
        val navController = findNavController(R.id.navHostFragment)

        // Initialize the app bar configuration property
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Setup the Action Bar with the navigation controller
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /** Methods */

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
