// Autores:
// Martin Martin, Jose Luis
// Martinez Arias, Miguel
package com.example.joselm.yambaandroidtestjl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity principal que lanza StatusFragment
 */
public class StatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Comprobar si la actividad ya ha sido creada con anterioridad
        if (savedInstanceState == null) {
            // Crear un fragment
            StatusFragment fragment = new StatusFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment,
                            fragment.getClass().getSimpleName())
                    .commit();
        }
    }

}

