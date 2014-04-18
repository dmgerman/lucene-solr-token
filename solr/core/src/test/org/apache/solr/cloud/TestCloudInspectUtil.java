begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|common
operator|.
name|SolrDocument
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
name|SolrDocumentList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|TestCloudInspectUtil
specifier|public
class|class
name|TestCloudInspectUtil
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestCloudInspectUtil
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
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
annotation|@
name|Test
DECL|method|testCheckIfDiffIsLegal
specifier|public
name|void
name|testCheckIfDiffIsLegal
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|addFails
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|deleteFails
init|=
literal|null
decl_stmt|;
name|SolrDocumentList
name|a
init|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|b
init|=
name|getDocList
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|boolean
name|legal
init|=
name|CloudInspectUtil
operator|.
name|checkIfDiffIsLegal
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|"control"
argument_list|,
literal|"cloud"
argument_list|,
name|addFails
argument_list|,
name|deleteFails
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|legal
argument_list|)
expr_stmt|;
comment|// ################################
name|addFails
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|deleteFails
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|a
operator|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|b
operator|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|addFails
operator|.
name|add
argument_list|(
literal|"4"
argument_list|)
expr_stmt|;
name|legal
operator|=
name|CloudInspectUtil
operator|.
name|checkIfDiffIsLegal
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|"control"
argument_list|,
literal|"cloud"
argument_list|,
name|addFails
argument_list|,
name|deleteFails
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|legal
argument_list|)
expr_stmt|;
comment|// ################################
name|addFails
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|deleteFails
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|a
operator|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|b
operator|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|addFails
operator|.
name|add
argument_list|(
literal|"4"
argument_list|)
expr_stmt|;
name|deleteFails
operator|.
name|add
argument_list|(
literal|"5"
argument_list|)
expr_stmt|;
name|legal
operator|=
name|CloudInspectUtil
operator|.
name|checkIfDiffIsLegal
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|"control"
argument_list|,
literal|"cloud"
argument_list|,
name|addFails
argument_list|,
name|deleteFails
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|legal
argument_list|)
expr_stmt|;
comment|// ################################
name|addFails
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|deleteFails
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|a
operator|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|b
operator|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|addFails
operator|.
name|add
argument_list|(
literal|"4"
argument_list|)
expr_stmt|;
name|deleteFails
operator|.
name|add
argument_list|(
literal|"6"
argument_list|)
expr_stmt|;
name|legal
operator|=
name|CloudInspectUtil
operator|.
name|checkIfDiffIsLegal
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|"control"
argument_list|,
literal|"cloud"
argument_list|,
name|addFails
argument_list|,
name|deleteFails
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|legal
argument_list|)
expr_stmt|;
comment|// ################################
name|addFails
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|deleteFails
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|a
operator|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|b
operator|=
name|getDocList
argument_list|(
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
try|try
block|{
name|legal
operator|=
name|CloudInspectUtil
operator|.
name|checkIfDiffIsLegal
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|"control"
argument_list|,
literal|"cloud"
argument_list|,
name|addFails
argument_list|,
name|deleteFails
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception because lists have no diff"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|getDocList
specifier|private
name|SolrDocumentList
name|getDocList
parameter_list|(
name|String
modifier|...
name|ids
parameter_list|)
block|{
name|SolrDocumentList
name|list
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

