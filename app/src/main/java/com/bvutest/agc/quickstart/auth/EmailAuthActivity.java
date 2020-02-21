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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.EmailUser;
import com.huawei.agconnect.auth.VerifyCodeSettings;

import java.util.Locale;

public class EmailAuthActivity extends BaseAuthActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        View content = inflater.inflate(R.layout.activity_email, null);
        layout.addView(content);
    }

    @Override
    protected void login() {
    }

    @Override
    protected void logout() {
        auth.signOut();
        updateUI();
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        loginButton.setVisibility(View.GONE);
    }

    @Override
    protected void link() {
        AGConnectUser user = auth.getCurrentUser();
        if (user != null) {
            //Email账户不能link Email账户，你可以通过别的途径来登录，登录成功后link到email账户
            //邮箱link一定要验证码，验证码类型是登录/注册，
            LinearLayout ll = new LinearLayout(this);
            ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.setOrientation(LinearLayout.VERTICAL);
            EditText email = new EditText(this);
            EditText verifyCode = new EditText(this);
            email.setHint("Email");
            verifyCode.setHint("verify Code");
            email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            verifyCode.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(email);
            ll.addView(verifyCode);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Link 邮箱");
            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                String emailStr = email.getText().toString();
                String verifyCodeStr = verifyCode.getText().toString();

                AGConnectAuthCredential credential = EmailAuthProvider.credentialWithVerifyCode(emailStr, null, verifyCodeStr);
                user.link(credential)
                    .addOnSuccessListener(signInResult -> showToast("link success"))
                    .addOnFailureListener(e -> showToast("link fail" + e));
                updateUI();
            });
            dialogBuilder.setView(ll);
            dialogBuilder.show();
        }
    }

    @Override
    protected void unlink() {
        AGConnectUser user = auth.getCurrentUser();
        if (user != null) {
            user.unlink(AGConnectAuthCredential.Email_Provider);
        }
    }

    public void verifyCode(View view) {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        EditText email = new EditText(this);
        CheckBox registerLoginCheckBox = new CheckBox(this);
        CheckBox resetPasswordCheckBox = new CheckBox(this);
        registerLoginCheckBox.setHint("ACTION_REGISTER_LOGIN");
        resetPasswordCheckBox.setHint("ACTION_RESET_PASSWORD");
        registerLoginCheckBox.setChecked(true);

        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (isChecked) {
                if (buttonView == registerLoginCheckBox) {
                    resetPasswordCheckBox.setChecked(false);
                } else {
                    registerLoginCheckBox.setChecked(false);
                }
            }
        };
        registerLoginCheckBox.setOnCheckedChangeListener(listener);
        resetPasswordCheckBox.setOnCheckedChangeListener(listener);

        email.setHint("Email");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        ll.addView(email);
        ll.addView(registerLoginCheckBox);
        ll.addView(resetPasswordCheckBox);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
            .setTitle("申请验证码");
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            String emailStr = email.getText().toString();
            int action;
            if (registerLoginCheckBox.isChecked()) {
                action = VerifyCodeSettings.ACTION_REGISTER_LOGIN;
            } else {
                action = VerifyCodeSettings.ACTION_RESET_PASSWORD;
            }

            VerifyCodeSettings settings = VerifyCodeSettings.newBuilder()
                .action(action) //ACTION_REGISTER_LOGIN/ACTION_RESET_PASSWORD
                .sendInterval(30) //最小发送间隔，30-120s
                .locale(Locale.US) //可选，设置的locale必须包含language和country
                                                   // 不设置默认为Locale.getDefault(),
                .build();
            EmailAuthProvider.verifyEmailCode(emailStr, settings, new VerifyCodeSettings.OnVerifyCodeCallBack() {
                @Override
                public void onVerifySuccess(String shortestInterval, String validityPeriod) {
                    showToast("onVerifySuccess");
                }

                @Override
                public void onVerifyFailure(Exception e) {
                    showToast(e.toString());
                }
            });
        });
        dialogBuilder.setView(ll);
        dialogBuilder.show();
    }

    public void createEmail(View view) {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        EditText email = new EditText(this);
        EditText password = new EditText(this);
        EditText verifyCode = new EditText(this);
        email.setHint("Email");
        password.setHint("password(可选)");
        verifyCode.setHint("verifyCode");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        verifyCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        ll.addView(email);
        ll.addView(password);
        ll.addView(verifyCode);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
            .setTitle("创建EmailUser");
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            String verifyCodeStr = verifyCode.getText().toString();
            EmailUser emailUser = new EmailUser.Builder()
                .setEmail(emailStr)
                .setPassword(passwordStr)//可选，表示创建用户的同时设置用户密码
                .setVerifyCode(verifyCodeStr)
                .build();
            AGConnectAuth.getInstance().createUser(emailUser)
                .addOnSuccessListener(signInResult -> {
                    EmailAuthActivity.this.showToast("createUserWithEmail success");
                    updateUI();
                })
                .addOnFailureListener(e -> EmailAuthActivity.this.showToast("createUserWithEmail failure:" + e));
        });
        dialogBuilder.setView(ll);
        dialogBuilder.show();
    }

    public void passwordLogin(View view) {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        EditText email = new EditText(this);
        EditText password = new EditText(this);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setHint("Email");
        password.setHint("password");
        ll.addView(email);
        ll.addView(password);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
            .setTitle("密码登录");
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            AGConnectAuthCredential emailAuthCredential =
                EmailAuthProvider.credentialWithPassword(emailStr, passwordStr);
            AGConnectAuth.getInstance().signIn(emailAuthCredential)
                .addOnSuccessListener(signInResult -> {
                    EmailAuthActivity.this.showToast("signInWithPassword success");
                    updateUI();
                })
                .addOnFailureListener(e -> EmailAuthActivity.this.showToast("signInWithPassword failure" + e));
        });
        dialogBuilder.setView(ll);
        dialogBuilder.show();
    }

    public void verifyCodeLogin(View view) {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        EditText email = new EditText(this);
        EditText password = new EditText(this);
        EditText verifyCode = new EditText(this);
        email.setHint("Email");
        password.setHint("password（可选）");
        verifyCode.setHint("verifyCode");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        verifyCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        ll.addView(email);
        ll.addView(password);
        ll.addView(verifyCode);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
            .setTitle("验证码登录");
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            String verifyCodeStr = verifyCode.getText().toString();
            AGConnectAuthCredential emailAuthCredential =
                EmailAuthProvider.credentialWithVerifyCode(emailStr, passwordStr, verifyCodeStr);
            AGConnectAuth.getInstance().signIn(emailAuthCredential)
                .addOnSuccessListener(signInResult -> {
                    showToast("signInWithCode success");
                    updateUI();
                })
                .addOnFailureListener(e -> showToast("signInWithCode failure" + e));
        });
        dialogBuilder.setView(ll);
        dialogBuilder.show();
    }

    public void updateEmail(View view) {
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {
            LinearLayout ll = new LinearLayout(this);
            ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.setOrientation(LinearLayout.VERTICAL);
            EditText email = new EditText(this);
            EditText verifyCode = new EditText(this);
            email.setHint("new Email");
            verifyCode.setHint("verifyCode");
            email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            verifyCode.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(email);
            ll.addView(verifyCode);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("更新邮箱");
            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                String emailStr = email.getText().toString();
                String verifyCodeStr = verifyCode.getText().toString();
                user.updateEmail(emailStr, verifyCodeStr)
                    .addOnSuccessListener(aVoid -> {
                        showToast("updateEmail success");
                        updateUI();
                    })
                    .addOnFailureListener(e -> showToast("updateEmail failure" + e));
            });
            dialogBuilder.setView(ll);
            dialogBuilder.show();
        } else {
            showToast("please login first");
        }
    }

    public void updatePassword(View view) {
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {
            LinearLayout ll = new LinearLayout(this);
            ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.setOrientation(LinearLayout.VERTICAL);
            EditText newPassword = new EditText(this);
            EditText verifyCode = new EditText(this);
            newPassword.setHint("newPassword");
            verifyCode.setHint("verifyCode");
            verifyCode.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(newPassword);
            ll.addView(verifyCode);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("UpdatePassword");
            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                String newPasswordStr = newPassword.getText().toString();
                String verifyCodeStr = verifyCode.getText().toString();
                user.updatePassword(newPasswordStr, verifyCodeStr, AGConnectAuthCredential.Email_Provider)
                    .addOnSuccessListener(aVoid -> showToast("updatePassword success"))
                    .addOnFailureListener(e -> showToast("updatePassword failure" + e));
            });
            dialogBuilder.setView(ll);
            dialogBuilder.show();
        } else {
            showToast("please login first");
        }
    }

}
