package com.ayoub.lockBox.model;

public record LockBoxData (String salt, String iv, String ciphertext){}
