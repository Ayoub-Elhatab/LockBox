package com.ayoub.lockBox.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class EncryptedData {
    private final String ciphertext;
    private final String iv;
}