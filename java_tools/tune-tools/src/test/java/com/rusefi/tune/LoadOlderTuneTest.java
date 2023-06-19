package com.rusefi.tune;

import com.opensr5.ini.IniFileModel;
import com.rusefi.*;
import com.rusefi.tools.tune.TuneCanTool;
import com.rusefi.tools.tune.TuneTools;
import com.rusefi.tune.xml.Msq;
import com.rusefi.tune.xml.Page;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LoadOlderTuneTest {
    @Test
    public void loadOlderTuneAgainstCurrentIni() throws Exception {
        Msq customOldTune = Msq.readTune(TuneReadWriteTest.TUNE_NAME);
        Page dataPage = customOldTune.findPage();
        assertFalse(dataPage.constant.isEmpty());

        Msq lessOldDefaultTune = Msq.readTune(TuneCanTool.SRC_TEST_RESOURCES + "simulator_tune-2023-06.xml");

        IniFileModel ini = new IniFileModel().readIniFile(TuneReadWriteTest.TEST_INI);
        assertFalse(ini.fieldsInUiOrder.isEmpty());

        RootHolder.ROOT = "../../firmware/";

        StringBuilder sb = TuneCanTool.getTunePatch(lessOldDefaultTune, customOldTune, ini);

        assertEquals("    // default \"Single Coil\"\n" +
                "    engineConfiguration->ignitionMode = IM_ONE_COIL;\n" +
                "    // default 4.0\n" +
                "    engineConfiguration->cylindersCount = 4;\n" +
                "    // default 1000.0\n" +
                "    engineConfiguration->tps1SecondaryMin = 0.0;\n" +
                "    // default 0.0\n" +
                "    engineConfiguration->tps1SecondaryMax = 1000.0;\n" +
                "    // default 1000.0\n" +
                "    engineConfiguration->tps2SecondaryMin = 0.0;\n" +
                "    // default 0.0\n" +
                "    engineConfiguration->tps2SecondaryMax = 1000.0;\n" +
                "    // default 5.0\n" +
                "    engineConfiguration->throttlePedalSecondaryUpVoltage = 0.0;\n" +
                "    // default 65.0\n" +
                "    engineConfiguration->mc33_hvolt = 0.0;\n" +
                "    // default 13000.0\n" +
                "    engineConfiguration->mc33_i_boost = 0.0;\n" +
                "    // default 9400.0\n" +
                "    engineConfiguration->mc33_i_peak = 0.0;\n" +
                "    // default 3700.0\n" +
                "    engineConfiguration->mc33_i_hold = 0.0;\n" +
                "    // default 400.0\n" +
                "    engineConfiguration->mc33_t_max_boost = 0.0;\n" +
                "    // default 10.0\n" +
                "    engineConfiguration->mc33_t_peak_off = 0.0;\n" +
                "    // default 700.0\n" +
                "    engineConfiguration->mc33_t_peak_tot = 0.0;\n" +
                "    // default 10.0\n" +
                "    engineConfiguration->mc33_t_bypass = 0.0;\n" +
                "    // default 60.0\n" +
                "    engineConfiguration->mc33_t_hold_off = 0.0;\n" +
                "    // default 10000.0\n" +
                "    engineConfiguration->mc33_t_hold_tot = 0.0;\n" +
                "    // default \"PULLUP\"\n" +
                "    engineConfiguration->clutchDownPinMode = 1.0;\n" +
                "    // default \"PULLUP\"\n" +
                "    engineConfiguration->clutchUpPinMode = 1.0;\n" +
                "    // default 410.0\n" +
                "    engineConfiguration->mapErrorDetectionTooHigh = 250.0;\n" +
                "    // default 3.0\n" +
                "    engineConfiguration->idleStepperReactionTime = 10.0;\n" +
                "    // default 200.0\n" +
                "    engineConfiguration->idleStepperTotalSteps = 150.0;\n" +
                "    // default \"true\"\n" +
                "    engineConfiguration->stepperForceParkingEveryRestart = false;\n" +
                "    // default -20.0\n" +
                "    engineConfiguration->idlerpmpid_iTermMin = -200.0;\n" +
                "    // default 20.0\n" +
                "    engineConfiguration->idlerpmpid_iTermMax = 200.0;\n" +
                "    // default 300.0\n" +
                "    engineConfiguration->idlePidRpmUpperLimit = 0.0;\n" +
                "    // default 5.0\n" +
                "    engineConfiguration->idlePidDeactivationTpsThreshold = 2.0;\n" +
                "    // default 0.0\n" +
                "    engineConfiguration->warningPeriod = 10.0;\n" +
                "    // default \"false\"\n" +
                "    engineConfiguration->isHip9011Enabled = true;\n" +
                "    // default 0.0\n" +
                "    engineConfiguration->hip9011PrescalerAndSDO = 6.0;\n" +
                "    // default 20.0\n" +
                "    engineConfiguration->knockDetectionWindowStart = 35.0;\n" +
                "    // default 60.0\n" +
                "    engineConfiguration->knockDetectionWindowEnd = 135.0;\n" +
                "    // default \"false\"\n" +
                "    engineConfiguration->is_enabled_spi_3 = t", sb.substring(0, 2400));
    }

    @Test(expected = IllegalStateException.class)
    public void testLegacyCustomEnumOrdinal() {
        String tsCustomLine = "bits, U08, @OFFSET@, [0:1], \"Single Coil\", \"Individual Coils\", \"Wasted Spark\", \"Two Distributors\"";

        Assert.assertEquals(0, TuneTools.resolveEnumByName(tsCustomLine, "One coil"));
    }

    @Test
    public void testCustomEnumOrdinal() {
        String tsCustomLine = "bits, U08, @OFFSET@, [0:1], \"Single Coil\", \"Individual Coils\", \"Wasted Spark\", \"Two Distributors\"";

        Assert.assertEquals(0, TuneTools.resolveEnumByName(tsCustomLine, "Single coil"));
        Assert.assertEquals(3, TuneTools.resolveEnumByName(tsCustomLine, "Two Distributors"));
    }
}
