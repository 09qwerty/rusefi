package com.rusefi.ldmp;

import com.rusefi.util.LazyFile;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StateDictionaryGeneratorTest {
    @Test
    public void test() throws IOException {

        String testYaml = "Usages:\n" +
                "#  output_channels always goes first at least because it has protocol version at well-known offset\n" +
                "  - name: output_channels\n" +
                "    java: TsOutputs.java\n" +
                "    folder: console/binary\n" +
                "    cppFileName: status_loop\n" +
                "    constexpr: \"engine->outputChannels\"\n" +
                "\n" +
                "  - name: fuel_computer\n" +
                "    java: FuelComputer.java\n" +
                "    folder: controllers/algo/fuel\n" +
                "    constexpr: \"engine->fuelComputer\"\n" +
                "    conditional_compilation: \"EFI_ENGINE_CONTROL\"\n";


        Map<String, Object> data = LiveDataProcessor.getStringObjectMap(new StringReader(testYaml));

        Map<String, StringBufferLazyFile> fileCapture = new HashMap<>();
        LiveDataProcessor liveDataProcessor = new LiveDataProcessor("test", fileName -> new StringReader(""), new LazyFile.LazyFileFactory() {
            @Override
            public LazyFile create(String fileName) {
                StringBufferLazyFile file = new StringBufferLazyFile();
                fileCapture.put(fileName, file);
                return file;
            }
        });
        liveDataProcessor.handleYaml(data);
        assertEquals(6, fileCapture.size());


        assertEquals("        stateDictionary.register(live_data_e.LDS_output_channels, TsOutputs.VALUES, \"status_loop\");\n" +
                "        stateDictionary.register(live_data_e.LDS_fuel_computer, FuelComputer.VALUES, \"fuel_computer\");\n", liveDataProcessor.stateDictionaryGenerator.content.toString());
    }
}
