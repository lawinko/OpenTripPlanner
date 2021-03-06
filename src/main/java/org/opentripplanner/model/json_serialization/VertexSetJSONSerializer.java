package org.opentripplanner.model.json_serialization;

import java.io.IOException;


import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;


/**
 * This serializes an object containing Vertex objects, replacing any edges with integer edge ids.
 * @see EdgeSetJSONSerializer
 * @author novalis
 *
 */
public class VertexSetJSONSerializer extends JsonSerializer<WithGraph> {

    @Override
    public void serialize(WithGraph object, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        // FIXME: there is probably a simple way to automatically wire this up that I can't find
        // right now 
        ObjectMapper mapper = SerializerUtils.getMapper();

        SimpleModule module = SerializerUtils.getSerializerModule();
        module.addSerializer(new EdgeSerializer(object.getGraph()));
        
        mapper.registerModule(module);
        //configuring jgen to just use the mapper doesn't actually work
        jgen.writeRawValue(mapper.writeValueAsString(object.getObject()));
    }
    
    class EdgeSerializer extends JsonSerializer<Edge> {

        private Graph graph;

        public EdgeSerializer(Graph graph) {
            this.graph = graph;
        }

        @Override
        public void serialize(Edge value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            Integer edgeId = graph.getIdForEdge(value);
            jgen.writeObject(edgeId);
        }
        
        @Override
        public Class<Edge> handledType() {
            return Edge.class;
        }
        
    }
}
