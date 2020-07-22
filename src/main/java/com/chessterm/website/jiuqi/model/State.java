package com.chessterm.website.jiuqi.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonSerialize(using = State.Serializer.class)
@JsonDeserialize(using = State.Deserializer.class)
public class State extends com.jingbh.flamechess.State {

    public State(byte[] state, int size) {
        super(state, size);
    }

    public State(byte[][] state) {
        super(state);
    }

    static class Serializer extends StdSerializer<State> {

        protected Serializer() {
            this(null);
        }

        protected Serializer(Class<State> t) {
            super(t);
        }

        @Override
        public void serialize(State value, JsonGenerator gen,
                              SerializerProvider provider) throws IOException {
            gen.writeStartArray();
            for (byte[] line: value.get2dState()) {
                gen.writeStartArray();
                for (byte cell: line) {
                    gen.writeNumber(cell);
                }
                gen.writeEndArray();
            }
            gen.writeEndArray();
        }
    }

    static class Deserializer extends StdDeserializer<State> {

        protected Deserializer() {
            this(null);
        }

        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public State deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            byte[][] state = jp.getCodec().readValue(jp, byte[][].class);
            return new State(state);
        }
    }
}
