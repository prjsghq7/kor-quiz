package com.kgh.korquiz.results.user;

import com.kgh.korquiz.results.Result;

public enum RegisterResult implements Result {
    FAILURE_DUPLICATE_NICKNAME,
    FAILURE_OAUTH_SESSION_EXPIRED,
}
