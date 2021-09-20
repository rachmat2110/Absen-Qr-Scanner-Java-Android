package com.example.absen.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.absen.Model.DaftarMahasiswaModel;
import com.example.absen.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeDosenAdapter extends RecyclerView.Adapter<HomeDosenAdapter.ViewHolder> {

    public static String keterangan = "";
    public static String nip_dosen = "";
    public static String kelas_spinner = "";
    public static String matkul_spinner = "";

    FirebaseFirestore firebaseFirestore;
    private List< DaftarMahasiswaModel > daftarMahasiswaModels;
    private HomeDosenAdapter.ItemAdapterCallback itemAdapterCallback;
    String currentUserId;

    public HomeDosenAdapter(String currentUserId){
        this.currentUserId = currentUserId;
        daftarMahasiswaModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_daftar_mahasiswa, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        DaftarMahasiswaModel model = daftarMahasiswaModels.get(position);

        holder.nama.setText(model.getNama_mahasiswa());
        holder.npm.setText(model.getNpm_mahasiswa());

        if (keterangan.equals("load")){
            holder.jumlah.setText("");
        }else if (keterangan.equals("proses")){
            firebaseFirestore = FirebaseFirestore.getInstance();

            firebaseFirestore.collection("Mahasiswa").document(kelas_spinner).collection("Mahasiswa "+kelas_spinner)
                    .document(model.getNpm_mahasiswa()).collection(nip_dosen).get().addOnCompleteListener(new OnCompleteListener< QuerySnapshot >() {
                @Override
                public void onComplete(@NonNull Task< QuerySnapshot > task) {
                    Integer jumlah = task.getResult().size();

                    holder.jumlah.setText(""+jumlah);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return daftarMahasiswaModels.size();
    }

    public void setData(List<DaftarMahasiswaModel> daftarMahasiswaModels) {
        this.daftarMahasiswaModels = daftarMahasiswaModels;
        notifyDataSetChanged();
    }

    public interface ItemAdapterCallback {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nama;
        private TextView npm;
        private TextView jumlah;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.nama_mhs);
            npm = itemView.findViewById(R.id.npm_mahasiswa_);
            jumlah = itemView.findViewById(R.id.jumlah_hadir_);
        }
    }
}
