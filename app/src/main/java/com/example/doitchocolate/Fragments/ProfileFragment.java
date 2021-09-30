package com.example.doitchocolate.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doitchocolate.Activities.LoginActivity;
import com.example.doitchocolate.Activities.RegisterActivity;
import com.example.doitchocolate.Helpers.prefHelper;
import com.example.doitchocolate.Models.User;
import com.example.doitchocolate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import es.dmoral.toasty.Toasty;

public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Context mContext;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    private EditText edtUsername, edtFullname, edtPassword, edtEmail, edtPhone;
    private Button btnLogout, btnSave;
    private ImageView imgProfile;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mContext = getActivity();

        edtUsername = (EditText) view.findViewById(R.id.edt_username);
        edtFullname = (EditText) view.findViewById(R.id.edt_nama);
        edtPassword = (EditText) view.findViewById(R.id.edt_password);
        edtEmail = (EditText) view.findViewById(R.id.edt_email);
        edtPhone = (EditText) view.findViewById(R.id.edt_phone);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
        btnSave = (Button) view.findViewById(R.id.btnSimpan);

        prefHelper.init(mContext);

        edtUsername.setText(prefHelper.getUserdata("username"));
        edtFullname.setText(prefHelper.getUserdata("fullname"));
        edtPassword.setText(prefHelper.getUserdata("password"));
        edtEmail.setText(prefHelper.getUserdata("email"));
        edtPhone.setText(prefHelper.getUserdata("phone"));
        Glide.with(mContext)
                .load(prefHelper.getUserdata("profile_picture"))
                .into(imgProfile);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefHelper.flushCart();
                prefHelper.flushSession();
                Toasty.success(mContext, "Log out success!", Toast.LENGTH_SHORT, true).show();
                Intent i = new Intent(mContext, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(i);
                getActivity().finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String fullname = edtFullname.getText().toString().trim();

                if(username.length() > 0 && password.length() > 0 && phone.length() > 0 && email.length() > 0 && fullname.length() > 0) {
                    final ProgressDialog dialog = ProgressDialog.show(mContext,"",
                            "Loading. Please wait...", true);

                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setNo_tlp(phone);
                    user.setEmail(email);
                    user.setNama(fullname);

                    db.collection("users")
                            .document(prefHelper.getUserdata("id"))
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Log.d("[DEBUG]", "DocumentSnapshot successfully written!");
                                    Toasty.success(mContext, "Update success!", Toast.LENGTH_SHORT, true).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Log.w("[DEBUG]", "Error writing document", e);
                                    Toasty.error(mContext, "Update failed!", Toast.LENGTH_SHORT, true).show();
                                }
                            });
                }
            }
        });

        return view;
    }
}
