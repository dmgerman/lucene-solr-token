begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|solr
operator|.
name|core
operator|.
name|SolrConfig
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
name|SolrCore
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|BadIndexSchemaTest
specifier|public
class|class
name|BadIndexSchemaTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"bad-schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ignoreException
argument_list|(
literal|"_twice"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"ftAgain"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"fAgain"
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|findErrorWithSubstring
specifier|private
name|Throwable
name|findErrorWithSubstring
parameter_list|(
name|List
argument_list|<
name|Throwable
argument_list|>
name|err
parameter_list|,
name|String
name|v
parameter_list|)
block|{
for|for
control|(
name|Throwable
name|t
range|:
name|err
control|)
block|{
if|if
condition|(
name|t
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
name|v
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|t
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|testSevereErrorsForDuplicateNames
specifier|public
name|void
name|testSevereErrorsForDuplicateNames
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
for|for
control|(
name|Throwable
name|t
range|:
name|SolrConfig
operator|.
name|severeErrors
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"got ex:"
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|SolrConfig
operator|.
name|severeErrors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Throwable
argument_list|>
name|err
init|=
operator|new
name|LinkedList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|err
operator|.
name|addAll
argument_list|(
name|SolrConfig
operator|.
name|severeErrors
argument_list|)
expr_stmt|;
name|Throwable
name|t
init|=
name|findErrorWithSubstring
argument_list|(
name|err
argument_list|,
literal|"*_twice"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|err
operator|.
name|remove
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|=
name|findErrorWithSubstring
argument_list|(
name|err
argument_list|,
literal|"ftAgain"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|err
operator|.
name|remove
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|=
name|findErrorWithSubstring
argument_list|(
name|err
argument_list|,
literal|"fAgain"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|err
operator|.
name|remove
argument_list|(
name|t
argument_list|)
expr_stmt|;
comment|// make sure thats all of them
name|assertTrue
argument_list|(
name|err
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

