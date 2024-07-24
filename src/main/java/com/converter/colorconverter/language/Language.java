package com.converter.colorconverter.language;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.io.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class Language {
    private ObjectProperty<ResourceBundle> resources;

    public Language(){
        resources = new SimpleObjectProperty<>();
    }

    public ObjectProperty<ResourceBundle> getResources() {
        return resources;
    }

    public void setResources(ObjectProperty<ResourceBundle> resources) {
        this.resources = resources;
    }
    public String[] readLangFromFile() throws IOException {
        String file ="src/main/resources/bundles/lang";

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String lang = reader.readLine();
        reader.close();
        return lang.split("_");

    }

    public void saveLanguage(String currLang) throws IOException {
        String file ="src/main/resources/bundles/lang";

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        if (Objects.equals(currLang, "null"))
            currLang = "en_US_English";
        writer.write(currLang);
        writer.close();
    }

    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            {
                bind(resources);
            }

            @Override
            public String computeValue() {
                return resources.get().getString(key);
            }
        };
    }
}

