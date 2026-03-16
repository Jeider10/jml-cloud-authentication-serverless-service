package com.cloud.jml.banner;

import com.github.lalyos.jfiglet.FigletFont;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class DynamicBanner implements Banner {

    private final String microName;

    private static final String GREEN = "\033[32m";
    private static final String CYAN = "\033[36m";
    private static final String RESET = "\033[0m";

    public DynamicBanner(String microName) {
        this.microName = microName;
    }

    @Override
    public void printBanner(Environment env, Class<?> sourceClass, PrintStream out) {

        String version = env.getProperty("application.version", "unknown");

        try {

            String ascii = FigletFont.convertOneLine(microName.toUpperCase());

            out.println();
            out.println(GREEN + ascii + RESET);

            out.println(CYAN + ":: Microservice :: (v" + version + ")" + RESET);

            out.println(CYAN + "Java " + System.getProperty("java.version")
                    + " | Spring Boot " + SpringBootVersion.getVersion() + RESET);

            out.println();

        } catch (Exception e) {

            out.println(microName + " v" + version);

        }
    }
}
