package com.zipwhip.zuno.game;

import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdGenerator {

    private final Hashids hashids;

    public String generate() {
        long nanoTime = System.nanoTime();
        return hashids.encode(nanoTime);
    }

}
