package fr.craftandconquest.warofsquirrels.utils;

import java.util.EventListener;

public interface OnSaveListener extends EventListener {
    String Name();
    void Save();
    void BackupSave();
}
