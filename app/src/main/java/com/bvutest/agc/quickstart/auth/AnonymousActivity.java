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

import android.os.Bundle;

import androidx.annotation.Nullable;

public class AnonymousActivity extends BaseAuthActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void login() {
        auth.signInAnonymously().addOnSuccessListener(signInResult -> {
            updateUI();
        }).addOnFailureListener(e -> {
            showToast(e.getMessage());
        });
    }

    protected void logout() {
        auth.signOut();
        updateUI();
    }

    protected void link() {

    }

    protected void unlink() {

    }
}