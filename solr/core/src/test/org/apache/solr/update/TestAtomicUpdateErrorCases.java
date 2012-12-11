begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

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
name|update
operator|.
name|DirectUpdateHandler2
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
name|SolrException
import|;
end_import

begin_class
DECL|class|TestAtomicUpdateErrorCases
specifier|public
class|class
name|TestAtomicUpdateErrorCases
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testUpdateNoTLog
specifier|public
name|void
name|testUpdateNoTLog
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
name|UpdateHandler
name|uh
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"this test requires DirectUpdateHandler2"
argument_list|,
name|uh
operator|instanceof
name|DirectUpdateHandler2
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"this test requires that the updateLog not be enabled, it "
operator|+
literal|"seems that someone modified the configs"
argument_list|,
operator|(
operator|(
name|DirectUpdateHandler2
operator|)
name|uh
operator|)
operator|.
name|getUpdateLog
argument_list|()
argument_list|)
expr_stmt|;
comment|// creating docs should work fine
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_i"
argument_list|,
literal|"42"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ignoreException
argument_list|(
literal|"updateLog"
argument_list|)
expr_stmt|;
comment|// updating docs should fail
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_i"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
operator|-
literal|666
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get error about needing updateLog"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
comment|// if the message doesn't match our expectation, wrap& rethrow
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"unless<updateLog/> is configured"
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"exception message is not expected"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|deleteCore
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testUpdateNoDistribProcessor
specifier|public
name|void
name|testUpdateNoDistribProcessor
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"this test requires an update chain named 'nodistrib'"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"nodistrib"
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating docs should work fine
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_i"
argument_list|,
literal|"42"
argument_list|)
argument_list|,
name|params
argument_list|(
literal|"update.chain"
argument_list|,
literal|"nodistrib"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ignoreException
argument_list|(
literal|"DistributedUpdateProcessorFactory"
argument_list|)
expr_stmt|;
comment|// updating docs should fail
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_i"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
operator|-
literal|666
argument_list|)
argument_list|)
argument_list|,
name|params
argument_list|(
literal|"update.chain"
argument_list|,
literal|"nodistrib"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get error about needing DistributedUpdateProcessorFactory"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
comment|// if the message doesn't match our expectation, wrap& rethrow
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DistributedUpdateProcessorFactory"
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"exception message is not expected"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|deleteCore
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

