package gov.smart.health.activity.vr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import gov.smart.health.R;
import gov.smart.health.activity.login.model.ResetPasswordModel;
import gov.smart.health.activity.vr.model.SportVideoListModel;
import gov.smart.health.activity.vr.model.VRSaveRecordModel;
import gov.smart.health.utils.SHConstants;
import gov.smart.health.utils.SharedPreferencesHelper;

public class SportShareActivity extends AppCompatActivity implements View.OnClickListener{

    private SportVideoListModel videoModel = new SportVideoListModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        View btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        sendData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT,"Hello");
                share.setType("text/plain");
                startActivity(share);
                break;
        }
    }

    private void sendData(){

        if(getIntent() != null && getIntent().getSerializableExtra(SHConstants.Video_ModelKey)!= null){
            videoModel = (SportVideoListModel) getIntent().getSerializableExtra(SHConstants.Video_ModelKey);
        }

        String pk = SharedPreferencesHelper.gettingString(SHConstants.LoginUserPkPerson,"");
        String name = SharedPreferencesHelper.gettingString(SHConstants.LoginUserPersonName,"");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SHConstants.Record_VRSport_Save_Pk_Person, pk);
            jsonObject.put(SHConstants.Record_VRSport_Save_Person_Name, name);

            jsonObject.put(SHConstants.Record_VRSport_Save_calorie, "555");
            jsonObject.put(SHConstants.Record_VRSport_Save_heart_rate, "100");
            jsonObject.put(SHConstants.Record_VRSport_Save_heart_rate_max, "128");

            jsonObject.put(SHConstants.Record_VRSport_Save_Pk_Place, videoModel.pk_folder);
            jsonObject.put(SHConstants.Record_VRSport_Save_Place_Name, "Place");

            jsonObject.put(SHConstants.Record_VRSport_Save_Pk_Video, videoModel.pk_video);
            jsonObject.put(SHConstants.Record_VRSport_Save_Video_Name, videoModel.video_name);

            jsonObject.put(SHConstants.Record_VRSport_Save_Record_Sport_Code, "1");
            jsonObject.put(SHConstants.Record_VRSport_Save_Record_Sport_name, "record_sport_name");

            jsonObject.put(SHConstants.Record_VRSport_Save_Time_End, "2017-10-25 09:09:09");
            jsonObject.put(SHConstants.Record_VRSport_Save_Time_Length, "300");
            jsonObject.put(SHConstants.Record_VRSport_Save_Time_Start, "2017-10-25 09:09:09");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(SHConstants.RecordVRSportSave)
                .addJSONObjectBody(jsonObject)
                .addHeaders(SHConstants.HeaderContentType, SHConstants.HeaderContentTypeValue)
                .addHeaders(SHConstants.HeaderAccept, SHConstants.HeaderContentTypeValue)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        VRSaveRecordModel model = gson.fromJson(response,VRSaveRecordModel.class);
                        if (model.success){
                            Toast.makeText(getApplication(),"保存成功",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplication(),"保存失败",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("","response error"+anError.getErrorDetail());
                        Toast.makeText(getApplication(),"保存失败",Toast.LENGTH_LONG).show();
                    }
                });
    }
}