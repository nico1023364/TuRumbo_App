package com.example.apptaller2.ui.main.productos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apptaller2.R


class HomeFragment : Fragment() {

    private val listaProductos = listOf(
        Product("Camisa Casial", 10.99, R.drawable.camisa_casual),
        Product("Camisa Polo", 12.99, R.drawable.camisa_casual),
        Product("Pantalon Jenas", 120.99, R.drawable.camisa_casual),
        Product("Zapatos Deportivos", 200.28, R.drawable.camisa_casual),
        Product("Camisa Casial", 10.99, R.drawable.camisa_casual),
        Product("Camisa Polo", 12.99, R.drawable.camisa_casual),
        Product("Pantalon Jenas", 120.99, R.drawable.camisa_casual),
        Product("Zapatos Deportivos", 200.28, R.drawable.camisa_casual),
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_productos)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = ProductoAdapter(listaProductos)
        return view

    }

}