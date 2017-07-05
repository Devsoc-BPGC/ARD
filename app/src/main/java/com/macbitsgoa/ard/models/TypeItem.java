package com.macbitsgoa.ard.models;

/**
 * Item format that can store any data and a type value.
 *
 * @author Vikramaditya Kukreja
 */
public class TypeItem {

    /**
     * Data for the object depending on {@link #type}.
     */
    private Object data;

    /**
     * An int value corresponding to the type of data.
     * Eg. {@link com.macbitsgoa.ard.types.PostType#ANNOUNCEMENT}
     */
    private int type;

    /**
     * Constructor for type item.
     *
     * @param data Object data.
     * @param type int value of type declared in an util class.
     */
    public TypeItem(final Object data, final int type) {
        this.data = data;
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public int getType() {
        return type;
    }

}
