package com.example.user1.jsonvolley;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User 1 on 30/12/2017.
 */

public class HeroAdapter extends ArrayAdapter {

    private RequestQueue requestQueue;
    JsonObjectRequest jsArrayRequest;
    private static final String URL_BASE = "http://jsonvolley.webcindario.com";
    private static final String URL_JSON = "/datos/heroes.json";
    private static final String TAG = "HeroAdapter";
    List<Hero> items;

    public HeroAdapter(@NonNull Context context) {
        super(context, 0);

        requestQueue = Volley.newRequestQueue(context);

        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_BASE + URL_JSON,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        items = parseJson(response);
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(jsArrayRequest);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView;

        listItemView=  null == convertView ? layoutInflater.inflate(
                R.layout.hero,
                parent,
                false
        ): convertView;

        Hero item = items.get(position);

        TextView textoTitulo = (TextView) listItemView.findViewById(R.id.tv_titulo);
        TextView textoDescripcion = (TextView) listItemView.findViewById(R.id.tv_descripcion);
        final ImageView imageHero = (ImageView) listItemView.findViewById(R.id.imageHero);

        textoTitulo.setText(item.getTitulo());
        textoDescripcion.setText(item.getDescripcion());

        ImageRequest request = new ImageRequest(
                URL_BASE + item.getImagen(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageHero.setImageBitmap(response);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imageHero.setImageResource(R.drawable.ic_launcher_background);
                        Log.d(TAG, "Error en respuesta Bitmap: "+ error.getMessage());
                    }
                }
        );

        requestQueue.add(request);
        return listItemView;
    }

    public List<Hero> parseJson(JSONObject jsonObject) {
        List<Hero> heroes = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0 ; i< jsonArray.length(); i++){
                try {
                    JSONObject objeto = jsonArray.getJSONObject(i);

                    Hero hero = new Hero(
                            objeto.getString("titulo"),
                            objeto.getString("descripcion"),
                            objeto.getString("imagen")
                    );

                    heroes.add(hero);
                }catch (JSONException e){
                    Log.e(TAG, "Error de parsing: "+ e.getMessage());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return heroes;
    }
}
