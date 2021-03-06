package com.joan.librohechizos.utiles;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.joan.librohechizos.R;
import com.joan.librohechizos.modelo.Clase;
import com.joan.librohechizos.sqlite.Contratos;
import com.joan.librohechizos.sqlite.OperacionesBD;
import com.joan.librohechizos.ui.LibroDeHechizos;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Joan on 14/03/2017.
 */

public class FiltroHechizo {
    private Button btnAplicar, btnCerrar, btnLimpiar;
    private CheckBox evocacion, conjuracion, abjuracion, encantamiento, ilusion, transmutacion, necromancia,
            adivinacion, ritual, concentracion, verbal, somatica, material;
    private Spinner clase, nivel;
    private View popView;
    private PopupWindow popupWindow;
    private OperacionesBD datos;
    LibroDeHechizos context;


    public FiltroHechizo(LibroDeHechizos context) {
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popView = layoutInflater.inflate(R.layout.filtro, null);
        popupWindow = new PopupWindow(popView, RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        //popupWindow.setHeight(100);
        //popupWindow.setWidth(WRAP_CONTENT);
        datos = OperacionesBD.obtenerInstancia(context);
        this.btnAplicar = (Button) popView.findViewById(R.id.btn_filtro_aplicar);
        this.btnCerrar = (Button) popView.findViewById(R.id.btn_cancelar_filtros);
        this.btnLimpiar = (Button) popView.findViewById(R.id.btn_filtros_limpiar);
        this.evocacion = (CheckBox) popView.findViewById(R.id.ck_filtro_evocacion);
        this.conjuracion = (CheckBox) popView.findViewById(R.id.ck_filtro_conjuracion);
        this.abjuracion = (CheckBox) popView.findViewById(R.id.ck_filtro_abjuracion);
        this.encantamiento = (CheckBox) popView.findViewById(R.id.ck_filtro_encantamiento);
        this.ilusion = (CheckBox) popView.findViewById(R.id.ck_filtro_ilucion);
        this.transmutacion = (CheckBox) popView.findViewById(R.id.ck_filtro_transmutacion);
        this.necromancia = (CheckBox) popView.findViewById(R.id.ck_filtro_necromancia);
        this.adivinacion = (CheckBox) popView.findViewById(R.id.ck_filtro_adivinacion);
        this.ritual = (CheckBox) popView.findViewById(R.id.ck_filtro_ritual);
        this.concentracion = (CheckBox) popView.findViewById(R.id.ck_filtro_concentracion);
        this.verbal = (CheckBox) popView.findViewById(R.id.ck_filtro_verbal);
        this.somatica = (CheckBox) popView.findViewById(R.id.ck_filtro_somatico);
        this.material = (CheckBox) popView.findViewById(R.id.ck_filtro_material);
        this.clase = (Spinner) popView.findViewById(R.id.spn_filtro_clase);
        this.nivel = (Spinner) popView.findViewById(R.id.spn_filtro_nivel);

        //se definen funcionalidades de los 3 botones
        funcionalidadBotonAplicarFiltro();
        funcionalidadBotonCerrarFiltro();
        funcionalidadBotonLimpiarFiltro();

        //se llenan los spiners
        clase.setAdapter(cargarClases());
        nivel.setAdapter(cargarNiveles());

    }

    private void funcionalidadBotonLimpiarFiltro() {
        btnLimpiar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.setClickFiltro(false);
                context.setFiltro("");
                popupWindow.dismiss();
            }
        });
    }


    private void funcionalidadBotonCerrarFiltro() {
        //este medod define lo que hace el boton cerrar de la ventanita de filtros
        btnCerrar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.setClickFiltro(false);
                popupWindow.dismiss();
            }
        });
    }

    private void funcionalidadBotonAplicarFiltro() {
        btnAplicar.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String seleccion = obtenerSeleccionYArgumentos();
                context.setClickFiltro(false);
                popupWindow.dismiss();
                context.setFiltro(seleccion);
            }
        });
    }

    private String obtenerSeleccionYArgumentos() {
        //el primer elementos es la seleccion
        String seleccion;
        seleccion = "";
        ArrayList<String> argumentos = new ArrayList<>();
        Clase clase = (Clase) this.clase.getSelectedItem();
        String nivel = (String) this.nivel.getSelectedItem();
        if (!nivel.equals("Todos los niveles")) {
            seleccion = Contratos.Hechizos.NIVEL + "=";
            switch (nivel) {
                case ("Truco"):
                    seleccion = seleccion + "0";
                    break;
                case ("nivel 1"):
                    seleccion = seleccion + "1";
                    break;
                case ("nivel 2"):
                    seleccion = seleccion + "2";
                    break;
                case ("nivel 3"):
                    seleccion = seleccion + "3";
                    break;
                case ("nivel 4"):
                    seleccion = seleccion + "4";
                    break;
                case ("nivel 5"):
                    seleccion = seleccion + "5";
                    break;
                case ("nivel 6"):
                    seleccion = seleccion + "6";
                    break;
                case ("nivel 7"):
                    seleccion = seleccion + "7";
                    break;
                case ("nivel 8"):
                    seleccion = seleccion + "8";
                    break;
                case ("nivel 9"):
                    seleccion = seleccion + "9";
                    break;
            }
        }
        if (!clase.getNombre().equals("Todas Las Clases")) {
            if (!seleccion.equals("")) {
                seleccion = seleccion + " AND ";
            }
            seleccion = seleccion + "clase." + Contratos.Clases.ID_CLASE + "=" + clase.getIdClase();
        }
        String seleccionEscuelas = "";
        if (comprobarTodosLosChecksBoxs()) {
            if (escuelaCheked()) {
                if (!seleccion.equals("")) {
                    seleccion = seleccion + " AND ";
                }
                seleccionEscuelas = "(";
                if (this.conjuracion.isChecked()) {
                    seleccionEscuelas = seleccionEscuelas + Contratos.Hechizos.ESCUELA + "=1";
                }
                if (this.evocacion.isChecked()) {
                    if (!seleccionEscuelas.equals("(")) {
                        seleccionEscuelas = seleccionEscuelas + " OR ";
                    }
                    seleccionEscuelas = seleccionEscuelas + Contratos.Hechizos.ESCUELA + "=2";
                }
                if (this.abjuracion.isChecked()) {
                    if (!seleccionEscuelas.equals("(")) {
                        seleccionEscuelas = seleccionEscuelas + " OR ";
                    }
                    seleccionEscuelas = seleccionEscuelas + Contratos.Hechizos.ESCUELA + "=3";
                }
                if (this.ilusion.isChecked()) {
                    if (!seleccionEscuelas.equals("(")) {
                        seleccionEscuelas = seleccionEscuelas + " OR ";
                    }
                    seleccionEscuelas = seleccionEscuelas + Contratos.Hechizos.ESCUELA + "=4";
                }
                if (this.encantamiento.isChecked()) {
                    if (!seleccionEscuelas.equals("(")) {
                        seleccionEscuelas = seleccionEscuelas + " OR ";
                    }
                    seleccionEscuelas = seleccionEscuelas + Contratos.Hechizos.ESCUELA + "=5";
                }
                if (this.necromancia.isChecked()) {
                    if (!seleccionEscuelas.equals("(")) {
                        seleccionEscuelas = seleccionEscuelas + " OR ";
                    }
                    seleccionEscuelas = seleccionEscuelas + Contratos.Hechizos.ESCUELA + "=6";
                }
                if (this.transmutacion.isChecked()) {
                    if (!seleccionEscuelas.equals("(")) {
                        seleccionEscuelas = seleccionEscuelas + " OR ";
                    }
                    seleccionEscuelas = seleccionEscuelas + Contratos.Hechizos.ESCUELA + "=7";
                }
                if (this.adivinacion.isChecked()) {
                    if (!seleccionEscuelas.equals("(")) {
                        seleccionEscuelas = seleccionEscuelas + " OR ";
                    }
                    seleccionEscuelas = seleccionEscuelas + Contratos.Hechizos.ESCUELA + "=8";
                }
                seleccionEscuelas = seleccionEscuelas + ") ";
                seleccion = seleccion + seleccionEscuelas;
            }
            if (this.verbal.isChecked()) {
                if (!seleccion.equals("")) {
                    seleccion = seleccion + " AND ";
                }
                seleccion = seleccion + Contratos.Hechizos.COMPONENTE_VERBAL + "=1";
                argumentos.add("1");
            }
            if (this.somatica.isChecked()) {
                if (!seleccion.equals("")) {
                    seleccion = seleccion + " AND ";
                }
                seleccion = seleccion + Contratos.Hechizos.COMPONENTE_SOMATICO + "=1";
            }
            if (this.material.isChecked()) {
                if (!seleccion.equals("")) {
                    seleccion = seleccion + " AND ";
                }
                seleccion = seleccion + Contratos.Hechizos.COMPONENTE_MATERIAL + "=1";
            }
            if (this.ritual.isChecked()) {
                if (!seleccion.equals("")) {
                    seleccion = seleccion + " AND ";
                }
                seleccion = seleccion + Contratos.Hechizos.RITUAL + "=1";
            }
            if (this.concentracion.isChecked()) {
                if (!seleccion.equals("")) {
                    seleccion = seleccion + " AND ";
                }
                seleccion = seleccion + Contratos.Hechizos.CONCENTRACION + "=1";
            }
        }
        return seleccion;
    }

    private boolean escuelaCheked() {
        return this.abjuracion.isChecked() || this.adivinacion.isChecked() || this.conjuracion.isChecked() ||
                this.encantamiento.isChecked() || this.evocacion.isChecked() || this.ilusion.isChecked() ||
                this.transmutacion.isChecked() || this.necromancia.isChecked();
    }


    private boolean comprobarTodosLosChecksBoxs() {
        return this.abjuracion.isChecked() || this.adivinacion.isChecked() ||
                this.concentracion.isChecked() || this.conjuracion.isChecked() || this.encantamiento.isChecked()
                || this.evocacion.isChecked() || this.ilusion.isChecked() || this.material.isChecked() ||
                this.necromancia.isChecked() || this.ritual.isChecked() || this.somatica.isChecked() ||
                this.transmutacion.isChecked() || this.verbal.isChecked();
    }


    private ArrayAdapter cargarNiveles() {
        String[] niveles = {"Todos los niveles", "Truco", "nivel 1", "nivel 2", "nivel 3", "nivel 4", "nivel 5", "nivel 6",
                "nivel 7", "nivel 8", "nivel 9"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, niveles);
        return adapter;
    }

    private ArrayAdapter cargarClases() {
        ArrayList<Clase> clases = new ArrayList<>();
        clases.add(new Clase("", "Todas Las Clases"));
        Cursor listaClases;
        try {
            datos.getDb().beginTransaction();
            listaClases = datos.obtenerClases();
            while (listaClases.moveToNext()) {
                clases.add(new Clase(listaClases.getString(0), listaClases.getString(1)));
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        ArrayAdapter<Clase> adtSpnClases = new ArrayAdapter<Clase>(context, R.layout.support_simple_spinner_dropdown_item, clases);
        return adtSpnClases;
    }


    public Spinner getClase() {
        return clase;
    }


    public PopupWindow getPopupWindow() {
        return popupWindow;
    }
}
