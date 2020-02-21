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

import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.QQAuthProvider;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class QQActivity extends BaseAuthActivity {
    private Tencent mTencent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTencent = Tencent.createInstance(getString(R.string.qq_app_id), this.getApplicationContext());
    }

    protected void login() {
        mTencent.login(this, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject jsonObject = (JSONObject) o;
                String accessToken = jsonObject.optString("access_token");
                String openId = jsonObject.optString("openid");
                AGConnectAuthCredential credential = QQAuthProvider.credentialWithToken(accessToken, openId);
                auth.signIn(credential).addOnSuccessListener(signInResult -> {
                    updateUI();
                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
            }

            @Override
            public void onError(UiError uiError) {
                showToast(uiError.toString());
            }

            @Override
            public void onCancel() {
                showToast("Cancel");
            }
        });
    }

    protected void logout() {
        mTencent.logout(this);
        auth.signOut();
        updateUI();
    }

    protected void link() {
        mTencent.login(this, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject jsonObject = (JSONObject) o;
                String accessToken = jsonObject.optString("access_token");
                String openId = jsonObject.optString("openid");
                AGConnectAuthCredential credential = QQAuthProvider.credentialWithToken(accessToken, openId);
                auth.getCurrentUser().link(credential).addOnSuccessListener(signInResult -> {
                    updateUI();
                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
            }

            @Override
            public void onError(UiError uiError) {
                showToast(uiError.toString());
            }

            @Override
            public void onCancel() {
                showToast("Cancel");
            }
        });
    }

    protected void unlink() {
        auth.getCurrentUser().unlink(AGConnectAuthCredential.QQ_Provider);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, null);
    }
}
