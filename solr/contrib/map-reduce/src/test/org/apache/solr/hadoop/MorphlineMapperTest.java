begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|LongWritable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mrunit
operator|.
name|mapreduce
operator|.
name|MapDriver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mrunit
operator|.
name|types
operator|.
name|Pair
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
name|Constants
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
name|SolrInputDocument
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
name|hadoop
operator|.
name|morphline
operator|.
name|MorphlineMapper
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

begin_class
DECL|class|MorphlineMapperTest
specifier|public
class|class
name|MorphlineMapperTest
extends|extends
name|MRUnitBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|assumeFalse
argument_list|(
literal|"Does not work on Windows, because it uses UNIX shell commands or POSIX paths"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"FIXME: This test fails under Java 8 due to the Saxon dependency - see SOLR-1301"
argument_list|,
name|Constants
operator|.
name|JRE_IS_MINIMUM_JAVA8
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"FIXME: This test fails under J9 due to the Saxon dependency - see SOLR-1301"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.info"
argument_list|,
literal|"<?>"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM J9"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapper
specifier|public
name|void
name|testMapper
parameter_list|()
throws|throws
name|Exception
block|{
name|MorphlineMapper
name|mapper
init|=
operator|new
name|MorphlineMapper
argument_list|()
decl_stmt|;
name|MapDriver
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|SolrInputDocumentWritable
argument_list|>
name|mapDriver
init|=
name|MapDriver
operator|.
name|newMapDriver
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
empty_stmt|;
name|Configuration
name|config
init|=
name|mapDriver
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|setupHadoopConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|mapDriver
operator|.
name|withInput
argument_list|(
operator|new
name|LongWritable
argument_list|(
literal|0L
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"hdfs://localhost/"
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|DOCUMENTS_DIR
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"+"
argument_list|,
literal|"%20"
argument_list|)
operator|+
literal|"/sample-statuses-20120906-141433.avro"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|sid
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|sid
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"uniqueid1"
argument_list|)
expr_stmt|;
name|sid
operator|.
name|addField
argument_list|(
literal|"user_name"
argument_list|,
literal|"user1"
argument_list|)
expr_stmt|;
name|sid
operator|.
name|addField
argument_list|(
literal|"text"
argument_list|,
literal|"content of record one"
argument_list|)
expr_stmt|;
name|SolrInputDocumentWritable
name|sidw
init|=
operator|new
name|SolrInputDocumentWritable
argument_list|(
name|sid
argument_list|)
decl_stmt|;
name|mapDriver
operator|.
name|withCacheArchive
argument_list|(
name|solrHomeZip
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|withOutput
argument_list|(
operator|new
name|Text
argument_list|(
literal|"0"
argument_list|)
argument_list|,
name|sidw
argument_list|)
expr_stmt|;
comment|//mapDriver.runTest();
name|List
argument_list|<
name|Pair
argument_list|<
name|Text
argument_list|,
name|SolrInputDocumentWritable
argument_list|>
argument_list|>
name|result
init|=
name|mapDriver
operator|.
name|run
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|Text
argument_list|,
name|SolrInputDocumentWritable
argument_list|>
name|p
range|:
name|result
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|p
operator|.
name|getFirst
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|p
operator|.
name|getSecond
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

