package com.example.absen.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.absen.Model.RekapAbsenModel;
import com.example.absen.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RekapAbsenAdapter extends RecyclerView.Adapter<RekapAbsenAdapter.ViewHolder> {

    private List< RekapAbsenModel > rekapAbsenModels;
    private RekapAbsenAdapter.ItemAdapterCallback itemAdapterCallback;
    String currentUserId;

    public RekapAbsenAdapter(String currentUserId){
        this.currentUserId = currentUserId;
        rekapAbsenModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rekap_absen, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        RekapAbsenModel model = rekapAbsenModels.get(position);

        holder.nama.setText(model.getNama_mahasiswa());
        holder.nomor.setText(model.getNpm_mahasiswa());
    }

    @Override
    public int getItemCount() {
        return rekapAbsenModels.size();
    }

    public void setData(List< RekapAbsenModel > rekapAbsenModels){
        this.rekapAbsenModels = rekapAbsenModels;
        notifyDataSetChanged();
    }

    public interface ItemAdapterCallback {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nama;
        private TextView nomor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.nama_mahasiswa);
            nomor = itemView.findViewById(R.id.nomor_mahasiswa);
        }
    }
}
