package com.zipwhip.zuno.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ZipwhipMessage implements Serializable {

    private String body;
    private String finalSource;

}
