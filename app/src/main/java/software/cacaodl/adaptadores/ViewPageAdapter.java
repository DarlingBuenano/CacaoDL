package software.cacaodl.adaptadores;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import software.cacaodl.R;

public class ViewPageAdapter extends RecyclerView.Adapter<ViewPageAdapter.Pager2ViewHolder> {
    ArrayList<Integer> imagenesArrayId;
    ArrayList<String> txtTitulos;
    ArrayList<String> txtDescripciones;
    Context contexto;

    public ViewPageAdapter(
            ArrayList<Integer> imagen_array_id,
            ArrayList<String> txt_titulos,
            ArrayList<String> txt_descripciones) {
        this.imagenesArrayId = imagen_array_id;
        this.txtTitulos = txt_titulos;
        this.txtDescripciones = txt_descripciones;
    }

    class Pager2ViewHolder extends RecyclerView.ViewHolder{
        ImageView imagen;
        Button indicador01;
        Button indicador02;
        TextView titulo;
        TextView descripcion;
        Button btn_terminar_guia;
        public Pager2ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.img_guia_pag_svg);
            indicador01 = itemView.findViewById(R.id.indicador_guia_pag01);
            indicador02 = itemView.findViewById(R.id.indicador_guia_pag02);
            titulo = itemView.findViewById(R.id.txt_guia_pag_titulo);
            descripcion = itemView.findViewById(R.id.txt_guia_pag_descripcion);
            btn_terminar_guia = itemView.findViewById(R.id.btn_terminar_guia);
        }
    }

    @NonNull
    @Override
    public ViewPageAdapter.Pager2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        contexto = parent.getContext();
        return new Pager2ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.guia_view_page, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewPageAdapter.Pager2ViewHolder holder, int position) {
        holder.imagen.setImageResource(imagenesArrayId.get(position));
        if (position == 0) {
            holder.indicador01.setBackgroundTintList(contexto.getResources().getColorStateList(R.color.naranja, contexto.getTheme()));
            holder.indicador02.setBackgroundTintList(contexto.getResources().getColorStateList(R.color.blanco, contexto.getTheme()));
        } else {
            holder.indicador01.setBackgroundTintList(contexto.getResources().getColorStateList(R.color.blanco, contexto.getTheme()));
            holder.indicador02.setBackgroundTintList(contexto.getResources().getColorStateList(R.color.naranja, contexto.getTheme()));
            holder.btn_terminar_guia.setVisibility(View.VISIBLE);
        }
        holder.titulo.setText(txtTitulos.get(position));
        holder.descripcion.setText(txtDescripciones.get(position));
    }

    @Override
    public int getItemCount() {
        return this.txtTitulos.size();
    }
}
