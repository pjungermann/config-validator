/*
 * Copyright 2015-2016 Patrick Jungermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pjungermann.config.types.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.types.ConfigConverter;
import com.github.pjungermann.config.types.FileTypeConfigFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

import static com.github.pjungermann.config.types.json.JsonConverter.MAPPER;

/**
 * {@link com.github.pjungermann.config.types.ConfigFactory}
 * for JSON files ({@code .json}).
 *
 * @author Patrick Jungermann
 */
@Component
@Singleton
public class JsonConfigFactory extends FileTypeConfigFactory<ObjectNode> {

    private JsonConverter converter;

    public JsonConfigFactory() {
        super("json");
    }

    @Inject
    public void setConverter(@NotNull final JsonConverter converter) {
        this.converter = converter;
    }

    @NotNull
    @Override
    protected ConfigConverter<ObjectNode> getConverter() {
        return converter;
    }

    @NotNull
    @Override
    protected ObjectNode doCreate(@NotNull final File source,
                                  @Nullable final String profile,
                                  @NotNull final Config context) throws IOException {
        return (ObjectNode) MAPPER.readTree(source);
    }

}
