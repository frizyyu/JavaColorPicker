package com.converter.colorconverter.language;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
        String userDir = System.getProperty("user.dir");
        BufferedReader br;
        try {
            File file = new File(userDir, "/colorConverterLangs/bundles/lang");
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e){
            File dir = new File(userDir, "/colorConverterLangs/bundles");
            File file = new File(dir, "lang");
            dir.mkdirs();
            file.createNewFile();
            saveLanguage("null");
            br = new BufferedReader(new FileReader(file));
        }
        String lang = br.readLine();
        br.close();
        return lang.split("_");

    }

    public void saveLanguage(String currLang) throws IOException {
        String userDir = System.getProperty("user.dir");
        File file = new File(userDir, "/colorConverterLangs/bundles/lang");
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

        if (Objects.equals(currLang, "null"))
            currLang = "en_US_English";
        writer.write(currLang.getBytes());
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

