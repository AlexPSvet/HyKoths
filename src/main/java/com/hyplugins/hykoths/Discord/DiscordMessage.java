package com.hyplugins.hykoths.Discord;

import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DiscordMessage {

    private static String webhookUrl;

    private final String content;
    private final String title;
    private final String imageUrl;
    private final String description;
    private final String color;
    private final Author author;
    private final Footer footer;
    private final List<Field> fields;

    public DiscordMessage(String content, String title, String imageUrl, String description, String color, List<Field> fields, Author author, Footer footer) {
        this.content = content;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.color = color;
        this.fields = fields;
        this.author = author;
        this.footer = footer;
    }

    private String toJsonString(KothObject koth) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"title\": \"").append(Utils.replacePlaceHolders(koth, title)).append("\", ");
        jsonBuilder.append("\"description\": \"").append(Utils.replacePlaceHolders(koth, description)).append("\", ");
        jsonBuilder.append("\"color\": \"").append(convertHexToColor(color)).append("\", ");

        if (imageUrl != null) {
            jsonBuilder.append("\"image\": {\"url\": \"").append(imageUrl).append("\"}, ");
        }

        if (author != null) {
            jsonBuilder.append("\"author\": {\"name\": \"").append(Utils.replacePlaceHolders(koth, author.getName())).append("\", ");
            if (author.getUrl() != null) {
                jsonBuilder.append("\"url\": \"").append(author.getUrl()).append("\", ");
            }
            if (author.getIconUrl() != null) {
                jsonBuilder.append("\"icon_url\": \"").append(author.getIconUrl()).append("\"");
            }
            jsonBuilder.append("}, ");
        }

        if (!fields.isEmpty()) {
            jsonBuilder.append("\"fields\": [");
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                jsonBuilder.append("{\"name\": \"").append(Utils.replacePlaceHolders(koth, field.getName())).append("\", ");
                jsonBuilder.append("\"value\": \"").append(Utils.replacePlaceHolders(koth, field.getValue())).append("\", ");
                jsonBuilder.append("\"inline\": ").append(field.isInline()).append("}");
                if (i < fields.size() - 1) {
                    jsonBuilder.append(", ");
                }
            }
            jsonBuilder.append("], ");
        }

        if (footer != null) {
            jsonBuilder.append("\"footer\": {");
            if (footer.getText() != null) {
                jsonBuilder.append("\"text\": \"").append(Utils.replacePlaceHolders(koth, footer.getText())).append("\"");
                if (footer.getIconUrl() != null) {
                    jsonBuilder.append(", ");
                }
            }
            if (footer.getIconUrl() != null) {
                jsonBuilder.append("\"icon_url\": \"").append(footer.getIconUrl()).append("\"");
            }
            jsonBuilder.append("}");
        }

        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

    public void sendMessage(KothObject koth) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = "{\"content\": \"" + Utils.replacePlaceHolders(koth, content) + "\", \"embeds\": [" + toJsonString(koth) + "]}";

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(payload.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                Bukkit.getConsoleSender().sendMessage("Error while sending koth webhook. Reponse code error: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int convertHexToColor(String hexColor) {
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }
        try {
            return Integer.parseInt(hexColor, 16);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static class Author {
        private final String name;
        private final String url;
        private final String iconUrl;

        public Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }

    public static class Footer {
        private final String text;
        private final String iconUrl;

        public Footer(String text, String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
        }

        public String getText() {
            return text;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }

    public static void setWebhookUrl(String webhookUrl) {
        DiscordMessage.webhookUrl = webhookUrl;
    }
}
