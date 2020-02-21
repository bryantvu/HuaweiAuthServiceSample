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

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.FacebookAuthProvider;

import java.util.Arrays;

public class FacebookActivity extends BaseAuthActivity {
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void login() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();
                AGConnectAuthCredential credential = FacebookAuthProvider.credentialWithToken(token);
                auth.signIn(credential).addOnSuccessListener(signInResult -> {
                    updateUI();
                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
            }

            @Override
            public void onCancel() {
                showToast("Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                showToast(error.getMessage());
            }
        });
    }

    protected void logout() {
        LoginManager.getInstance().logOut();
        auth.signOut();
        updateUI();
    }

    protected void link() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();
                AGConnectAuthCredential credential = FacebookAuthProvider.credentialWithToken(token);
                auth.getCurrentUser().link(credential).addOnSuccessListener(signInResult -> {
                    updateUI();
                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
            }

            @Override
            public void onCancel() {
                showToast("Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                showToast(error.getMessage());
            }
        });
    }

    protected void unlink() {
        auth.getCurrentUser().unlink(AGConnectAuthCredential.Facebook_Provider);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
