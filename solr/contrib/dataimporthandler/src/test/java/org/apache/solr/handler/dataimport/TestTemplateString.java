begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *<p>  * Test for TemplateString  *</p>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestTemplateString
specifier|public
class|class
name|TestTemplateString
block|{
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|VariableResolverImpl
name|vri
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ns
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|ns
operator|.
name|put
argument_list|(
literal|"last_index_time"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|1199429363730l
argument_list|)
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"indexer"
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"select id from subject where last_modified> 1199429363730"
argument_list|,
operator|new
name|TemplateString
argument_list|()
operator|.
name|replaceTokens
argument_list|(
literal|"select id from subject where last_modified> ${indexer.last_index_time}"
argument_list|,
name|vri
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|EMPTY_PROPS
specifier|private
specifier|static
name|Properties
name|EMPTY_PROPS
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
DECL|field|SELECT_WHERE_PATTERN
specifier|private
specifier|static
name|Pattern
name|SELECT_WHERE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(select\\b.*?\\b)(where).*"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

