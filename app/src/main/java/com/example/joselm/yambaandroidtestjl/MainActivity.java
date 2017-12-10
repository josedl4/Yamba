// Autores:
// Martin Martin, Jose Luis
// Martinez Arias, Miguel
package com.example.joselm.yambaandroidtestjl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton floatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatButton = (FloatingActionButton) findViewById(R.id.floatbutton);
        floatButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, StatusActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Determina la accion a realizar dependiendo de la
     * opcion del menu seleccionada.
     *
     * @param item elemento del menu seleccionado
     * @return true si se ha realizado una accion, false en caso contrario
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_tweet:
                startActivity(new Intent(this, StatusActivity.class));
                return true;
            case R.id.itemServiceStart:
                startService(new Intent(this, RefreshService.class));
                return true;
            case R.id.itemServiceStop:
                stopService(new Intent(this, RefreshService.class));
                return true;
            case R.id.action_purge:
                int rows = getContentResolver().delete(StatusContract.CONTENT_URI, null, null);
                Toast.makeText(this, rows + " " + getString(R.string.rowsOfDataDeleted),
                        Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }
}
