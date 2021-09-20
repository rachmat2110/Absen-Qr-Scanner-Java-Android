package com.example.absen.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.absen.Model.RekapAbsenMahasiswaModel;
import com.example.absen.Model.RekapAbsenModel;
import com.example.absen.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RekapAbsenMahasiswaAdapter extends RecyclerView.Adapter<RekapAbsenMahasiswaAdapter.ViewHolder> {

    List< RekapAbsenMahasiswaModel > rekapAbsenMahasiswaModels;
    private RekapAbsenMahasiswaAdapter.ItemAdapterCallback itemAdapterCallback;
    String currentUserId;
    FirebaseFirestore firebaseFirestore;

    public static String kelas_mahasiswa = "";
    public static String npm_mahasiswa = "";

    public RekapAbsenMahasiswaAdapter(String currentUserId){
        this.currentUserId = currentUserId;
        rekapAbsenMahasiswaModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rekab_absen_mahasiswa, parent, false);
        final RekapAbsenMahasiswaAdapter.ViewHolder viewHolder = new RekapAbsenMahasiswaAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final RekapAbsenMahasiswaModel model = rekapAbsenMahasiswaModels.get(position);

        firebaseFirestore = FirebaseFirestore.getInstance();

        holder.nama.setText(model.getNama_dosen());
        holder.matkul.setText(model.getMatkul());
        Picasso.get().load(model.getFoto_dosen()).into(holder.foto);

        firebaseFirestore.collection("Mahasiswa").document(kelas_mahasiswa).collection("Mahasiswa "+kelas_mahasiswa)
                .document(npm_mahasiswa).collection(model.getNip_dosen()).get().addOnCompleteListener(new OnCompleteListener< QuerySnapshot >() {
            @Override
            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                Integer jumlah = task.getResult().size();

                holder.jumlah.setText(""+jumlah);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        view.getContext(), R.style.BottomSheetDialogTheme
                );
                String foto_dosen = model.getFoto_dosen();
                String nama_dosen = model.getNama_dosen();
                String nip_dosen = model.getNip_dosen();
                final View view1 = LayoutInflater.from(view.getContext()).inflate(R.layout.bottom_sheet, (LinearLayout)view.findViewById(R.id.linearLayout));

                firebaseFirestore.collection("Mahasiswa").document(kelas_mahasiswa).collection("Mahasiswa "+kelas_mahasiswa)
                        .document(npm_mahasiswa).collection(nip_dosen).orderBy("tanggal", Query.Direction.ASCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener< QuerySnapshot >() {
                            @Override
                            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                                if (task.isSuccessful()){
                                    ArrayList<String> ListTanggal = new ArrayList<String>();
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                        String tanggal = (String) documentSnapshot.getData().get("tanggal");
                                        ListTanggal.add(tanggal);
                                    }

                                    TextView tanggal = view1.findViewById(R.id.tanggal_rekap);
                                    tanggal.setText(ListTanggal.toString().replace(",","\n\n"));
                                }
                            }
                        });

                TextView nm_dosen = view1.findViewById(R.id.nama_dosen);
                ImageView ft_dosen = view1.findViewById(R.id.foto_dosen);

                nm_dosen.setText(nama_dosen);
                Picasso.get().load(foto_dosen).into(ft_dosen);

                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return rekapAbsenMahasiswaModels.size();
    }

    public void setData(List< RekapAbsenMahasiswaModel > rekapAbsenMahasiswaModels){
        this.rekapAbsenMahasiswaModels = rekapAbsenMahasiswaModels;
        notifyDataSetChanged();
    }

    public interface ItemAdapterCallback {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nama;
        private TextView matkul;
        private TextView jumlah;
        private ImageView foto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.nama_dosen);
            matkul = itemView.findViewById(R.id.matkul);
            jumlah = itemView.findViewById(R.id.jumlah);
            foto = itemView.findViewById(R.id.foto_dosen);
        }
    }
}
