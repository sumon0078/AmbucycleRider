package com.arnab.ambucyclerider.Utils;


//import com.arnab.ambucyclelast.Common;
//import com.arnab.ambucyclelast.Model.TokenModel;
import com.arnab.ambucyclerider.Common.Common;
import com.arnab.ambucyclerider.Model.DriverGeoModel;
import com.arnab.ambucyclerider.Model.EventBus.SelectePlaceEvent;
import com.arnab.ambucyclerider.Model.FCMSendData;
import com.arnab.ambucyclerider.Model.TokenModel;
import com.arnab.ambucyclerider.R;
import com.arnab.ambucyclerider.Remote.IFCMService;
import com.arnab.ambucyclerider.Remote.RetrofitFCMClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/*import com.example.uberdriver.Common;
import com.example.uberdriver.Model.EventBus.NotifyToRiderEvent;
import com.example.uberdriver.Model.FCMSendData;
import com.example.uberdriver.Model.TokenModel;
import com.example.uberdriver.R;
import com.example.uberdriver.Remote.IFCMService;
import com.example.uberdriver.Remote.RetrofitFCMClient;
import com.example.uberdriver.Services.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;*/



    public class UserUtils {
        public static void updateUser(View view, Map<String, Object> updateData) {
            FirebaseDatabase.getInstance()
                    .getReference(Common.RIDER_INFO_REFERENCE)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(updateData)
                    .addOnFailureListener(e -> Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_SHORT).show())
                    .addOnSuccessListener(aVoid -> Snackbar.make(view, "Update infomation successfully!", Snackbar.LENGTH_SHORT).show());
        }

        public static void updateToken(Context context, String token) {
            TokenModel tokenModel = new TokenModel(token);

            FirebaseDatabase.getInstance()
                    .getReference(Common.TOKEN_REFERENCE)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(tokenModel)
                    .addOnFailureListener(e -> {

                    }).addOnSuccessListener(aVoid -> {

            });
        }


        public static void sendRequestToDriver(Context context, RelativeLayout main_layout, DriverGeoModel foundDriver, SelectePlaceEvent selectePlaceEvent) {

            CompositeDisposable compositeDisposable = new CompositeDisposable();
            IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

            //Get token
            FirebaseDatabase
                    .getInstance()
                    .getReference(Common.TOKEN_REFERENCE)
                    .child(foundDriver.getKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                TokenModel tokenModel = dataSnapshot.getValue(TokenModel.class);

                                Map<String, String> notificationData = new HashMap<>();
                                notificationData.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_TITLE);
                                notificationData.put(Common.NOTI_CONTENT, "This message respersent for request driver action");
                                notificationData.put(Common.RIDER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());


                                notificationData.put(Common.RIDER_PICKUP_LOCATION_STRING,selectePlaceEvent.getOriginString());
                                notificationData.put(Common.RIDER_PICKUP_LOCATION, new StringBuilder("")
                                        .append(selectePlaceEvent.getOrigin().latitude)
                                        .append(",")
                                        .append(selectePlaceEvent.getOrigin().longitude)
                                        .toString());

                                notificationData.put(Common.RIDER_DESTINATION_STRING,selectePlaceEvent.getAddress());
                                notificationData.put(Common.RIDER_DESTINATION, new StringBuilder("")
                                        .append(selectePlaceEvent.getDestination().latitude)
                                        .append(",")
                                        .append(selectePlaceEvent.getDestination().longitude)
                                        .toString());


                                FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                                compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(fcmResponse -> {
                                            if (fcmResponse.getSuccess() ==0)
                                            {
                                                compositeDisposable.clear();
                                                Snackbar.make(main_layout,context.getString(R.string.request_driver_failed),Snackbar.LENGTH_LONG).show();
                                            }

                                        }, throwable -> {
                                            compositeDisposable.clear();
                                            Snackbar.make(main_layout,throwable.getMessage(),Snackbar.LENGTH_LONG).show();
                                        }));
                            }
                            else
                            {
                                Snackbar.make(main_layout,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Snackbar.make(main_layout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
    }




