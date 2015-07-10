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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|ConcurrentMergeScheduler
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|TieredMergePolicy
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
name|InfoStream
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
name|SolrTestCaseJ4
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
name|handler
operator|.
name|admin
operator|.
name|ShowFileRequestHandler
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
name|IndexSchema
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
name|IndexSchemaFactory
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
name|update
operator|.
name|SolrIndexConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_class
DECL|class|TestConfig
specifier|public
class|class
name|TestConfig
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-test-misc.xml"
argument_list|,
literal|"schema-reversed.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLib
specifier|public
name|void
name|testLib
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrResourceLoader
name|loader
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|InputStream
name|data
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|expectedFiles
init|=
operator|new
name|String
index|[]
block|{
literal|"empty-file-main-lib.txt"
block|,
literal|"empty-file-a1.txt"
block|,
literal|"empty-file-a2.txt"
block|,
literal|"empty-file-b1.txt"
block|,
literal|"empty-file-b2.txt"
block|,
literal|"empty-file-c1.txt"
block|}
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|expectedFiles
control|)
block|{
name|data
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have found file "
operator|+
name|f
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|data
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|unexpectedFiles
init|=
operator|new
name|String
index|[]
block|{
literal|"empty-file-c2.txt"
block|,
literal|"empty-file-d2.txt"
block|}
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|unexpectedFiles
control|)
block|{
name|data
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|data
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/* :NOOP: (un)expected */
block|}
name|assertNull
argument_list|(
literal|"should not have been able to find "
operator|+
name|f
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDisableRequetsHandler
specifier|public
name|void
name|testDisableRequetsHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"disabled"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"enabled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJavaProperty
specifier|public
name|void
name|testJavaProperty
parameter_list|()
block|{
comment|// property values defined in build.xml
name|String
name|s
init|=
name|solrConfig
operator|.
name|get
argument_list|(
literal|"propTest"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"prefix-proptwo-suffix"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|s
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
literal|"propTest/@attr1"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"propone-${literal}"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|s
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
literal|"propTest/@attr2"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default-from-config"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|s
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
literal|"propTest[@attr2='default-from-config']"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"prefix-proptwo-suffix"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|NodeList
name|nl
init|=
operator|(
name|NodeList
operator|)
name|solrConfig
operator|.
name|evaluate
argument_list|(
literal|"propTest"
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nl
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"prefix-proptwo-suffix"
argument_list|,
name|nl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|solrConfig
operator|.
name|getNode
argument_list|(
literal|"propTest"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"prefix-proptwo-suffix"
argument_list|,
name|node
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// sometime if the config referes to old things, it must be replaced with new stuff
annotation|@
name|Test
DECL|method|testAutomaticDeprecationSupport
specifier|public
name|void
name|testAutomaticDeprecationSupport
parameter_list|()
block|{
comment|// make sure the "admin/file" handler is registered
name|ShowFileRequestHandler
name|handler
init|=
operator|(
name|ShowFileRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/admin/file"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"file handler should have been automatically registered"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// If defaults change, add test methods to cover each version
annotation|@
name|Test
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDefaultsTested
init|=
literal|0
decl_stmt|;
name|int
name|numNullDefaults
init|=
literal|0
decl_stmt|;
name|SolrConfig
name|sc
init|=
operator|new
name|SolrConfig
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
argument_list|,
literal|"solrconfig-defaults.xml"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrIndexConfig
name|sic
init|=
name|sc
operator|.
name|indexConfig
decl_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default useCompoundFile"
argument_list|,
literal|false
argument_list|,
name|sic
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default maxBufferedDocs"
argument_list|,
operator|-
literal|1
argument_list|,
name|sic
operator|.
name|maxBufferedDocs
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default maxMergeDocs"
argument_list|,
operator|-
literal|1
argument_list|,
name|sic
operator|.
name|maxMergeDocs
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default mergeFactor"
argument_list|,
operator|-
literal|1
argument_list|,
name|sic
operator|.
name|mergeFactor
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default ramBufferSizeMB"
argument_list|,
literal|100.0D
argument_list|,
name|sic
operator|.
name|ramBufferSizeMB
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default writeLockTimeout"
argument_list|,
operator|-
literal|1
argument_list|,
name|sic
operator|.
name|writeLockTimeout
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default LockType"
argument_list|,
name|SolrIndexConfig
operator|.
name|LOCK_TYPE_NATIVE
argument_list|,
name|sic
operator|.
name|lockType
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default infoStream"
argument_list|,
name|InfoStream
operator|.
name|NO_OUTPUT
argument_list|,
name|sic
operator|.
name|infoStream
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
operator|++
name|numNullDefaults
expr_stmt|;
name|assertNull
argument_list|(
literal|"default mergePolicyInfo"
argument_list|,
name|sic
operator|.
name|mergePolicyInfo
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
operator|++
name|numNullDefaults
expr_stmt|;
name|assertNull
argument_list|(
literal|"default mergeSchedulerInfo"
argument_list|,
name|sic
operator|.
name|mergeSchedulerInfo
argument_list|)
expr_stmt|;
operator|++
name|numDefaultsTested
expr_stmt|;
operator|++
name|numNullDefaults
expr_stmt|;
name|assertNull
argument_list|(
literal|"default mergedSegmentWarmerInfo"
argument_list|,
name|sic
operator|.
name|mergedSegmentWarmerInfo
argument_list|)
expr_stmt|;
name|IndexSchema
name|indexSchema
init|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
literal|"schema.xml"
argument_list|,
name|solrConfig
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|sic
operator|.
name|toIndexWriterConfig
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null mp"
argument_list|,
name|iwc
operator|.
name|getMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"mp is not TMP"
argument_list|,
name|iwc
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|TieredMergePolicy
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"null ms"
argument_list|,
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ms is not CMS"
argument_list|,
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
operator|instanceof
name|ConcurrentMergeScheduler
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"non-null mergedSegmentWarmer"
argument_list|,
name|iwc
operator|.
name|getMergedSegmentWarmer
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDefaultsMapped
init|=
name|sic
operator|.
name|toMap
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"numDefaultsTested vs. numDefaultsMapped+numNullDefaults ="
operator|+
name|sic
operator|.
name|toMap
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|,
name|numDefaultsTested
argument_list|,
name|numDefaultsMapped
operator|+
name|numNullDefaults
argument_list|)
expr_stmt|;
block|}
comment|// sanity check that sys propertis are working as expected
DECL|method|testSanityCheckTestSysPropsAreUsed
specifier|public
name|void
name|testSanityCheckTestSysPropsAreUsed
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrConfig
name|sc
init|=
operator|new
name|SolrConfig
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
argument_list|,
literal|"solrconfig-basic.xml"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrIndexConfig
name|sic
init|=
name|sc
operator|.
name|indexConfig
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ramBufferSizeMB sysprop"
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.ramBufferSizeMB"
argument_list|)
argument_list|)
argument_list|,
name|sic
operator|.
name|ramBufferSizeMB
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"useCompoundFile sysprop"
argument_list|,
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"useCompoundFile"
argument_list|)
argument_list|)
argument_list|,
name|sic
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

