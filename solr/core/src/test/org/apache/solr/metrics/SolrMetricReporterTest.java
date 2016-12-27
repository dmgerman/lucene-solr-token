begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CoreAdminParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|PluginInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
operator|.
name|reporters
operator|.
name|MockMetricReporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|SolrMetricReporterTest
specifier|public
class|class
name|SolrMetricReporterTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testInit
specifier|public
name|void
name|testInit
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|SolrMetricManager
name|metricManager
init|=
operator|new
name|SolrMetricManager
argument_list|()
decl_stmt|;
specifier|final
name|String
name|registryName
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
decl_stmt|;
specifier|final
name|MockMetricReporter
name|reporter
init|=
operator|new
name|MockMetricReporter
argument_list|(
name|metricManager
argument_list|,
name|registryName
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attrs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|put
argument_list|(
name|FieldType
operator|.
name|CLASS_NAME
argument_list|,
name|MockMetricReporter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|shouldDefineConfigurable
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|String
name|configurable
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldDefineConfigurable
condition|)
name|attrs
operator|.
name|put
argument_list|(
literal|"configurable"
argument_list|,
name|configurable
argument_list|)
expr_stmt|;
name|boolean
name|shouldDefinePlugin
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|PluginInfo
name|pluginInfo
init|=
name|shouldDefinePlugin
condition|?
operator|new
name|PluginInfo
argument_list|(
name|type
argument_list|,
name|attrs
argument_list|)
else|:
literal|null
decl_stmt|;
try|try
block|{
name|reporter
operator|.
name|init
argument_list|(
name|pluginInfo
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pluginInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|configurable
argument_list|,
name|attrs
operator|.
name|get
argument_list|(
literal|"configurable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporter
operator|.
name|didValidate
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|reporter
operator|.
name|configurable
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|configurable
argument_list|,
name|reporter
operator|.
name|configurable
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|pluginInfo
operator|==
literal|null
operator|||
name|attrs
operator|.
name|get
argument_list|(
literal|"configurable"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporter
operator|.
name|didValidate
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|reporter
operator|.
name|configurable
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reporter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
