package com.example.apptaller2.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.apptaller2.R
import com.example.apptaller2.SupabaseClient
import com.example.apptaller2.data.UsuarioRepository
import com.example.apptaller2.ui.auth.LoginActivity
import com.example.apptaller2.ui.main.admin.AdminFragment
import com.example.apptaller2.ui.main.admin.UsuariosFragment
import com.example.apptaller2.ui.main.perfil.PerfilFragment
import com.example.apptaller2.ui.main.productos.CarritoFragment
import com.example.apptaller2.ui.main.productos.CatalogoFragment
import com.example.apptaller2.ui.main.productos.FavoritosFragment
import com.example.apptaller2.ui.main.productos.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
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

        configurarMenuPorRol(navView.menu)


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

    private fun configurarMenuPorRol(menu: Menu){
        lifecycleScope.launch {
            val rol = UsuarioRepository.obtenerRolActual()
            android.util.Log.d("DEBUG_ROL", "Rol obtenido: $rol")

            runOnUiThread {
                when (rol) {
                    "admin" ->{
                        //admin ve todo
                        menu.findItem(R.id.nav_admin).isVisible = true
                        menu.findItem(R.id.nav_usuarios).isVisible = true
                    }
                    "vendedor" ->{
                        //vendedor ve catalogo y carrito
                        menu.findItem(R.id.nav_admin).isVisible = true
                        menu.findItem(R.id.nav_usuarios).isVisible = false
                    }
                    else -> {
                        menu.findItem(R.id.nav_admin).isVisible = false
                        menu.findItem(R.id.nav_usuarios).isVisible = false
                    }
                }
            }

        }
    }

    private fun cargarFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun cerrarSesion() {
        lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
                runOnUiThread {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finishAffinity()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}