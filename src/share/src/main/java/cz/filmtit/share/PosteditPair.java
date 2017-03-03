/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.share;

import java.io.Serializable;

/**
 *
 * @author matus
 */
public class PosteditPair implements com.google.gwt.user.client.rpc.IsSerializable, Serializable {

    private Long id = (long) - 1;

    private Chunk chunk1;

    private Chunk chunk2;
    
    private PosteditSource source;

    public PosteditPair() {
        // do nothing
    }

    public PosteditPair(Chunk chunk1, Chunk chunk2) {
        this.chunk1 = chunk1;
        this.chunk2 = chunk2;
    }

    public PosteditPair(String userTranslation, String userTranslation1) {
        this(new Chunk(userTranslation), new Chunk(userTranslation1));
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the chunk1
     */
    public Chunk getChunk1() {
        return chunk1;
    }

    /**
     * @param chunk1 the chunk1 to set
     */
    public void setChunk1(Chunk chunk1) {
        this.chunk1 = chunk1;
    }

    /**
     * @return the chunk2
     */
    public Chunk getChunk2() {
        return chunk2;
    }

    /**
     * @param chunk2 the chunk2 to set
     */
    public void setChunk2(Chunk chunk2) {
        this.chunk2 = chunk2;
    }

    public String getString2() {
        if (chunk2 == null) {
            return "";
        }
        return chunk2.getSurfaceForm();
    }

    public String getString1() {
        if (chunk1 == null) {
            return "";
        }
        return chunk1.getSurfaceForm();
    }

    public Double getScore() {
        return 0d;
    }

    /**
     * @return the source
     */
    public PosteditSource getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(PosteditSource source) {
        this.source = source;
    }

}
