package com.example.absen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Grafik_Mahasiswa_Dosen extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;

    public static String kelas_scan = "";
    public static String matkul_scan = "";
    public static String minggu_ke = "";
    public static String nip_dosen = "";

    TextView kelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik__mahasiswa__dosen);

        firebaseFirestore = FirebaseFirestore.getInstance();

        kelas = findViewById(R.id.kelas);
        kelas.setText("Kelas dipilih : \n"+kelas_scan);

        getChart();
    }

    private void getChart() {
        firebaseFirestore.collection("Dosen").document(nip_dosen).collection("Kelas dan Matkul")
                .document(kelas_scan+", "+matkul_scan).collection("line_chart").orderBy("tanggal", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener< QuerySnapshot >() {
            @Override
            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                if (task.isSuccessful()){
                    ArrayList<String> lisTanggal = new ArrayList<String>();
                    ArrayList<Number> lisJumlah = new ArrayList<Number>();

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String tanggal = (String) documentSnapshot.getData().get("tanggal");
                        lisTanggal.add(tanggal);
                    }

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        Number jumlah = (Number) documentSnapshot.getData().get("jumlah");
                        lisJumlah.add(jumlah);
                    }

                    AnyChartView anyChartView = findViewById(R.id.any_chart_view);

                    Cartesian cartesian = AnyChart.line();
                    cartesian.animation(true);
                    cartesian.padding(10d, 20d, 5d, 20d);

                    cartesian.crosshair().enabled(true);
                    cartesian.crosshair()
                            .yLabel(true)
                            // TODO ystroke
                            .yStroke((Stroke) null, null, null, (String) null, (String) null);

                    cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                    cartesian.title("Grafik Mingguan Absen Mahasiswa");
                    cartesian.yAxis(0).title("Bilangan angka");
                    cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
                    Double[] jumlah = {6.5,2.5,4.2,1.5,10.1,11.50,12.89,78.9,76.90,87.90};

                    Log.d("TAG", "Jumlah >="+jumlah);

                    List< DataEntry > seriesData = new ArrayList<>();
                    for (int i = 0; i < lisTanggal.size(); i++){
                        seriesData.add(new ValueDataEntry(lisTanggal.get(i), lisJumlah.get(i)));
                    }

                    Set set = Set.instantiate();
                    set.data(seriesData);

                    Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");


                    Line series2 = cartesian.line(series2Mapping);
                    series2.name("Jumlah");
                    series2.hovered().markers().enabled(true);
                    series2.hovered().markers()
                            .type(MarkerType.CIRCLE)
                            .size(4d);
                    series2.tooltip()
                            .position("right")
                            .anchor(Anchor.CENTER)
                            .offsetX(5d)
                            .offsetY(5d);

                    //leftcenter
                    cartesian.legend().enabled(true);
                    cartesian.legend().fontSize(10d);
                    cartesian.legend().padding(0d, 0d, 10d, 0d);

                    anyChartView.setChart(cartesian);
                }
            }
        });
    }

    public void back_home(View view) {
        startActivity(new Intent(getApplicationContext(), Home_Dosen.class));
        this.finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home_Dosen.class));
        super.onBackPressed();
    }

}
