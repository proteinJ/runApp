package com.running.runapp.domain.ghostRun.config;

import com.running.runapp.domain.ghostRun.domain.GhostCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GhostCategoryWebConverter implements Converter<String, GhostCategory> {

    @Override
    public GhostCategory convert(String source) {
        return GhostCategory.from(source);
    }
}
