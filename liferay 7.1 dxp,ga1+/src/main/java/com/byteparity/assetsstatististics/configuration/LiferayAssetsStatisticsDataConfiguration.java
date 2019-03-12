package com.byteparity.assetsstatististics.configuration;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(id = "com.byteparity.assetsstatististics.configuration.LiferayAssetsStatisticsDataConfiguration")
public interface LiferayAssetsStatisticsDataConfiguration {
	@Meta.AD(required = false)
    public String getAssetsStatistics();
	
	@Meta.AD(required = false)
    public String getDateFormat();
}
