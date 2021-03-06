/*
 * Copyright 2017 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2017-02-11 02:24:31
 *
 */

package com.gcssloop.diycode_sdk.api.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gcssloop.diycode_sdk.api.bean.Token;

/**
 * 登录
 */
public class LoginEvent {
    private boolean ok = false;     // 是否登录成功
    private Integer state = -1;     // 状态码
    private Token token;            // 令牌

    /**
     * @param state 状态码
     */
    public LoginEvent(@NonNull Integer state) {
        this.ok = false;
        this.token = null;
        this.state = state;
    }

    /**
     * @param state 状态码
     * @param token 令牌 token
     */
    public LoginEvent(@NonNull Integer state, @Nullable Token token) {
        this.ok = true;
        this.token = token;
        this.state = state;
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * 获取 token
     *
     * @return token
     */
    public Token getToken() {
        return token;
    }

    /**
     * 获取请求状态
     *
     * @return 请求状态
     */
    public Integer getState() {
        return state;
    }

}
