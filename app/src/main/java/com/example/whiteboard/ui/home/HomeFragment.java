package com.example.whiteboard.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.res.TypedArrayUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.whiteboard.MainActivity;
import com.example.whiteboard.R;
import com.google.android.material.textfield.TextInputLayout;

import org.jivesoftware.smackx.xdata.FormField;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        TextInputLayout ip = root.findViewById(R.id.textInputLayout2_fh);
        Button save = root.findViewById(R.id.button_fh);
        CheckBox subscribe = root.findViewById(R.id.checkBox);
        RadioGroup users = root.findViewById(R.id.users_radiogroup);
        users.check(R.id.radioButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.currentUser = ((RadioButton)root.findViewById(users.getCheckedRadioButtonId())).getText().toString();

                if(ip.getEditText().getText() == null || ip.getEditText().getText().toString().equals("")) return;
                MainActivity.hostIP = ip.getEditText().getText().toString();

                if(subscribe.isChecked())
                    MainActivity.subscribe = true;
                else
                    MainActivity.subscribe = false;

                int x =7;
            }
        });

        return root;
    }
}