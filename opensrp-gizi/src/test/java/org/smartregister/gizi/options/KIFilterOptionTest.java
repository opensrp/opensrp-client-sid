package org.smartregister.gizi.options;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.gizi.option.KICommonObjectFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

import shared.BaseUnitTest;

/**
 * Created by sid-tech on 4/24/18
 */
public class KIFilterOptionTest extends BaseUnitTest {

    @Mock
    private SmartRegisterClient smartRegisterClient;
    private String criteria = "criteria";
    private String fieldName = "location_name";
    private String filterOption = "filter_option";
    private String tableName = "ec_anak";
    private KICommonObjectFilterOption motherFilterOption;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        motherFilterOption = new KICommonObjectFilterOption(criteria, fieldName, filterOption);
        motherFilterOption.name();
        motherFilterOption.filter();
        Assert.assertFalse(motherFilterOption.filter(smartRegisterClient));
//        childFilterOption.filter(smartRegisterClient);

    }

    @Test
    public void filterOptionReturnString() {
        Assert.assertEquals(motherFilterOption.name(), "filter_option");
    }

    @Test
    public void filterWithVALUE() {

        Assert.assertEquals("AND " + tableName + ".relational_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "')", motherFilterOption.filter());

    }

    @Test
    public void filterWithKEY() {
        fieldName = "josh";
        criteria = "abcd";
        motherFilterOption = new KICommonObjectFilterOption(criteria, fieldName, filterOption);
        Assert.assertEquals("AND " + tableName + ".relational_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "')", motherFilterOption.filter());

    }

    @Test
    public void filterTest() {
        Assert.assertFalse(motherFilterOption.filter(smartRegisterClient));
    }

}