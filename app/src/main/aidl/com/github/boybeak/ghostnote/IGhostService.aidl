// IGhostService.aidl
package com.github.boybeak.ghostnote;

interface IGhostService {
    void showNote(int id);
    void updateNote(int id);
    void dismissNote(int id);
}