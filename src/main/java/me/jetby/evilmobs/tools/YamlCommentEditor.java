package me.jetby.evilmobs.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class YamlCommentEditor {

    private final File file;
    private final List<String> lines;
    private boolean modified = false;

    public YamlCommentEditor(File file) throws IOException {
        this.file = file;
        this.lines = new ArrayList<>(Files.readAllLines(file.toPath()));
        clearAllComments();
    }

    private void clearAllComments() {
        lines.removeIf(line -> !line.trim().isEmpty() && line.trim().charAt(0) == '#');
        modified = true;
    }

    public void setComment(String path, List<String> comments) {
        String[] keys = path.split("\\.");
        int depth = Math.max(0, keys.length - 1);
        String indent = "  ".repeat(depth);
        String target = keys[depth] + ":";

        for (int i = 0; i < lines.size(); i++) {
            String trimmed = lines.get(i).trim();
            if (trimmed.startsWith(target)) {
                for (int j = comments.size() - 1; j >= 0; j--) {
                    lines.add(i, indent + "# " + comments.get(j));
                }
                modified = true;
                return;
            }
        }
    }

    public void setComment(String path, String... comments) {
        setComment(path, List.of(comments));
    }

    public void save() throws IOException {
        if (!modified) return;
        Files.write(file.toPath(), lines);
        modified = false;
    }
}