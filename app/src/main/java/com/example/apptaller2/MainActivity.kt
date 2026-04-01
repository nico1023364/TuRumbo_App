package com.example.apptaller2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.apptaller2.ui.main.admin.AdminFragment
import com.example.apptaller2.ui.main.admin.UsuariosFragment
import com.example.apptaller2.ui.main.perfil.PerfilFragment
import com.example.apptaller2.ui.main.productos.CarritoFragment
import com.example.apptaller2.ui.main.productos.CatalogoFragment
import com.example.apptaller2.ui.main.productos.FavoritosFragment
import com.example.apptaller2.ui.main.productos.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navView = findViewById<NavigationView>(R.id.nav_menu)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
        this,
        drawerLayout,
        toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        cargarFragment(HomeFragment())
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> cargarFragment(HomeFragment())
                R.id.nav_catalogo -> cargarFragment(CatalogoFragment())
                R.id.nav_carrito -> cargarFragment(CarritoFragment())
                R.id.nav_perfil -> cargarFragment(PerfilFragment())
            }
            true
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_favoritos -> cargarFragment(FavoritosFragment())
                R.id.nav_admin -> cargarFragment(AdminFragment())
                R.id.nav_usuarios -> cargarFragment(UsuariosFragment())
            }
            drawerLayout.closeDrawers()
            true
        }



    }

    private fun cargarFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


}