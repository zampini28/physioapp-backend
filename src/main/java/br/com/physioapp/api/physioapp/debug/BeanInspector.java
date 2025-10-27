package br.com.physioapp.api.physioapp.debug;

import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanInspector implements ApplicationRunner {
    private final ApplicationContext ctx;

    public BeanInspector(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] names = ctx.getBeanNamesForType(org.springframework.web.filter.OncePerRequestFilter.class);
        System.out.println("OncePerRequestFilter beans: " + Arrays.toString(names));
        names = ctx.getBeanNamesForType(br.com.physioapp.api.physioapp.security.JwtAuthenticationFilter.class);
        System.out.println("JwtAuthenticationFilter beans for security class: " + Arrays.toString(names));
    }
}
