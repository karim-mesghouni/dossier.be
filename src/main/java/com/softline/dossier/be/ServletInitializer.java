package com.softline.dossier.be;

import com.softline.dossier.be.Halpers.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ServletInitializer extends SpringBootServletInitializer {
    @Autowired
    private EnvUtil util;
}