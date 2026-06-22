package com.erp.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import java.util.UUID;

/**
 * MD5加盐加密工具 用于用户密码
 */
public class Md5Util {

    /**
     * 生成32位随机盐
     */
    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    /**
     * 密码+盐 双重MD5加密
     */
    public static String encrypt(String password, String salt) {
        if (StrUtil.isBlank(password) || StrUtil.isBlank(salt)) {
            throw new RuntimeException("密码或盐不能为空");
        }
        String temp = password + salt;
        return DigestUtil.md5Hex(DigestUtil.md5Hex(temp));
    }

    /**
     * 校验密码
     */
    public static boolean match(String rawPwd, String salt, String encryptPwd) {
        String target = encrypt(rawPwd, salt);
        return target.equals(encryptPwd);
    }
}