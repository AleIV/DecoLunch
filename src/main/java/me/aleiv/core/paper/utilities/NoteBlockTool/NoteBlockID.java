package me.aleiv.core.paper.utilities.NoteBlockTool;

import org.bukkit.Instrument;
import org.bukkit.Note.Tone;

import lombok.Data;

@Data
public class NoteBlockID {

    String instrumentF3;
    int noteF3;

    Instrument instrument;
    Tone tone;
    int octave;
    boolean powered;
    boolean sharped;

    public NoteBlockID(String instrumentF3, int noteF3, Instrument instrument, Tone tone, int octave,
            boolean powered) {
        this.instrumentF3 = instrumentF3;
        this.noteF3 = noteF3;
        this.instrument = instrument;
        this.tone = tone;
        this.octave = octave;
        this.powered = powered;
    }

}
