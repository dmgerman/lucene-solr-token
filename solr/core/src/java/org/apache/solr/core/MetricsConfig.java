begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MetricsConfig
specifier|public
class|class
name|MetricsConfig
block|{
DECL|field|metricReporters
specifier|private
specifier|final
name|PluginInfo
index|[]
name|metricReporters
decl_stmt|;
DECL|field|hiddenSysProps
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenSysProps
decl_stmt|;
DECL|field|counterSupplier
specifier|private
specifier|final
name|PluginInfo
name|counterSupplier
decl_stmt|;
DECL|field|meterSupplier
specifier|private
specifier|final
name|PluginInfo
name|meterSupplier
decl_stmt|;
DECL|field|timerSupplier
specifier|private
specifier|final
name|PluginInfo
name|timerSupplier
decl_stmt|;
DECL|field|histogramSupplier
specifier|private
specifier|final
name|PluginInfo
name|histogramSupplier
decl_stmt|;
DECL|method|MetricsConfig
specifier|private
name|MetricsConfig
parameter_list|(
name|PluginInfo
index|[]
name|metricReporters
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenSysProps
parameter_list|,
name|PluginInfo
name|counterSupplier
parameter_list|,
name|PluginInfo
name|meterSupplier
parameter_list|,
name|PluginInfo
name|timerSupplier
parameter_list|,
name|PluginInfo
name|histogramSupplier
parameter_list|)
block|{
name|this
operator|.
name|metricReporters
operator|=
name|metricReporters
expr_stmt|;
name|this
operator|.
name|hiddenSysProps
operator|=
name|hiddenSysProps
expr_stmt|;
name|this
operator|.
name|counterSupplier
operator|=
name|counterSupplier
expr_stmt|;
name|this
operator|.
name|meterSupplier
operator|=
name|meterSupplier
expr_stmt|;
name|this
operator|.
name|timerSupplier
operator|=
name|timerSupplier
expr_stmt|;
name|this
operator|.
name|histogramSupplier
operator|=
name|histogramSupplier
expr_stmt|;
block|}
DECL|method|getMetricReporters
specifier|public
name|PluginInfo
index|[]
name|getMetricReporters
parameter_list|()
block|{
return|return
name|metricReporters
return|;
block|}
DECL|method|getHiddenSysProps
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getHiddenSysProps
parameter_list|()
block|{
return|return
name|hiddenSysProps
return|;
block|}
DECL|method|getCounterSupplier
specifier|public
name|PluginInfo
name|getCounterSupplier
parameter_list|()
block|{
return|return
name|counterSupplier
return|;
block|}
DECL|method|getMeterSupplier
specifier|public
name|PluginInfo
name|getMeterSupplier
parameter_list|()
block|{
return|return
name|meterSupplier
return|;
block|}
DECL|method|getTimerSupplier
specifier|public
name|PluginInfo
name|getTimerSupplier
parameter_list|()
block|{
return|return
name|timerSupplier
return|;
block|}
DECL|method|getHistogramSupplier
specifier|public
name|PluginInfo
name|getHistogramSupplier
parameter_list|()
block|{
return|return
name|histogramSupplier
return|;
block|}
DECL|class|MetricsConfigBuilder
specifier|public
specifier|static
class|class
name|MetricsConfigBuilder
block|{
DECL|field|metricReporterPlugins
specifier|private
name|PluginInfo
index|[]
name|metricReporterPlugins
init|=
operator|new
name|PluginInfo
index|[
literal|0
index|]
decl_stmt|;
DECL|field|hiddenSysProps
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenSysProps
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|counterSupplier
specifier|private
name|PluginInfo
name|counterSupplier
decl_stmt|;
DECL|field|meterSupplier
specifier|private
name|PluginInfo
name|meterSupplier
decl_stmt|;
DECL|field|timerSupplier
specifier|private
name|PluginInfo
name|timerSupplier
decl_stmt|;
DECL|field|histogramSupplier
specifier|private
name|PluginInfo
name|histogramSupplier
decl_stmt|;
DECL|method|MetricsConfigBuilder
specifier|public
name|MetricsConfigBuilder
parameter_list|()
block|{      }
DECL|method|setHiddenSysProps
specifier|public
name|MetricsConfigBuilder
name|setHiddenSysProps
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenSysProps
parameter_list|)
block|{
if|if
condition|(
name|hiddenSysProps
operator|!=
literal|null
operator|&&
operator|!
name|hiddenSysProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|hiddenSysProps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|hiddenSysProps
operator|.
name|addAll
argument_list|(
name|hiddenSysProps
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|setMetricReporterPlugins
specifier|public
name|MetricsConfigBuilder
name|setMetricReporterPlugins
parameter_list|(
name|PluginInfo
index|[]
name|metricReporterPlugins
parameter_list|)
block|{
name|this
operator|.
name|metricReporterPlugins
operator|=
name|metricReporterPlugins
operator|!=
literal|null
condition|?
name|metricReporterPlugins
else|:
operator|new
name|PluginInfo
index|[
literal|0
index|]
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCounterSupplier
specifier|public
name|MetricsConfigBuilder
name|setCounterSupplier
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|counterSupplier
operator|=
name|info
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMeterSupplier
specifier|public
name|MetricsConfigBuilder
name|setMeterSupplier
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|meterSupplier
operator|=
name|info
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTimerSupplier
specifier|public
name|MetricsConfigBuilder
name|setTimerSupplier
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|timerSupplier
operator|=
name|info
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setHistogramSupplier
specifier|public
name|MetricsConfigBuilder
name|setHistogramSupplier
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|histogramSupplier
operator|=
name|info
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|MetricsConfig
name|build
parameter_list|()
block|{
return|return
operator|new
name|MetricsConfig
argument_list|(
name|metricReporterPlugins
argument_list|,
name|hiddenSysProps
argument_list|,
name|counterSupplier
argument_list|,
name|meterSupplier
argument_list|,
name|timerSupplier
argument_list|,
name|histogramSupplier
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

