package org.example.pivo.components;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.pivo.config.property.NanoIdProperty;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class NanoIdGenerator {
    private final NanoIdProperty nanoIdProperty;
    private static Supplier<String> generateNanoId;

    @PostConstruct
    private void init(){
        Random random = new SecureRandom();
        generateNanoId = () -> NanoIdUtils.randomNanoId(
                random, nanoIdProperty.getAlphabet().toCharArray(), nanoIdProperty.getSize());
    }

    public static String gen(){
        return generateNanoId.get();
    }
}
