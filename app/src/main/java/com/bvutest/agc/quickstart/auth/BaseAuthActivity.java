/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bvutest.agc.quickstart.auth;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.ProfileRequest;

public abstract class BaseAuthActivity extends AppCompatActivity {

    protected ImageView iconImageView;
    protected TextView nameTextView;
    protected Button loginButton;
    protected Button logoutButton;
    protected Button linkButton;
    protected Button unlinkButton;
    protected Button userExtra;
    protected Button updateProfile;

    protected AGConnectAuth auth;

    protected FrameLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_base_auth);
        auth = AGConnectAuth.getInstance();
        layout = findViewById(R.id.layout);

        iconImageView = findViewById(R.id.icon);
        nameTextView = findViewById(R.id.name);
        loginButton = findViewById(R.id.login);
        logoutButton = findViewById(R.id.logout);
        linkButton = findViewById(R.id.link);
        unlinkButton = findViewById(R.id.unlink);
        userExtra = findViewById(R.id.user_extra);
        updateProfile = findViewById(R.id.update_profile);

        loginButton.setOnClickListener(view -> {
            login();
        });
        logoutButton.setOnClickListener(view -> {
            logout();
        });
        linkButton.setOnClickListener(view -> {
            link();
        });
        unlinkButton.setOnClickListener(view -> {
            unlink();
        });
        userExtra.setOnClickListener(v -> {
            getUserExtra();
        });

        updateProfile.setOnClickListener(v -> {
            updateProfile();
        });

        updateUI();
    }

    protected abstract void login();

    protected abstract void logout();

    protected abstract void link();

    protected abstract void unlink();

    private void getUserExtra() {
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getUserExtra()
                .addOnSuccessListener(userExtra -> BaseAuthActivity.this.showToast("CreateTime:" + userExtra.getCreateTime()))
                .addOnFailureListener(e -> showToast("Get UserExtra fail" + e));
        } else {
            showToast("user is null");
        }
    }

    private void updateProfile() {
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {
            LinearLayout ll = new LinearLayout(BaseAuthActivity.this);
            ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.setOrientation(LinearLayout.VERTICAL);
            EditText displayName = new EditText(BaseAuthActivity.this);
            EditText photoUrl = new EditText(BaseAuthActivity.this);
            displayName.setHint("displayName");
            photoUrl.setHint("photoUrl");
            ll.addView(displayName);
            ll.addView(photoUrl);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BaseAuthActivity.this)
                .setTitle("更新Profile");
            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                String displayNameStr = displayName.getText().toString();
                String photoUrlStr = photoUrl.getText().toString();
                ProfileRequest userProfile = new ProfileRequest.Builder()
                    .setDisplayName(displayNameStr)
                    .setPhotoUrl(photoUrlStr)
                    .build();
                user.updateProfile(userProfile)
                    .addOnSuccessListener(aVoid -> {
                        BaseAuthActivity.this.showToast("updateProfile success");
                        updateUI();
                    })
                    .addOnFailureListener(e -> showToast("updateProfile failure" + e));
            });
            dialogBuilder.setView(ll);
            dialogBuilder.show();
        } else {
            showToast("user is null");
        }
    }

    protected void updateUI() {
        AGConnectUser user = auth.getCurrentUser();
        if (user == null) {
            nameTextView.setText(R.string.nouser);
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            linkButton.setVisibility(View.GONE);
            unlinkButton.setVisibility(View.GONE);
            userExtra.setVisibility(View.GONE);
            updateProfile.setVisibility(View.GONE);
            Glide.with(this).load(R.mipmap.ic_launcher).into(iconImageView);
        } else if (user.isAnonymous()) {
            nameTextView.setText(String.format("uid:%s\ndisplayName::%s\nemail:%s",
                user.getUid(),
                user.getDisplayName(),
                user.getEmail()));
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            linkButton.setVisibility(View.VISIBLE);
            unlinkButton.setVisibility(View.VISIBLE);
            userExtra.setVisibility(View.VISIBLE);
            updateProfile.setVisibility(View.VISIBLE);
            Glide.with(this).load(user.getPhotoUrl()).into(iconImageView);
        } else {
            nameTextView.setText(String.format("uid:%s\ndisplayName::%s\nemail:%s",
                user.getUid(),
                user.getDisplayName(),
                user.getEmail()));
            Glide.with(this).load(user.getPhotoUrl()).into(iconImageView);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            linkButton.setVisibility(View.VISIBLE);
            unlinkButton.setVisibility(View.VISIBLE);
            userExtra.setVisibility(View.VISIBLE);
            updateProfile.setVisibility(View.VISIBLE);
        }
    }

    protected void showToast(String msg) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(() -> Toast.makeText(BaseAuthActivity.this, msg, Toast.LENGTH_SHORT).show());
        }
    }
}
