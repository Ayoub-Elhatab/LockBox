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
public class DerivedKey {
    private final byte[] key;
    private final byte[] salt;
}
