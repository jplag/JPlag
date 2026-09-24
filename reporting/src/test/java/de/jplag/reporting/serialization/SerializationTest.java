package de.jplag.reporting.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.clustering.ClusteringAlgorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class SerializationTest {
    @Test
    void testEnumSerialization() throws JsonProcessingException {
        ObjectMapper mapper = JacksonUtils.createNewObjectMapper();

        assertEquals("\"" + ClusteringAlgorithm.SPECTRAL.name() + "\"", mapper.writeValueAsString(ClusteringAlgorithm.SPECTRAL));
    }
}
